from .preprocess import Preprocessor

import pandas as pd # type: ignore
from sklearn.metrics.pairwise import cosine_similarity
import pickle
import Levenshtein
from sklearn.feature_extraction.text import TfidfVectorizer
from txtai.embeddings import Embeddings
import json
import os
import re

#chuyển folder vị trí đang chạy về folder search_engine
original_dir = os.getcwd()
def find_search_engine_path():
    # Lấy đường dẫn tuyệt đối của thư mục hiện tại
    current_directory = os.getcwd()

    # Duyệt qua tất cả các thư mục và tệp tin trong cây thư mục bắt đầu từ thư mục hiện tại
    for dirpath, dirnames, filenames in os.walk(current_directory):
        # Kiểm tra xem thư mục hiện tại có tên là "search_engine" không
        if "search_engine" in dirnames:
            return os.path.join(dirpath, "search_engine")
        
    # Trả về None nếu không tìm thấy thư mục "search_engine" trong cây thư mục
    return None

os.chdir(find_search_engine_path())

# khởi tạo các tệp đã lưu
preprocessor = Preprocessor() #preprocessor
#đọc file csv
relative_path = "../../resources/data/all_data.csv"
preprocessed_path = "../../resources/data/preprocessed_data.csv"
df = pd.read_csv(relative_path)
preprocessed_df = pd.read_csv(preprocessed_path)
#đọc tfidf
tfidf_matrix = pickle.load(open("models/tfidf/tfidf_matrix.sav", 'rb'))
vectorizer = pickle.load(open("models/tfidf/vectorizer.sav", 'rb'))

title_tfidf_matrix = pickle.load(open("models/tfidf/title_tfidf_matrix.sav", 'rb'))
title_vectorizer = pickle.load(open("models/tfidf/title_vectorizer.sav", 'rb'))
#đọc txtai
embeddings = Embeddings()
embeddings.load('models/txtai_embeddings.model')
# về thư mục gốc
os.chdir(original_dir)

class QueryHandler:
    def __init__(self) -> None:
        return None

    def find_closest_word(self, word, model_name='Tfidf'):
        if model_name == 'TxtAI':
            return word
        
        model = vectorizer
        if model_name == 'Title_Tfidf':
            model = title_vectorizer

        vocab = model.vocabulary_
        
        try:
            # Kiểm tra xem từ có trong từ điển không
            if word in vocab:
                return word
            else:
                for vocab_word in vocab:
                    if vocab_word.startswith(word):
                        return vocab_word                    
                # Tìm từ gần nhất trong từ điển sử dụng Levenshtein distance
                closest_word = min(vocab, key=lambda x: Levenshtein.distance(word, x))
                return closest_word
    
        except KeyError:
            # Trong trường hợp từ không tồn tại trong vocab
            return word

    def preprocess_query(self, query, model_name='Tfidf'):
        if model_name == 'TxtAI':
            return query
        
        original_query = query
        query = preprocessor.preprocess_text(query)
        query = ' '.join(self.find_closest_word(word=word, model_name=model_name) for word in query.split())
        if query == '':
            query = ' '.join(self.find_closest_word(word=word, model_name=model_name) for word in original_query.split())
        
        return query

    def word_query(self, query, model_name='Tfidf', not_including=[]):

        preprocessed_query = self.preprocess_query(query=query, model_name=model_name)
        if model_name == 'Tfidf':

            query_vector = vectorizer.transform([preprocessed_query])
            similarities = cosine_similarity(query_vector, tfidf_matrix)[0]
            similarities = list(zip(range(len(similarities)), similarities)) #chuyển về tuple

        elif model_name == 'Title_Tfidf':

            query_vector = title_vectorizer.transform([preprocessed_query])
            similarities = cosine_similarity(query_vector, title_tfidf_matrix)[0]
            similarities = list(zip(range(len(similarities)), similarities)) #chuyển về tuple

        
        elif model_name == 'TxtAI':
            similarities = embeddings.search(preprocessed_query, limit=500)

        results = []
        for idx, sim in similarities:
            if sim > 0.05:

                if len(not_including) > 0 and any(word.lower() in str(preprocessed_df.iloc[idx][' Content']) or
                                    word.lower() in str(preprocessed_df.iloc[idx][' Article title']) or
                                    word.lower() in str(preprocessed_df.iloc[idx][' Author']) or
                                    word.lower() in str(preprocessed_df.iloc[idx][' Tags']) or
                                    word.lower() in str(preprocessed_df.iloc[idx][' Category']) for word in not_including):
                    continue    

                result = {
                    "similarity score": sim,
                    "article": {
                        "Article link": df.iloc[idx]['Article link'],
                        "Website source": df.iloc[idx][' Website source'],
                        "Article type": df.iloc[idx][' Article type'],
                        "Article title": df.iloc[idx][' Article title'],
                        "Content": df.iloc[idx][' Content'],
                        "Creation date": df.iloc[idx][' Creation date'],
                        "Author": df.iloc[idx][' Author'],
                        "Tags": df.iloc[idx][' Tags'],
                        "Category": df.iloc[idx][' Category'],
                        "Summary": df.iloc[idx][' Summary']
                    }

                    
                }
                results.append(result)

        return preprocessed_query, results

    def parse_query(self, query_string):
    
        terms = re.split(r'\s+or\s+|\s+Or\s+|\s+OR\s+', query_string)
        
        queries = []
        not_including = []

        for term in terms:
            words = term.split()
            for word in words:
                if word.startswith('-') and len(word):
                    not_including.append(word[1:])
                else:
                    queries.append(word)
        
        return queries, not_including
    
    def query(self, query_string, by_title=False, semantic_search=False):
        model_name = 'Tfidf'
        if by_title:
            model_name = 'Title_Tfidf'
        if semantic_search:
            model_name = 'TxtAI'
        
        queries, not_including = self.parse_query(query_string)

        all_results = []
        all_preprocessed_words = []
        for word in queries:
            preprocessed_word, results = self.word_query(word, model_name=model_name, not_including=not_including)
            all_results.extend(results)    
            all_preprocessed_words.append(preprocessed_word)
        
        all_results.sort(key=lambda x: x['similarity score'], reverse=True)
        all_results = all_results[:500]
        suggested_query = ' '.join(all_preprocessed_words)


        response = {
            "query": query_string,
            "sugggested query": suggested_query,
            "isNull": (len(all_results) == 0),
            "results": all_results,
        }

        return json.dumps(response)

# if __name__ == '__main__':
#     st = 'elon - twitter or solana'
#     queryhandler = QueryHandler()
#     queries, not_including = queryhandler.parse_query(st)
#     print(not_including)
    
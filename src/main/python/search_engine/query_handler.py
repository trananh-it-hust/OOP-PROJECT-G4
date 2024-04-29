from .preprocess import Preprocessor

import pandas as pd # type: ignore
from sklearn.metrics.pairwise import cosine_similarity
from joblib import load
import Levenshtein
from sklearn.feature_extraction.text import TfidfVectorizer
import json
import os

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
relative_path = "../../resources/data/data.csv"
df = pd.read_csv(relative_path)
#đọc tfidf
tfidf_matrix = load("models/tfidf/tfidf_matrix.joblib")
vectorizer = load("models/tfidf/vectorizer.joblib")
# về thư mục gốc
os.chdir(original_dir)

class QueryHandler:
    def __init__(self) -> None:
        return None;

    def find_closest_word(self, word, model=vectorizer, model_name='Tfidf'):
        if model_name == 'Doc2Vec':
            vocab = model.wv.key_to_index
        elif model_name == 'Tfidf':
            vocab = model.vocabulary_
        try:
            # Kiểm tra xem từ có trong từ điển không
            if word in vocab:
                return word
            else:
                # Tìm từ gần nhất trong từ điển sử dụng Levenshtein distance
                closest_word = min(vocab, key=lambda x: Levenshtein.distance(word, x))
                return closest_word
    
        except KeyError:
            # Trong trường hợp từ không tồn tại trong vocab
            return None

    def preprocess_query(self, query):
        query = preprocessor.preprocess_text(query)
        query = ' '.join(self.find_closest_word(word=word) for word in query.split())
        
        return query;

    def query(self, query, model_name='Tfidf'):

        preprocessed_query = self.preprocess_query(query=query)
        if model_name == 'Tfidf':

            query_vector = vectorizer.transform([preprocessed_query])
            similarities = cosine_similarity(query_vector, tfidf_matrix)

        # Bước 6: Sắp xếp và hiển thị kết quả
        results = []
        for idx, sim in enumerate(similarities[0]):
            if sim > 0.1:
                result = {
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
                    },
                    "similarity score": sim,
                    
                }
                results.append(result)


        results.sort(key=lambda x: x['similarity score'], reverse=True)

        response = {
            "query": query,
            "results": results,
            "sugggested query": preprocessed_query,
            "isNull": (len(results) == 0)
        }

        return json.dumps(response)

    


    
    
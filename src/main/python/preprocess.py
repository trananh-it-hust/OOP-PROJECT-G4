import pandas as pd
import re
import json
import spacy
from spacy.matcher import Matcher
from dateutil.parser import parse



nlp = spacy.load("en_core_web_sm")
matcher = Matcher(nlp.vocab)

date_pattern = [{"LIKE_NUM": True}, {"LOWER": {"REGEX": "(january|february|march|april|may|june|july|august|september|october|november|december)"}}]

matcher.add("DATE", [date_pattern])

with open('../resources/assets/stopwords.txt') as file:
    stopwords = file.read().splitlines()

with open('../resources/assets/lemmatization-en.json') as file:
    lemmatizer = json.load(file)

class Preprocessor():

    def __init__(self) -> None:
        pass



    def clean_text(self, text):
        text = str(text)
        words = text.split("\n")
        # Loại bỏ khoảng trắng và chuỗi trống sau khi tách
        words = [' '.join(word.split()) for word in words]
        words = [word.strip() for word in words if word.strip()]
        # Kết hợp các từ thành một chuỗi
        text = " ".join(words)

        text = text.lower()  # Chuyển đổi về chữ thường
        text = re.sub(r'[^\w\s]', '', text)  # Loại bỏ dấu câu

        return text

    def split_numbers_from_characters(self, text):
        parts = re.split('(\d+)', text)
        #tách các số ra khỏi từ. Ví dụ "i3 a4" -> [i, 3, a, 4]
        if len(parts) > 1: 
            return ' '.join(parts)
        
        text = str(text)

        return text

    def remove_stopwords(self, text):
        return ' '.join(word for word in text.split() if word not in stopwords)
    
    def lemmatize_text(self, text):
        return ' '.join([lemmatizer.get(word, word) for word in text.split()])

    def preprocess_text(self,text):
        text = self.clean_text(text=text)
        text = self.split_numbers_from_characters(text)

        text = self.remove_stopwords(text=text)
        text = self.lemmatize_text(text=text)

        # Kết hợp các từ thành một chuỗi

        return text


    def preprocess_date(self, text):
        doc = nlp(text)
        date_phrases = []
        current_phrase = []

        for token in doc:
            # Lặp qua từng token trong doc, kiểm tra token có phải dạng date không
            if token.ent_type_ == "DATE":
                current_phrase.append(token.text)
            
            elif current_phrase:
                date_str = " ".join(current_phrase)
                try:
                    date = parse(date_str, fuzzy=True)
                    if date:
                        date_phrases.append(date)
                except ValueError:
                    pass  # Bỏ qua nếu gặp lỗi trong quá trình chuyển đổi
                current_phrase = []

        if current_phrase:
            date_str = " ".join(current_phrase)
            try:
                date = parse(date_str, fuzzy=True)
                if date:
                    date_phrases.append(date)
            except ValueError:
                pass  # Bỏ qua nếu gặp lỗi trong quá trình chuyển đổi
        
        if date_phrases:
            return max(date_phrases)
        
        return None


# if __name__ == '__main__':
    
#     preprocess = Preprocessor()
#     print(preprocess.preprocess_text(text=text))
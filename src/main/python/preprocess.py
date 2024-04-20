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

# with open('../resources/assets/stopwords.txt') as file:
#     stopwords = file.read().splitlines()




def clean_text(text):
    text = str(text)
    words = text.split("\n")
    # Loại bỏ khoảng trắng và chuỗi trống sau khi tách
    words = [word.strip() for word in words if word.strip()]
    # Kết hợp các từ thành một chuỗi
    text = " ".join(words)

    text = text.lower()  # Chuyển đổi về chữ thường
    text = re.sub(r'[^\w\s]', '', text)  # Loại bỏ dấu câu
    return text

def split_numbers_from_characters(text):
    parts = re.split('(\d+)', text)
    #tách các số ra khỏi từ. Ví dụ "i3 a4" -> [i, 3, a, 4]
    if len(parts) > 1: 
        return ' '.join(parts)
    
    return text

def remove_stopwords(text):
    return ' '.join(word for word in text.split() if word not in stopwords)

def preprocess_text(text):
    text = clean_text(text)
    text = split_numbers_from_characters(text)
    text = remove_stopwords(text)

    return text


def preprocess_date(text):
    doc = nlp(text)
    date_phrases = []
    current_phrase = []

    for token in doc:
        #lặp qua từng token trong doc, kiểm tra token có phải dạng date không
        if token.ent_type_ == "DATE":
            current_phrase.append(token.text)
        
        elif current_phrase:
            date_str = " ".join(current_phrase)
            date = parse(date_str, fuzzy=True)
            if date:
                date_phrases.append(date)
            current_phrase = []

    if current_phrase:
        date_str = " ".join(current_phrase)
        date = parse(date_str, fuzzy=True)
        if date:
            date_phrases.append(date)
    
    if date_phrases:
        return max(date_phrases)
    
    return None


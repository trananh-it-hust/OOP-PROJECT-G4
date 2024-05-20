from flask import Flask, request, jsonify
import requests
import spacy
app = Flask(__name__)

# Tải mô hình NLP đã huấn luyện của spaCy
nlp_ner = spacy.load("OOP-PROJECT-G4/src/main/python/_NER/model-best")# OOP-PROJECT-G4\src\main\python\_NER\model-best

# Định nghĩa hàm predict
@app.route('/predict', methods=['POST'])
def predict():
    # Nhận dữ liệu đầu vào từ yêu cầu
    data = request.data.decode('utf-8')
    doc = nlp_ner(data)
    ans = {}
    for ent in doc.ents:
        ans[ent.label_] = []
    for ent in doc.ents:
        ans[ent.label_].append((ent.start_char, ent.end_char, ent.text))
    return jsonify({'entities': ans})
if __name__ == '__main__':
    app.run(debug=True)
    print("Server end")
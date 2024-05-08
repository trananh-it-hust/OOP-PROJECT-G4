from flask import Flask, request, jsonify
import spacy
app = Flask(__name__)

# Tải mô hình NLP đã huấn luyện của spaCy
nlp_ner = spacy.load("./src/main/python/NER/model-best")

# Định nghĩa hàm predict
@app.route('/predict', methods=['POST'])
def predict():
    # Nhận dữ liệu đầu vào từ yêu cầu
    data = request.args.get('text')
    # Xử lý văn bản và dự đoán
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
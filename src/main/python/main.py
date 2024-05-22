from flask import Flask, request, jsonify
from search_engine.query_handler import QueryHandler
import time
import spacy
from Server_trả_gợi_ý import handle_time
from Server_trả_gợi_ý import handle

app = Flask(__name__)

# API search
@app.route('/search', methods=['GET'])
def search():
    query = request.args.get('q')
    by_title = request.args.get('byTitle', 'False').lower() in ['true', '1', 't', 'y', 'yes']
    semantic_search = request.args.get('semanticSearch', 'False').lower() in ['true', '1', 't', 'y', 'yes']

    query_handler = QueryHandler()
    return query_handler.query(query_string=query, by_title=by_title, semantic_search=semantic_search)

# API suggestion
@app.route('/suggestion', methods=['GET'])
def suggestion():
    data = request.args.get('data')
    ket_qua_tim_kiem = []
    time_limit=0.6
    suggestion_limit=5
    start_time = time.time()
    handle.handle(data,ket_qua_tim_kiem)
    return jsonify({'result':ket_qua_tim_kiem} )

#NER
nlp_ner = spacy.load("./src/main/python/NER/model-best")
@app.route('/predict', methods=['POST'])
def predict():
    # Nhận dữ liệu đầu vào từ yêu cầu
    data = request.data.decode('utf-8')
    # Xử lý văn bản và dự đoán
    doc = nlp_ner(data)
    ans = {}
    for ent in doc.ents:
        ans[ent.label_] = []
    for ent in doc.ents:
        ans[ent.label_].append((ent.start_char, ent.end_char, ent.text))
    return jsonify({'entities': ans})

if __name__ == '__main__':
    app.run(debug=True, port=8000)
    print('Server end') 


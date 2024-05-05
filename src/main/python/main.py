from flask import Flask, request, jsonify
from search_engine.query_handler import QueryHandler
import time
from Server_trả_gợi_ý import handle_time
from Server_trả_gợi_ý import handle

app = Flask(__name__)

# API search
@app.route('/search', methods=['GET'])
def search():
    query = request.args.get('q')
    query_handler = QueryHandler()

    return query_handler.query(query=query)

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

if __name__ == '__main__':
    app.run(debug=True, port=8000)
    print('Server end')
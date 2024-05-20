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

#NER
nlp_ner = spacy.load(r"OOP-PROJECT-G4\src\main\python\_NER\model-best")
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

#Trends detection
import json
import pathlib
import textwrap
import google.generativeai as genai
#decypt gemini_api_key
'''
type this in git bash: 
gpg --output "OOP-PROJECT-G4/src/main/python/TrendDetect/Gemini_api_key.txt" --decrypt "OOP-PROJECT-G4/src/main/python/TrendDetect/Gemini_api_key.txt" 
if it doesn't work, try direct path instead of relative path
'''
gemini_api_key = open("OOP-PROJECT-G4/src/main/python/TrendDetect/Gemini_api_key.txt", "r").read()
genai.configure(api_key=gemini_api_key)
# prompt instruction 
sys_instruction = '''To complete the task, you need to follow these steps:
                       1. Read the document
                       2. detect trends in articles
                       3. With each trends, explain reasoning by giving exact citations from articles.
                       4. Output a Json file of trends. Each elements include {name of trend, reasoning, citations}: { {trend1, reasoning, citations}, ...  }
                  '''
persona = "You are an expert in natural language processing. Your task is detecting trends in articles."
constraints = "The number of trends must not exceed 3. Keep the answer explicit, clear and easy to transfrom to list or JSON format"
context = "articles or blogs about Blockchain-related technologies"
examples = '''<Example> INPUT:  'Circular is applying Blockchain technology for logistics' ,
                        OUTPUT :{{'trend': 'Blokchain for logistics',
                                  'reason': Circular company is developing logistics due to blockchain,
                                  'citations': ['Circular is applying Blockchain technology for logistics'] }}
               </Example>'''
reasoning = "Explain your reasoning step-by-step"
responseFormat = "JSON"
prompt =[ persona,  sys_instruction, constraints, context,examples,  reasoning, responseFormat]
#load model 
model = genai.GenerativeModel(
    model_name="models/gemini-1.5-pro-latest",
    system_instruction=prompt
)
# define trends detection function
@app.route('/detect', methods=['POST'])
def detect():
    # Nhận dữ liệu đầu vào từ yêu cầu
    data = request.data.decode('utf-8')
    response = model.generate_content(data)
    ans = response.text.replace('\n', '') # remove escape sequences
    formatted_ans = ans[7:-3] # remove " '''json " at the beginning and " ''' " at the end
    res = json.loads(formatted_ans)
    return jsonify({'trends' : res})

if __name__ == '__main__':
    app.run(debug=True, port=8000)
    print('Server end') 


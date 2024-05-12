# Named entity recognition model using Spacy 
This model use Spacy to recognize entities in document. The entities will be classified in these tags: 
- PER: person 
- ORG: organization 
- LOC: location
- DATE: date
- DATA_AI: termilogies about Data and Artificial Intelligence
- BLOCKCHAIN: termilogies about Blockchain technology 
- OTHER_IT_TERMILOGIES: other termilogies about IT 
- FINANCE: termilogies about finance, such as currency, trading, transaction,...
- CONFERENCE: conference

## Resources 
The training data will be collected by two ways: 
- The termilogies that we seek about each tags ( NER0.json ) 
- Data crawled for project ( other .json files)

## Models: 
The spacy model is created and trained in the .ipynb files. Then, it was saved in two folder "model-best" and "model-last". 

## API: 
API is created in API.py

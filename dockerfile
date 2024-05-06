FROM  python
COPY . /testdocker
WORKDIR /testdocker
RUN pip install -r requirements.txt
CMD ["python", "-u", "OOP-PROJECT-G4/src/main/python/main.py"]
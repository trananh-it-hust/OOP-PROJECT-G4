import csv
import time

def handle_time(tu_khoa,ket_qua_tim_kiem,suggestion_limit,time_limit,start_time):
    if tu_khoa=='':
        ket_qua_tim_kiem=[]
        return

                
    with open('./src/main/resources/data/data.csv', mode='r', encoding='utf-8') as file:
        csv_file = csv.reader(file)
        for row in csv_file:
            row_ug=row[0].split('/')
            row_ug=row_ug[len(row_ug)-1]
            row_ug=row_ug.split('-')
            row_ug=' '.join(row_ug)
            row_ug=row_ug.lower()
            row_ug=row_ug.split(tu_khoa)
            if len(row_ug)==1:
                if len(ket_qua_tim_kiem)>suggestion_limit:
                    break
                else:
                    continue
            else:
                row_ug=row_ug[1]
                e=row_ug.split(" ")
                if len(e)==1 or e[0]!='':
                    row_ug=tu_khoa+e[0]
                else:
                    row_ug=tu_khoa+e[0]+' '+e[1]
                if row_ug in ket_qua_tim_kiem:
                    continue
                else:
                    ket_qua_tim_kiem.append(row_ug)
            if len(ket_qua_tim_kiem)>=suggestion_limit:
                break    
    
    if len(ket_qua_tim_kiem) < suggestion_limit and time.time()-start_time<time_limit:
        with open('./src/main/resources/data/data.csv', mode='r', encoding='utf-8') as file:
            csv_file = csv.reader(file)
            for row in csv_file:
                row_ug=row[3].split('.')
                row_ug=' '.join(row_ug)
                row_ug=row_ug.lower()
                row_ug=row_ug.split(tu_khoa)
                if len(row_ug)==1:
                    if len(ket_qua_tim_kiem)>5:
                        break
                    else:
                        continue
                else:
                    row_ug=row_ug[1]
                    e=row_ug.split(" ")
                    if len(e)==1 or e[0]!='':
                        row_ug=tu_khoa+e[0]
                    else:
                        row_ug=tu_khoa+e[0]+' '+e[1]
                    if row_ug in ket_qua_tim_kiem:
                        continue
                    else:
                        ket_qua_tim_kiem.append(row_ug)
                if len(ket_qua_tim_kiem)>=suggestion_limit:
                    break         
    if len(ket_qua_tim_kiem) < suggestion_limit and time.time()-start_time<time_limit:
        with open('./src/main/resources/data/data.csv', mode='r', encoding='utf-8') as file:
            csv_file = csv.reader(file)
            for row in csv_file:
                row_ug=row[4].split('.')
                row_ug=' '.join(row_ug)
                row_ug=row_ug.lower()
                row_ug=row_ug.split(tu_khoa)
                if len(row_ug)==1:
                    if len(ket_qua_tim_kiem)>suggestion_limit:
                        break
                    else:
                        continue
                else:
                    row_ug=row_ug[1]
                    e=row_ug.split(" ")
                    if len(e)==1 or e[0]!='':
                        row_ug=tu_khoa+e[0]
                    else:
                        row_ug=tu_khoa+e[0]+' '+e[1]
                    if row_ug in ket_qua_tim_kiem:
                        continue
                    else:
                        ket_qua_tim_kiem.append(row_ug)
                if len(ket_qua_tim_kiem)>suggestion_limit:
                    break  
    print(ket_qua_tim_kiem)   
    print(time.time()-start_time)
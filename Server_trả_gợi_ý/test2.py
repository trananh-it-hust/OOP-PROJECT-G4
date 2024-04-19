
from http.server import SimpleHTTPRequestHandler, HTTPServer
import json
from urllib.parse import urlparse, parse_qs
import csv
class MyHttpRequestHandler(SimpleHTTPRequestHandler):
    def do_GET(self):
        # Phân tích URL
        parsed_path = urlparse(self.path)
        # Lấy các tham số truy vấn dưới dạng từ điển
        
        query_params = parse_qs(parsed_path.query)
        
            # Xử lý yêu cầu GET dựa trên đường dẫn
        if parsed_path.path == '/':
            try:    # Lấy dữ liệu từ tham số 'name' nếu có
                tu_khoa = query_params.get('data', [''])[0]  # Trả về chuỗi rỗng nếu không tìm thấy 'name'
                print(tu_khoa)
                # Xử lý dữ liệu
                ket_qua_tim_kiem = []
                link = [] # Danh sách để lưu trữ các hàng tìm thấy
                with open('data.csv', mode='r', encoding='utf-8') as file:
                    csv_file = csv.reader(file)
                    for row in csv_file:
                        row_ug=row[0].split('/')
                        row_ug=row_ug[len(row_ug)-1]
                        row_ug=row_ug.split('-')
                        row_ug=' '.join(row_ug)
                        row_ug=row_ug.lower()
                        row_ug=row_ug.split(tu_khoa)
                        if len(row_ug)==1:
                            if len(ket_qua_tim_kiem)>10:
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
                        if len(ket_qua_tim_kiem)>10:
                            break    
                
                if len(ket_qua_tim_kiem) < 10:
                    with open('data.csv', mode='r', encoding='utf-8') as file:
                        csv_file = csv.reader(file)
                        for row in csv_file:
                            row_ug=row[3].split('.')
                            row_ug=' '.join(row_ug)
                            row_ug=row_ug.lower()
                            row_ug=row_ug.split(tu_khoa)
                            if len(row_ug)==1:
                                if len(ket_qua_tim_kiem)>10:
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
                            if len(ket_qua_tim_kiem)>10:
                                break         
                if len(ket_qua_tim_kiem) < 10:
                    with open('data.csv', mode='r', encoding='utf-8') as file:
                        csv_file = csv.reader(file)
                        for row in csv_file:
                            row_ug=row[4].split('.')
                            row_ug=' '.join(row_ug)
                            row_ug=row_ug.lower()
                            row_ug=row_ug.split(tu_khoa)
                            if len(row_ug)==1:
                                if len(ket_qua_tim_kiem)>10:
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
                            if len(ket_qua_tim_kiem)>10:
                                break  
                print(ket_qua_tim_kiem)    
                print(link)

                #gửiiiii
                data = {
                    "du_lieu": ket_qua_tim_kiem,
                }
                # Chuyển đổi dữ liệu thành chuỗi JSON
                json_string = json.dumps(data)

                # Gửi phản hồi HTTP
                self.send_response(200)
                self.send_header('Content-type', 'application/json')
                self.end_headers()

                # Gửi chuỗi JSON
                self.wfile.write(json_string.encode('utf-8'))
            except json.JSONDecodeError as e:
            # Gửi phản hồi lỗi nếu dữ liệu không phải là JSON hợp lệ
                self.send_response(400)
                self.send_header('Content-type', 'application/json')
                self.end_headers()
                response = {
                    "status": "error",
                    "message": "Dữ liệu không phải là JSON hợp lệ: " + str(e)
                }
                self.wfile.write(json.dumps(response).encode('utf-8'))
# Thiết lập địa chỉ và cổng cho server
hostName = "localhost"
serverPort = 8000

# Tạo server HTTP
if __name__ == "__main__":
    webServer = HTTPServer((hostName, serverPort), MyHttpRequestHandler)
    print("Server đã bắt đầu tại http://%s:%s" % (hostName, serverPort))

    # Chạy server mãi mãi cho đến khi nhận được tín hiệu ngắt
    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass

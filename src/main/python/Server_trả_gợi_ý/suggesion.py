
from http.server import SimpleHTTPRequestHandler, HTTPServer
import json
from urllib.parse import urlparse, parse_qs
import csv
from handle import handle
class MyHttpRequestHandler(SimpleHTTPRequestHandler):
    def do_GET(self):
        # Phân tích URL
        parsed_path = urlparse(self.path)
        # Lấy các tham số truy vấn dưới dạng từ điển
        
        query_params = parse_qs(parsed_path.query)
        
            # Xử lý yêu cầu GET dựa trên đường dẫn
        if parsed_path.path == '/suggestions':
            try:    # Lấy dữ liệu từ tham số 'name' nếu có
                tu_khoa = query_params.get('data', [''])[0]  # Trả về chuỗi rỗng nếu không tìm thấy 'data'
                print(tu_khoa)
                ket_qua_tim_kiem = []
                # Xử lý dữ liệu
                handle(tu_khoa,ket_qua_tim_kiem)

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
                # end_time = time.time()
                # elapsed_time = end_time - start_time
                # print(elapsed_time)
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

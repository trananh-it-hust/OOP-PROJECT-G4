from .query_handler import QueryHandler

from http.server import BaseHTTPRequestHandler
import json

from urllib.parse import urlparse, parse_qs
import time

query_handler = QueryHandler()

class QueryAPIHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        # Phân tích URL
        parsed_path = urlparse(self.path)
        # Lấy các tham số truy vấn dưới dạng từ điển
        
        query_params = parse_qs(parsed_path.query)
        
            # Xử lý yêu cầu GET dựa trên đường dẫn
        if parsed_path.path == '/search':
            start_time = time.time()

            try:    # Lấy dữ liệu từ tham số 'q' nếu có
                query = query_params.get('q', [''])[0]  # Trả về chuỗi rỗng nếu không tìm thấy 'q'
                print(query)
                
                
                # Xử lý dữ liệu
                response = query_handler.query(query)

                # Gửi phản hồi HTTP
                self.send_response(200)
                self.send_header('Content-type', 'application/json')
                self.end_headers()

                # Gửi chuỗi JSON
                self.wfile.write(response.encode('utf-8'))
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
        
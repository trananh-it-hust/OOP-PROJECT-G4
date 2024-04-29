from search_engine.query import QueryAPIHandler
from http.server import HTTPServer

# Thiết lập địa chỉ và cổng cho server
hostName = "localhost"
serverPort = 8000

# Tạo server HTTP
if __name__ == "__main__":
    webServer = HTTPServer((hostName, serverPort), QueryAPIHandler)
    print("Server đã bắt đầu tại http://%s:%s" % (hostName, serverPort))

    # Chạy server mãi mãi cho đến khi nhận được tín hiệu ngắt
    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass
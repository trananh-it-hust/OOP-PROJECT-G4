import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                // Tạo kết nối socket mới mỗi lần nhập truy vấn
                try (Socket socket = new Socket("localhost", 8000);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

                    System.out.print("Nhập query (nhập 'exit' để thoát): ");
                    String query = input.nextLine();

                    // Gửi truy vấn đến server
                    writer.println(query);

                    // Kiểm tra điều kiện thoát
                    if (query.equals("exit")) {
                        break;
                    }

                    // Đợi và nhận kết quả từ server
                    String response = waitForResponse(reader);
                    System.out.println("Kết quả từ server: " + response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String waitForResponse(BufferedReader reader) throws IOException {
        // Đợi và nhận kết quả từ server
        long startTime = System.currentTimeMillis();
        while (!reader.ready()) {
            // Kiểm tra xem đã vượt quá thời gian chờ chưa
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime > 10000) { // Chờ tối đa 10 giây
                return "Timeout: không nhận được kết quả từ server.";
            }
        }
        return reader.readLine();
    }
}
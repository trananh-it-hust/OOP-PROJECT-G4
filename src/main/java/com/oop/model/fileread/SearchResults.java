package com.oop.model.fileread;

import com.oop.model.fileread.CSVFileReader;
import com.oop.model.Item;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.List;

public class SearchResults extends Application {
    private boolean continueOpeningLinks = true;
    private List<Item> itemList; // Danh sách các item

    @Override
    public void start(Stage primaryStage) {
        // Load danh sách item từ CSV hoặc từ một nguồn khác
        CSVFileReader csvReader = new CSVFileReader();
        itemList = csvReader.getAllItems();

        // Tạo một VBox để chứa các itemNode
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);

        // Tạo và thêm các itemNode vào VBox
        for (Item item : itemList) {
            VBox itemNode = createItemNode(item);
            root.getChildren().add(itemNode);
        }

        // Đặt VBox vào một ScrollPane để cuộn nếu có quá nhiều ItemNode
        ScrollPane scrollPane = new ScrollPane(root);

        // Tạo Scene và đặt ScrollPane làm nội dung
        Scene scene = new Scene(scrollPane, 800, 600);

        // Đặt Scene vào Stage và hiển thị Stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Search Results");
        primaryStage.show();
    }

    // Phương thức này tạo một VBox chứa các UI Elements tương ứng với mỗi Item
    private VBox createItemNode(Item item) {
        // Tạo các Text cho các trường thông tin của Item
        Hyperlink hyperlink = new Hyperlink(item.getArticleLink());
        hyperlink.setOnAction(event -> openWebView(item.getArticleLink()));

        Text title = new Text(item.getArticleTitle());
        Text source = new Text("Source: " + item.getWebsiteSource());
        Text date = new Text("Date: " + item.getCreationDate());

        // Tạo TextFlow cho nội dung và giới hạn độ dài
        TextFlow content = createTextFlow(item.getContent());

        // Giới hạn độ dài của nội dung và thêm "..." nếu cần
        limitTextLength(content, 2);

        // Tạo một VBox để chứa các phần tử hiển thị thông tin của Item
        VBox itemNode = new VBox(title, hyperlink, source, date, content);
        itemNode.setSpacing(5); // Đặt khoảng cách giữa các phần tử
        itemNode.setPadding(new Insets(5)); // Đặt padding cho itemNode

        return itemNode;
    }

    // Phương thức này tạo một TextFlow từ nội dung và giới hạn độ dài
    private TextFlow createTextFlow(String content) {
        TextFlow textFlow = new TextFlow();
        Text text = new Text(content);
        //double sceneWidth = scene.getWidth();
        // Đặt chiều rộng tối đa để tự động xuống dòng
        textFlow.getChildren().add(text);
        textFlow.setMaxWidth(800);
        return textFlow;
    }

    // Phương thức giới hạn độ dài của TextFlow theo số dòng
    // Phương thức này giới hạn độ dài của TextFlow theo số dòng
    // Phương thức giới hạn độ dài của TextFlow theo số dòng
    private void limitTextLength(TextFlow textFlow, int maxLines) {
        double textFlowWidth = textFlow.getMaxWidth(); // Lấy chiều rộng tối đa của TextFlow
        double textHeight = 0.0; // Chiều cao của văn bản trong TextFlow

        int lineCount = 0; // Số dòng hiện tại

        for (Node node : textFlow.getChildren()) {
            if (node instanceof Text) {
                Text text = (Text) node;
                // Thiết lập chiều rộng tối đa cho Text để lấy kích thước của nó
                text.setWrappingWidth(800);
                textHeight += text.getBoundsInLocal().getHeight(); // Tính tổng chiều cao của văn bản
                lineCount++;
            }
        }

        // Giới hạn số dòng và thêm "..." nếu cần
        if (lineCount > maxLines) {
            // Xóa các dòng thừa
            int removeIndex = maxLines;
            textFlow.getChildren().remove(removeIndex, textFlow.getChildren().size());

            // Thêm "..."
            Text lastLine = new Text("..."); // Tạo Text mới chứa "..."
            textFlow.getChildren().add(lastLine);
        }
    }


    // Phương thức mở WebView khi nhấp vào Hyperlink
    private void openWebView(String url) {
        // Mở URL trong WebView
        // Lưu ý: WebView cần phải được khởi tạo trước khi sử dụng
        WebView webView = new WebView();
        webView.getEngine().load(url);

        // Tạo cửa sổ Stage để hiển thị WebView
        Stage webViewStage = new Stage();
        webViewStage.setScene(new Scene(webView, 800, 600));
        webViewStage.setTitle("Web Page");
        webViewStage.showAndWait(); // Đợi cho đến khi cửa sổ WebView được đóng

        // Kiểm tra nếu người dùng đã đóng cửa sổ WebView
        if (!continueOpeningLinks) {
            // Nếu người dùng không muốn mở nữa, hủy vòng lặp
            return;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

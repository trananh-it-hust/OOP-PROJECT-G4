package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import com.oop.model.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import com.oop.model.APICaller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

public class SearchController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private VBox suggestions;

    @FXML
    private VBox searchResult;
    @FXML
    private Button nextPage;
    @FXML
    private Button prevPage;

    @FXML
    private ChoiceBox<String> categorySort;

    // Danh sách tiêu chí sắp xếp
    private ObservableList<String> criteriaList = FXCollections.observableArrayList("Ngày đăng giảm dần");

    // Tham chiếu tới Stage hiện tại
    private Stage stage;

    // Tham chiếu tới Scene hiện tại
    private Scene scene;

    // Tham chiếu tới Parent root của Scene hiện tại
    private Parent root;

    // Dữ liệu tìm kiếm được nhập từ ô tìm kiếm
    private String searchQuery;

    // Danh sách các mục được trả về từ API
    private ArrayList<Item> itemList;
    static private int countPageNumber = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Thêm các tiêu chí sắp xếp vào ChoiceBox
        categorySort.setItems(criteriaList);
    }

    // Xử lý sự kiện khi lựa chọn tiêu chí sắp xếp thay đổi
    public void setChoice(ActionEvent event) {
        String choiceChosen = categorySort.getValue();
        // Xử lý tiêu chí đã chọn
        if (choiceChosen != null) {
            switch (choiceChosen) {
                case "Ngày đăng giảm dần":
                    // Sắp xếp danh sách theo ngày giảm dần
                    filterByUpdateDateDescending();
                    break;
                // Thêm các trường hợp khác nếu cần
            }
        }
    }

    // Sắp xếp danh sách theo ngày giảm dần
    public void filterByUpdateDateDescending() {
        Comparator<Item> dateComparator = Comparator.comparing(Item::getCreationDate).reversed();
        // Sắp xếp danh sách theo ngày giảm dần
        Collections.sort(itemList, dateComparator);
        // Hiển thị danh sách đã sắp xếp
        addSearchResult(itemList);
    }

    // Chuyển đến màn hình chính
    public void switchToMain(Event event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Chuyển đến kết quả tìm kiếm
    public void switchToSearchResults(Event event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/SearchResults.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Thêm các gợi ý tìm kiếm
    public void addSuggestions(List<String> suggestionsResult) throws IOException {
        suggestions.getChildren().clear(); // Xóa các gợi ý trước
        for (String suggestion : suggestionsResult) {
            // Tạo các gợi ý và xử lý sự kiện khi được chọn
            VBox suggestionField = new VBox();
            Label suggestionLabel = new Label(suggestion);
            suggestionField.getChildren().add(suggestionLabel);
            suggestionField.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    searchField.setText(suggestionLabel.getText());
                    try {
                        switchToSearchResults(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            // Hiển thị hiệu ứng khi di chuột qua các gợi ý
            suggestionField.setOnMouseEntered(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    suggestionField.setStyle("-fx-border-color: #808080;-fx-background-color: #F0F8FF;");
                }
            });
            suggestionField.setOnMouseExited(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    suggestionField.setStyle("-fx-border-color: transparent;-fx-background-color: transparent;");
                }
            });
            suggestions.getChildren().add(suggestionField); // Thêm gợi ý vào VBox
        }
        suggestions.setCursor(Cursor.HAND); // Đặt con trỏ chuột thành kiểu tay
    }

    @FXML
    private void nextPage(ActionEvent event) {
        // Tăng biến countPageNumber lên 1 nếu chưa là trang cuối cùng
        if (countPageNumber < itemList.size() / 10) {
            countPageNumber++;
            // Hiển thị lại dữ liệu mới
            addSearchResult(itemList);
        }
    }

    @FXML
    private void prevPage(ActionEvent event) {
        // Giảm biến countPageNumber đi 1 nếu không phải là trang đầu tiên
        if (countPageNumber > 1) {
            countPageNumber--;
            // Hiển thị lại dữ liệu mới
            addSearchResult(itemList);
        }
    }

    // Thêm kết quả tìm kiếm
    private void addSearchResult(ArrayList<Item> itemList) {
        searchResult.getChildren().clear(); // Xóa kết quả tìm kiếm trước đó
        int startIndex = 10 * (countPageNumber - 1);
        int endIndex = Math.min(10 * countPageNumber, itemList.size()); // Đảm bảo không vượt quá số lượng mục trong itemList
        for (int i = startIndex; i < endIndex; i++) {
            // Tạo giao diện cho mỗi mục kết quả tìm kiếm
            VBox itemNode = createItemNode(itemList.get(i));
            searchResult.getChildren().add(itemNode);
        }
    }


    // Tạo giao diện cho mỗi mục kết quả tìm kiếm
    private VBox createItemNode(Item item) {
        // Tạo các thành phần giao diện cho mỗi mục kết quả tìm kiếm
        Hyperlink hyperlink = new Hyperlink(item.getArticleLink());
        hyperlink.setOnAction(event -> openWebView(item.getArticleLink()));

        Text title = new Text(item.getArticleTitle());
        Text source = new Text("Source: " + item.getWebsiteSource());
        Text date = new Text("Date: " + item.getCreationDate());

        TextFlow content = createTextFlow(item.getContent());
        limitTextLength(content, 2);

        VBox itemNode = new VBox(title, hyperlink, source, date, content);
        itemNode.setSpacing(5);
        itemNode.setPadding(new Insets(5));

        return itemNode;
    }

    // Tạo TextFlow cho nội dung
    private TextFlow createTextFlow(String content) {
        TextFlow textFlow = new TextFlow();
        Text text = new Text(content);
        textFlow.getChildren().add(text);
        textFlow.setMaxWidth(800); // Đặt chiều rộng tối đa
        return textFlow;
    }

    // Giới hạn độ dài của nội dung và thêm "..." nếu cần
    private void limitTextLength(TextFlow textFlow, int maxLines) {
        double textFlowWidth = textFlow.getMaxWidth();
        double textHeight = 0.0;
        int lineCount = 0;

        for (Node node : textFlow.getChildren()) {
            if (node instanceof Text) {
                Text text = (Text) node;
                text.setWrappingWidth(textFlowWidth);
                textHeight += text.getBoundsInLocal().getHeight();
                lineCount++;
            }
        }

        if (lineCount > maxLines) {
            int removeIndex = maxLines;
            textFlow.getChildren().remove(removeIndex, textFlow.getChildren().size());
            Text lastLine = new Text("...");
            textFlow.getChildren().add(lastLine);
        }
    }

    // Xử lý sự kiện khi ô tìm kiếm được sử dụng
    public void initialize() {
        searchField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    // Chuyển đến kết quả tìm kiếm khi nhấn Enter
                    try {
                        switchToSearchResults(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Hiển thị gợi ý tìm kiếm khi nhập
                    String searchQuery = searchField.getText();
                    List<String> suggestionsResults = new ArrayList<String>();
                    try {
                        suggestionsResults = APICaller.querySuggest(searchQuery);
                    } catch (URISyntaxException | IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        addSuggestions(suggestionsResults);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Mở liên kết trong WebView
    private void openWebView(String url) {
        WebView webView = new WebView();
        webView.getEngine().load(url);
        Stage webViewStage = new Stage();
        webViewStage.setScene(new Scene(webView, 800, 600));
        webViewStage.setTitle("Web Page");
        webViewStage.show();
    }

    // Cài đặt nội dung cho ô tìm kiếm
    public void setSearchText(String searchText) {
        searchField.setText(searchText);
    }
}

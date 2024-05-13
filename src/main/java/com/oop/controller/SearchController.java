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
    private VBox searchResults;
    @FXML
    private Button nextPage;
    @FXML
    private Button prevPage;

    @FXML
    private ChoiceBox<String> categorySort;
    @FXML
    private Text currentPage;
    @FXML
    Text categoryText;
    private ObservableList<String> criteriaList = FXCollections.observableArrayList("Descending post date");

    private Stage stage;

    private Scene scene;

    private Parent root;

    private String searchQuery;

    private ArrayList<Item> itemList;
    static private int countPageNumber = 1;

    public void setChoice(ActionEvent event) {
        String choiceChosen = categorySort.getValue();
        if (choiceChosen != null) {
            switch (choiceChosen) {
                case "Descending posting date":
                    filterByUpdateDateDescending();
                    break;
            }
        }
    }

    public void filterByUpdateDateDescending() {
        Comparator<Item> dateComparator = Comparator.comparing(Item::getCreationDate).reversed();
        Collections.sort(itemList, dateComparator);
        addSearchResult(itemList);
    }

    public void switchToMain(Event event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSearchResults(Event event) throws IOException {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SearchResults.fxml"));
            root = loader.load();
            SearchController searchController = loader.getController();
            searchController.setSearchText(searchText);
            searchController.initialize(null, null);
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please enter something in the search box before clicking search!");
            alert.showAndWait();
        }
    }

    public void addSuggestions(List<String> suggestionsResult) throws IOException {
        suggestions.getChildren().clear();
        for (String suggestion : suggestionsResult) {
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
            suggestions.getChildren().add(suggestionField);
        }
        suggestions.setCursor(Cursor.HAND);
    }

    @FXML
    private void nextPage(ActionEvent event) {
        if (countPageNumber < itemList.size() / 10) {
            countPageNumber++;
            addSearchResult(itemList);
            currentPage.setText("Page: " + countPageNumber);
        }
    }

    @FXML
    private void prevPage(ActionEvent event) {
        if (countPageNumber > 1) {
            countPageNumber--;
            addSearchResult(itemList);
            currentPage.setText("Page: " + countPageNumber);
        }
    }

    private void addSearchResult(List<Item> itemList) {
        searchResults.getChildren().clear();
        int startIndex = 10 * (countPageNumber - 1);
        int endIndex = Math.min(10 * countPageNumber, itemList.size());
        VBox scrollableContent = new VBox(); // Tạo VBox để chứa nội dung cuộn
        for (int i = startIndex; i < endIndex; i++) {
            VBox itemNode = createItemNode(itemList.get(i));
            scrollableContent.getChildren().add(itemNode);
        }
        // Tạo ScrollPane và đặt nội dung là VBox chứa các item
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(scrollableContent);
        // Đặt chiều rộng và chiều cao cho ScrollPane
        scrollPane.setPrefWidth(800); // Đặt chiều rộng tuỳ ý
        scrollPane.setPrefHeight(400); // Đặt chiều cao tuỳ ý
        searchResults.getChildren().add(scrollPane); // Thêm ScrollPane vào VBox searchResults
    }

    private VBox createItemNode(Item item) {
        Hyperlink hyperlink = new Hyperlink(item.getArticleLink());
        hyperlink.setOnAction(event -> openWebView(item.getArticleLink()));

        Text title = new Text(item.getArticleTitle());
        Text source = new Text("Source: " + item.getWebsiteSource());
        Text date = new Text("Date: " + item.getCreationDate());

        TextFlow content = createTextFlow(item.getContent());

        VBox itemNode = new VBox(title, hyperlink, source, date, content);
        itemNode.setSpacing(5);
        itemNode.setPadding(new Insets(5));

        return itemNode;
    }

    private TextFlow createTextFlow(String content) {
        TextFlow textFlow = new TextFlow();
        Text text = new Text(content);
        textFlow.getChildren().add(text);
        textFlow.setMaxWidth(800);
        limitTextLength(textFlow, 2);
        return textFlow;
    }

    private void limitTextLength(TextFlow textFlow, int maxLines) {
        double textFlowWidth = textFlow.getMaxWidth();
        double totalTextHeight = 0.0;
        int lineCount = 0;

        // Xác định tổng chiều cao của văn bản
        for (Node node : textFlow.getChildren()) {
            if (node instanceof Text) {
                Text text = (Text) node;
                text.setWrappingWidth(textFlowWidth);
                totalTextHeight += text.getBoundsInLocal().getHeight();
                lineCount++;
            }
        }

        // Nếu có nhiều hơn 2 dòng, cắt đi và chỉ hiển thị 2 dòng đầu tiên
        if (lineCount > maxLines) {
            double maxHeight = textFlow.getChildren().get(0).getBoundsInLocal().getHeight() * maxLines;
            double currentHeight = 0.0;
            int i = 0;
            for (Node node : textFlow.getChildren()) {
                if (currentHeight + node.getBoundsInLocal().getHeight() <= maxHeight) {
                    currentHeight += node.getBoundsInLocal().getHeight();
                } else {
                    break;
                }
                i++;
            }
            // Xóa các nút thừa ra khỏi textFlow
            if (i < textFlow.getChildren().size()) {
                textFlow.getChildren().remove(i, textFlow.getChildren().size());
                // Thêm dấu ... vào cuối
                Text lastLine = new Text("...");
                textFlow.getChildren().add(lastLine);
            }
        }
    }

    public void getData() throws ParseException, IOException, URISyntaxException {
        List<Item> resultSearch = APICaller.getSearchResult(searchField.getText());
        addSearchResult(resultSearch);
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("1st");
        categorySort.setItems(criteriaList);
        categoryText.setText("Category");
        currentPage.setText("Page: " + (countPageNumber));
        try {
            getData();
        } catch (ParseException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    try {
                        switchToSearchResults(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
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

    private void openWebView(String url) {
        WebView webView = new WebView();
        webView.getEngine().load(url);
        Stage webViewStage = new Stage();
        webViewStage.setScene(new Scene(webView, 800, 600));
        webViewStage.setTitle("Web Page");
        webViewStage.show();
    }

    public void setSearchText(String searchText) {
        searchField.setText(searchText);
    }
}

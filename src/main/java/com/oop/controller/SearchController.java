package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import com.oop.model.NetWorkException;
import com.oop.model.Item;
import com.oop.sorter.DateSorter;
import com.oop.sorter.TitleSorter;
import com.opencsv.exceptions.CsvValidationException;
import com.oop.sorter.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private ChoiceBox<String> categorySort;
    @FXML
    private Text currentPage;
    @FXML
    private Text categoryText;
    private final int totalResultsPerPage = 10;
    private final ObservableList<String> criteriaList = FXCollections.observableArrayList(
            "Sort by title",
            "Sort by date");

    private Stage stage;

    private Scene scene;

    private Parent root;

    private ArrayList<Item> searchResultList;

    private int pageNumber = 1;

    @FXML
    private void handleSortCriteriaChange() {
        String selectedCriteria = categorySort.getValue();
        if (selectedCriteria.equals("Sort by title")) {
            // Sắp xếp dữ liệu theo tiêu đề bài viết
            TitleSorter sorter = new TitleSorter();
            searchResultList = (ArrayList<Item>) sorter.sort(searchResultList);
            addSearchResult(searchResultList);
        } else if (selectedCriteria.equals("Sort by date")) {
            // Sắp xếp dữ liệu theo ngày đăng
            DateSorter sorter = new DateSorter();
            searchResultList = (ArrayList<Item>) sorter.sort(searchResultList);
            addSearchResult(searchResultList);
        }
        // Có thể thêm các tiêu chí sắp xếp khác ở đây
    }

    public void goHomePage(Event event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Main.fxml"))); // Đảm bảo đối tượng
                                                                                                   // truyền vào không
                                                                                                   // phải là null
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void goDetailPage(Event event, Item item)
            throws IOException, CsvValidationException, java.text.ParseException, URISyntaxException, ParseException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Detail.fxml"));
        root = loader.load();
        DetailController detailController = loader.getController();
        detailController.setItem(item);
        detailController.initialize();
        detailController.setPageNumberReturn(this.pageNumber);
        detailController.setSearchQueryReturn(this.searchField.getText());
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void continueSearch(Event event) throws IOException {
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
            suggestionField
                    .setStyle("-fx-background-color: rgb(15, 76, 117);-fx-text-fill: rgb(255, 255, 255);");
            suggestionField.getChildren().add(suggestionLabel);
            suggestionField.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    searchField.setText(suggestionLabel.getText());
                    try {
                        continueSearch(event);
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
                    suggestionField.setStyle(
                            "-fx-background-color: rgb(15, 76, 117);-fx-text-fill: rgb(255, 255, 255);");
                }
            });
            suggestionField.setOnMouseEntered((EventHandler<Event>) event -> suggestionField
                    .setStyle("-fx-border-color: #808080;-fx-background-color: #F0F8FF;"));
            suggestionField.setOnMouseExited((EventHandler<Event>) event -> suggestionField
                    .setStyle("-fx-border-color: transparent;-fx-background-color: transparent;"));
            suggestions.getChildren().add(suggestionField);
        }
        suggestions.setCursor(Cursor.HAND);
    }

    @FXML
    private void nextPage() {
        if (pageNumber < searchResultList.size() / totalResultsPerPage) {
            pageNumber++;
            addSearchResult(searchResultList);
            currentPage.setText("" + pageNumber + "/" + Math.max(searchResultList.size() / totalResultsPerPage, 1));

        }
    }

    @FXML
    private void prevPage() {
        if (pageNumber > 1) {
            pageNumber--;
            addSearchResult(searchResultList);
            currentPage.setText("" + pageNumber + "/" + Math.max(searchResultList.size() / totalResultsPerPage, 1));
        }
    }

    private void addSearchResult(List<Item> itemList) {
        searchResults.getChildren().clear();
        int startIndex = totalResultsPerPage * (pageNumber - 1);
        int endIndex = Math.min(totalResultsPerPage * pageNumber, itemList.size());
        VBox scrollableContent = new VBox(); // Tạo VBox để chứa nội dung cuộn
        for (int i = startIndex; i < endIndex; i++) {
            VBox itemNode = createItemNode(itemList.get(i));
            itemNode.getStyleClass().add("itemNode");
            scrollableContent.getChildren().add(itemNode);
        }
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(scrollableContent);
        scrollPane.setStyle("-fx-padding: 10px 0px 0px 10px;-fx-boder-width: 0px !important;");
        scrollPane.setPrefWidth(478);
        scrollPane.setPrefHeight(780);
        searchResults.getChildren().add(scrollPane);
    }

    private VBox createItemNode(Item item) {
        Hyperlink hyperlink = new Hyperlink(item.getArticleLink());
        hyperlink.setOnAction(event -> openWebView(item.getArticleLink()));
        //
        TextFlow title = createTextFlow(item.getArticleTitle());
        title.getStyleClass().add("title");
        //
        String dateTimeString = item.getCreationDate().toString();
        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, originalFormatter);
        DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = dateTime.format(newFormatter);
        Text date = new Text("" + formattedDate);
        date.getStyleClass().add("date");
        //
        TextFlow content = createTextFlow(
                item.getContent().substring(0, Math.min(item.getContent().length(), 250)) + " ...");
        content.setMinWidth(740);
        content.getStyleClass().add("content");
        //
        Button detailButton = new Button("Detail");
        detailButton.setStyle(
                "-fx-background-color: rgb(15, 76, 117); -fx-text-fill: rgb(187, 225, 250); -fx-font-weight: bold;");
        //
        detailButton.setOnAction(event -> {
            try {
                goDetailPage(event, item);
            } catch (IOException | CsvValidationException | java.text.ParseException | URISyntaxException
                    | ParseException e) {
                e.printStackTrace();
            }
        });
        VBox itemNode = new VBox(title, hyperlink, date, content, detailButton);
        itemNode.getStyleClass().add("itemNode");
        itemNode.setSpacing(5);
        itemNode.setPadding(new Insets(5));
        return itemNode;
    }

    private TextFlow createTextFlow(String content) {
        TextFlow textFlow = new TextFlow();
        content = content + ".....";
        Text text = new Text(content);
        textFlow.getChildren().add(text);
        textFlow.setMaxWidth(450);
        return textFlow;
    }

    public void getData() throws ParseException, IOException, URISyntaxException {
        searchResultList = (ArrayList<Item>) APICaller.getSearchResult(searchField.getText());
        addSearchResult(searchResultList);
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchField.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    continueSearch(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Gợi ý tìm kiếm dựa trên nội dung của ô tìm kiếm
                String searchQuery = searchField.getText();
                try {
                    List<String> suggestionsResults = APICaller.querySuggest(searchQuery);
                    addSuggestions(suggestionsResults);
                } catch (URISyntaxException | IOException | ParseException | NetWorkException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            getData();
            setupUIComponents();
        } catch (ParseException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void setupUIComponents() {
        categorySort.setItems(criteriaList);
        categoryText.setText("Category");
        currentPage.setText("" + pageNumber + "/" + Math.max(searchResultList.size() / totalResultsPerPage, 1));
    }

    private void openWebView(String url) {
        try {
            APICaller.checkConnectNetWork();
        } catch (NetWorkException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please check your connection and try again!");
            alert.showAndWait();
            throw new RuntimeException();
        }
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

    public void setSearchPage(int pageBefore) {
        this.pageNumber = pageBefore;
    }
}
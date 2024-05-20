package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import com.oop.model.NetWorkException;
import com.oop.model.Item;
import com.opencsv.exceptions.CsvValidationException;

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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

public class SearchController extends ASearchPage implements Initializable {

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
    private final ObservableList<String> criteriaList = FXCollections.observableArrayList("Descending post date");

    private Stage stage;

    private Scene scene;

    private Parent root;

    private ArrayList<Item> searchResultList;

    private int PageNumber = 1;

    public void setChoice() {
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
        searchResultList.sort(dateComparator);
        addSearchResult(searchResultList);
    }

    public void goHomePage(Event event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Main.fxml"))); //Đảm bảo đối tượng truyền vào không phải là null
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
        detailController.setSearchReturn(this.searchField.getText());
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

    @Override
    public void addSuggestions(List<String> suggestionsResult) throws IOException {
        suggestions.getChildren().clear();
        for (String suggestion : suggestionsResult) {
            VBox suggestionField = new VBox();
            Label suggestionLabel = new Label(suggestion);
            suggestionField.getChildren().add(suggestionLabel);
            suggestionField.setOnMouseClicked(event -> {
                searchField.setText(suggestionLabel.getText());
                try {
                    continueSearch(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            suggestionField.setOnMouseEntered((EventHandler<Event>) event -> suggestionField.setStyle("-fx-border-color: #808080;-fx-background-color: #F0F8FF;"));
            suggestionField.setOnMouseExited((EventHandler<Event>) event -> suggestionField.setStyle("-fx-border-color: transparent;-fx-background-color: transparent;"));
            suggestions.getChildren().add(suggestionField);
        }
        suggestions.setCursor(Cursor.HAND);
    }

    @FXML
    private void nextPage() {
        if (PageNumber < searchResultList.size() / totalResultsPerPage) {
            PageNumber++;
            addSearchResult(searchResultList);
            currentPage.setText("Page: " + PageNumber);

        }
    }

    @FXML
    private void prevPage() {
        if (PageNumber > 1) {
            PageNumber--;
            addSearchResult(searchResultList);
            currentPage.setText("Page: " + PageNumber);
        }
    }

    private void addSearchResult(List<Item> itemList) {
        searchResults.getChildren().clear();
        int startIndex = totalResultsPerPage * (PageNumber - 1);
        int endIndex = Math.min(totalResultsPerPage * PageNumber, itemList.size());
        VBox scrollableContent = new VBox(); // Tạo VBox để chứa nội dung cuộn
        for (int i = startIndex; i < endIndex; i++) {
            VBox itemNode = createItemNode(itemList.get(i));
            scrollableContent.getChildren().add(itemNode);
        }
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(scrollableContent);
        scrollPane.setPrefWidth(485);
        scrollPane.setPrefHeight(250);
        searchResults.getChildren().add(scrollPane);
    }

    private VBox createItemNode(Item item) {
        Hyperlink hyperlink = new Hyperlink(item.getArticleLink());
        hyperlink.setOnAction(event -> openWebView(item.getArticleLink()));
        Text title = new Text(item.getArticleTitle());
        Text source = new Text("Source: " + item.getWebsiteSource());
        Text date = new Text("Date: " + item.getCreationDate());
        TextFlow content = createTextFlow(item.getContent().substring(0, Math.min(item.getContent().length(), 100)));
        Button detailButton = new Button("Detail");
        detailButton.setOnAction(event -> {
            try {
                goDetailPage(event, item);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CsvValidationException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        VBox itemNode = new VBox(title, hyperlink, source, date, content, detailButton);
        itemNode.getStyleClass().add("itemNode");
        itemNode.setSpacing(5);
        itemNode.setPadding(new Insets(5));
        return itemNode;
    }

    private TextFlow createTextFlow(String content) {
        TextFlow textFlow = new TextFlow();
        content = content+".....";
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
        searchField.setOnKeyPressed(event -> {
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
        currentPage.setText("Page: " + PageNumber);
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
}
package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import com.oop.model.NetWorkException;
import com.oop.model.Item;
import com.oop.sorter.DateSorter;
import com.oop.sorter.TitleSorter;
import com.opencsv.exceptions.CsvValidationException;
import com.oop.sorter.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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

public class SearchController extends BaseController implements Initializable  {

    @FXML
    private TextField searchField;
    @FXML
    private Button prevPageButton;
    @FXML
    private Button nextPageButton;
    @FXML
    private VBox suggestions;
    @FXML
    private Button homePage;
    @FXML
    private VBox searchResults;
    @FXML
    private ChoiceBox<String> categorySort;
    @FXML
    private Button continueButton;
    @FXML
    private Text currentPage;
    @FXML
    private Text categoryText;
    private final int totalResultsPerPage = 10;
    private final ObservableList<String> criteriaList = FXCollections.observableArrayList
            (
                    "Sort by title",
                    "Sort by date",
                    "Sort by author");

    private ArrayList<Item> searchResultList;

    private int pageNumber = 1;

    @FXML
    private void handleSortCriteriaChange() {
        String selectedCriteria = categorySort.getValue();
        switch (selectedCriteria) {
            case "Sort by title": {
                TitleSorter sorter = new TitleSorter();
                searchResultList = (ArrayList<Item>) sorter.sort(searchResultList);
                addSearchResult(searchResultList);
                break;
            }
            case "Sort by date": {
                DateSorter sorter = new DateSorter();
                searchResultList = (ArrayList<Item>) sorter.sort(searchResultList);
                addSearchResult(searchResultList);
                break;
            }
            case "Sort by author": {
                AuthorSorter sorter = new AuthorSorter();
                searchResultList = (ArrayList<Item>) sorter.sort(searchResultList);
                addSearchResult(searchResultList);
                break;
            }
        }
    }


    public void addSuggestions(List<String> suggestionsResult) throws IOException {
        suggestions.getChildren().clear();
        for (String suggestion : suggestionsResult) {
            VBox suggestionField = new VBox();
            Label suggestionLabel = new Label(suggestion);
            suggestionField.getChildren().add(suggestionLabel);
            suggestionField.setOnMouseClicked(event -> {
                searchField.setText(suggestionLabel.getText());
                try {
                    SwitchController.goSearchPage(this,event);
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


    private void addSearchResult(List<Item> itemList) {
        searchResults.getChildren().clear();
        int startIndex = totalResultsPerPage * (pageNumber - 1);
        int endIndex = Math.min(totalResultsPerPage * pageNumber, itemList.size());
        VBox scrollableContent = new VBox();
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
                SwitchController.goDetailPage(this, event, item, this.pageNumber, this.searchField.getText());
            } catch (IOException | CsvValidationException | java.text.ParseException | URISyntaxException |
                     ParseException e) {
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
                    SwitchController.goSearchPage(this,event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
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
        currentPage.setText("Page: " + pageNumber);
        categorySort.setOnAction(event -> handleSortCriteriaChange());
        homePage.setOnAction(event -> {
            try {
                SwitchController.goHomePage(this,event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        continueButton.setOnAction(event -> {
            try{
                SwitchController.goSearchPage(this,event);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
        nextPageButton.setOnAction(event -> {
            if (pageNumber < searchResultList.size() / totalResultsPerPage) {
                pageNumber++;
                addSearchResult(searchResultList);
                currentPage.setText("Page: " + pageNumber);

            }
        });
        prevPageButton.setOnAction(event -> {
            if (pageNumber > 1) {
                pageNumber--;
                addSearchResult(searchResultList);
                currentPage.setText("Page: " + pageNumber);
            }
        });
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
    @Override
    public TextField getSearchField(){
        return this.searchField;
    }
}
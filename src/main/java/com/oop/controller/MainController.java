package com.oop.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;


import com.oop.model.Item;
import com.oop.model.SearchModel;
import com.opencsv.exceptions.CsvValidationException;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private TextField searchField;

    @FXML
    private VBox infoData;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToMain(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToDetail(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Detail.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSearchResults(Event event) throws IOException, CsvValidationException, ParseException {
        List<Item> searchResults = handleSearch();
        root = FXMLLoader.load(getClass().getResource("/view/SearchResults.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        for (Item item : searchResults) {
            VBox itemCard = Item.renderUI(item);
            ((VBox) scene.lookup("#searchResults")).getChildren().add(itemCard);
        }
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public List<Item> handleSearch() throws CsvValidationException, IOException, ParseException {
        String searchQuery = searchField.getText();
        System.out.println("Search query: " + searchQuery);
        List<Item> items = Item.readItemsFromCSV();
        List<Item> searchResults = SearchModel.searchByTitle(items, searchQuery);
        return searchResults;
    }

    @FXML
    public void addSuggestions(List<String> suggestions) throws IOException {
        VBox suggestionsBox;
        if (scene != null) {
            suggestionsBox = (VBox) scene.lookup("#suggestions");
            suggestionsBox.getChildren().clear();
        } else {
            root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
            scene = new Scene(root);
            suggestionsBox = (VBox) scene.lookup("#suggestions");
            suggestionsBox.getChildren().clear();
        }
        for (String suggestion : suggestions) {
            System.out.println("Suggestion: " + suggestion);
            VBox suggestionField = new VBox();
            Label suggestionLabel = new Label("a" + suggestion);
            suggestionField.getChildren().add(suggestionLabel);
            suggestionsBox.getChildren().add(suggestionField);
        }
    }

    public void getInfoData() throws CsvValidationException, IOException, ParseException {
        List<Item> items = Item.readItemsFromCSV();
        int artTypeNewsArticleCount = 0;
        for (Item item : items) {
            if (item.getArticleType().equals("News Article")) {
                artTypeNewsArticleCount++;
            }
        }
        Label infoLabel = new Label("Number of items: " + items.size());
        Label newsArticleLabel = new Label("Number of News Articles: " + artTypeNewsArticleCount);
        infoData.getChildren().add(infoLabel);
        infoData.getChildren().add(newsArticleLabel);
    }

    public void initialize() throws CsvValidationException, IOException, ParseException {
        // Thêm lắng nghe sự kiện cho TextField khi scene được tạo
        searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                // Kiểm tra nếu phím được ấn là phím Enter
                if (event.getCode().equals(KeyCode.ENTER)) {
                    try {
                        switchToSearchResults(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (CsvValidationException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    String searchQuery = searchField.getText();
                    List<String> suggestionsResults = SearchModel.GetSuggestion(searchQuery);
                    try {
                        addSuggestions(suggestionsResults);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // Thêm lắng nghe sự kiện cho Pane khi scene được tạo
        // getInfoData();
    }
}

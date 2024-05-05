package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.oop.model.APICaller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchController {

    @FXML
    private TextField searchField;

    @FXML
    private VBox suggestions;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToMain(Event event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSearchResults(Event event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/SearchResults.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void addSuggestions(List<String> suggestionsResult) throws IOException {
        suggestions.getChildren().clear();
        for (String suggestion : suggestionsResult) {
            System.out.println("Suggestion: " + suggestion);
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

    public void initialize() {
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
                    }
                } else {
                    String searchQuery = searchField.getText();
                    List<String> suggestionsResults = new ArrayList<String>();
                    try {
                        suggestionsResults = APICaller.querySuggest(searchQuery);
                    } catch (URISyntaxException | IOException e) {
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
}

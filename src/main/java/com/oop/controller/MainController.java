package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.oop.model.NetWorkException;
import javafx.scene.control.Button;
import org.json.simple.parser.ParseException;

import com.oop.model.APICaller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.Cursor;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;


public class MainController extends BaseController{

    @FXML
    private TextField searchField;

    @FXML
    private VBox suggestions;
    @FXML
    private Button searchButton;
    public void addSuggestions(List<String> suggestionsResult) throws IOException {
        suggestions.getChildren().clear();
        for (String suggestion : suggestionsResult) {
            System.out.println("Suggestion: " + suggestion);
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

    public void initialize() throws IOException, ParseException {
        searchField.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    SwitchController.goSearchPage(this,event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                String searchQuery = searchField.getText();
                List<String> suggestionsResults = new ArrayList<>();
                try {
                    suggestionsResults = APICaller.querySuggest(searchQuery);
                } catch (URISyntaxException | IOException | ParseException | NetWorkException e) {
                    e.printStackTrace();
                }
                try {
                    addSuggestions(suggestionsResults);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        searchButton.setOnAction(event -> {
            try {
                SwitchController.goSearchPage(this,event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public TextField getSearchField() {
        return this.searchField;
    }
}

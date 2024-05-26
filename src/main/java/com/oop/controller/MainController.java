package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.oop.model.NetWorkException;
import javafx.scene.control.Button;
import org.json.simple.parser.ParseException;

import com.oop.model.APICaller;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.Cursor;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import javafx.scene.layout.VBox;

public class MainController extends BaseController {

    private String searchQuery;

    private static final int IDLE_TIMEOUT = 500;
    private Timeline idleTimeline;
    private String lastSearchQuery;

    @FXML
    private TextField searchField;

    @FXML
    private VBox suggestions;
    @FXML
    private Button searchContainer;

    public void addSuggestions(List<String> suggestionsResult) throws IOException {
        suggestions.getChildren().clear();
        for (String suggestion : suggestionsResult) {
            VBox suggestionField = new VBox();
            Label suggestionLabel = new Label(suggestion);
            suggestionField.getChildren().add(suggestionLabel);
            suggestionField.setStyle(
                    "-fx-padding:5px;-fx-font-size: 15px;-fx-border-color: rgb(15, 76,117);-fx-border-width: 0px 0px 1px 0px;-fx-border-radius: 0px 0px 10px 10px;");
            suggestionField.setOnMouseClicked(event -> {
                searchField.setText(suggestionLabel.getText());
                try {
                    SwitchController.goSearchPage(this, event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            suggestionField.setOnMouseEntered(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    suggestionField.setStyle(
                            "-fx-background-color: #dae7f3;-fx-background-radius: 10px;-fx-padding:5px;-fx-font-size: 15px;-fx-border-color: rgb(15, 76,117);-fx-border-width: 0px 1px 1px 0px;-fx-border-radius: 0px 0px 10px 10px;");
                }
            });
            suggestionField.setOnMouseExited(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    suggestionField.setStyle(
                            "-fx-padding:5px;-fx-font-size: 15px;-fx-border-color: rgb(15, 76,117);-fx-border-width: 0px 0px 1px 0px;-fx-border-radius: 0px 0px 10px 10px;");
                }
            });
            suggestions.getChildren().add(suggestionField);
        }
        suggestions.setCursor(Cursor.HAND);
    }

    private void handleIdleEvent() throws NetWorkException {
        String searchQuery = searchField.getText();
        if (searchQuery.equals(lastSearchQuery)) {
            idleTimeline.stop();
            return;
        }
        lastSearchQuery = searchQuery;
        System.out.println("Searching for: " + searchQuery);
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

    public void initialize() throws IOException, ParseException {
        searchField.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    SwitchController.goSearchPage(this, event);
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
        searchContainer.setOnAction(event -> {
            try {
                SwitchController.goSearchPage(this, event);
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

package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.oop.manager.SwitchManager;
import com.oop.service.APICaller;
import com.oop.service.NetWorkException;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;

import org.json.simple.parser.ParseException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.Cursor;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainController extends BaseController {

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
                    SwitchManager.goSearchPage(this, event);
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
        List<String> suggestionsResults = new ArrayList<>();
        try {
            suggestionsResults = APICaller.querySuggest(searchQuery);
        } catch (URISyntaxException | IOException | ParseException e) {
            e.printStackTrace();
            return;
        }
        try {
            addSuggestions(suggestionsResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() throws IOException, ParseException {
        searchField.setOnKeyReleased(event -> handleKeyReleased(event));
    }

    private void handleKeyReleased(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            try {
                SwitchManager.goSearchPage(this, event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (idleTimeline == null) {
                idleTimeline = new Timeline(new KeyFrame(Duration.millis(IDLE_TIMEOUT), evt -> {
                    try {
                        handleIdleEvent();
                    } catch (NetWorkException e) {
                        e.printStackTrace();
                    }
                }));
                idleTimeline.setCycleCount(Timeline.INDEFINITE);
            }
            idleTimeline.playFromStart();
        }
    }

    @Override
    public TextField getSearchField() {
        return this.searchField;
    }
}

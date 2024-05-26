package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.oop.model.NetWorkException;
import javafx.scene.control.Alert;
import org.json.simple.parser.ParseException;

import com.oop.model.APICaller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

public class MainController {

    private String searchQuery;

    private static final int IDLE_TIMEOUT = 500;
    private Timeline idleTimeline;
    private String lastSearchQuery;

    @FXML
    private TextField searchField;

    @FXML
    private VBox suggestions;

    public void navigateToSearchResultsPage(Event event) throws IOException {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SearchResults.fxml"));
            Parent root = loader.load(); // Load content from SearchResults.fxml
            SearchController searchController = loader.getController();
            searchController.setSearchText(searchText); // Truyền nội dung sang SearchController
            searchController.initialize(null, null);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
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
            suggestionField.setStyle(
                    "-fx-padding:5px;-fx-font-size: 15px;-fx-border-color: rgb(15, 76,117);-fx-border-width: 0px 0px 1px 0px;-fx-border-radius: 0px 0px 10px 10px;");
            suggestionField.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    searchField.setText(suggestionLabel.getText());
                    try {
                        navigateToSearchResultsPage(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
        // Thêm lắng nghe sự kiện cho TextField khi scene được tạo

        searchField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    try {
                        navigateToSearchResultsPage(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        idleTimeline = new Timeline(new KeyFrame(Duration.millis(IDLE_TIMEOUT), event -> {
            try {
                handleIdleEvent();
            } catch (NetWorkException e) {
                e.printStackTrace();
            }
        }));
        idleTimeline.setCycleCount(Timeline.INDEFINITE);
        searchField.setOnKeyTyped(event -> {
            idleTimeline.playFromStart();
        });
    }
}

package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.oop.model.NetWorkException;
import javafx.scene.control.Alert;
import org.json.simple.parser.ParseException;

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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {

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
            System.out.println("Suggestion: " + suggestion);
            VBox suggestionField = new VBox();
            Label suggestionLabel = new Label(suggestion);
            suggestionField.getChildren().add(suggestionLabel);
            suggestionField.setOnMouseClicked(event -> {
                searchField.setText(suggestionLabel.getText());
                try {
                    navigateToSearchResultsPage(event);
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
        // Thêm lắng nghe sự kiện cho TextField khi scene được tạo
        searchField.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    navigateToSearchResultsPage(event);
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
    }
}

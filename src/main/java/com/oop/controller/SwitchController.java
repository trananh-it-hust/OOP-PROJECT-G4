package com.oop.controller;

import com.oop.model.Item;
import com.opencsv.exceptions.CsvValidationException;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;


public class SwitchController {
    private static Stage stage;

    private static Scene scene;

    private static Parent root;

    public static void goDetailPage(SearchController searchController,Event event, Item item, int pageNumber,String searchField)
            throws IOException, CsvValidationException, java.text.ParseException, URISyntaxException, ParseException {
        FXMLLoader loader = new FXMLLoader(searchController.getClass().getResource("/view/Detail.fxml"));
        root = loader.load();
        DetailController detailController = loader.getController();
        detailController.setItem(item);
        detailController.initialize();
        detailController.setPageNumberReturn(pageNumber);
        detailController.setSearchQueryReturn(searchField);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public static void goHomePage(SearchController searchController,Event event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(searchController.getClass().getResource("/view/Main.fxml"))); //Đảm bảo đối tượng truyền vào không phải là null
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public static void continueSearch(SearchController searchController,Event event) throws IOException {
        String searchText = searchController.getSearchField().getText().trim();
        if (!searchText.isEmpty()) {
            FXMLLoader loader = new FXMLLoader(searchController.getClass().getResource("/view/SearchResults.fxml"));
            root = loader.load();
            SearchController searchControllerNew = loader.getController();
            searchControllerNew.setSearchText(searchText);
            searchControllerNew.initialize(null, null);
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

}

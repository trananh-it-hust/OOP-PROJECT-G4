package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Set;

import com.oop.model.APICaller;
import com.oop.model.Item;
import com.opencsv.exceptions.CsvValidationException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class DetailController {

    private Item item;

    HashMap<String, Set<String>> detailContent;

    @FXML
    private TextFlow contentBox;
    private String searchText;
    private int seachPageBeforeGo;
    public void returnSearchPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SearchResults.fxml"));
        Parent root = loader.load(); // Load content from SearchResults.fxml
        SearchController searchController = loader.getController();
        searchController.setSearchPage(this.seachPageBeforeGo);
        searchController.setSearchText(this.searchText); // Truyền nội dung sang SearchController
        searchController.initialize(null, null);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void getDetailData() throws IOException, URISyntaxException, org.json.simple.parser.ParseException {
        if (item == null) {
            return;
        }
        detailContent = APICaller.getEntities(item.getContent());
        createDetailContent();
    }

    public void createDetailContent() {
        boolean isRender;
        String[] words = item.getContent().split(" ");
        for (String word : words) {
            isRender = false;
            for (String key : detailContent.keySet()) {
                if (!isRender && detailContent.get(key).contains(word)) {
                    Text keyword = new Text(key + " ");
                    Text text = new Text(word + " ");
                    text.setStyle("-fx-fill: blue; -fx-underline: true;");
                    keyword.setStyle("-fx-font-weight: bold; -fx-fill: red;");
                    contentBox.getChildren().add(keyword);
                    contentBox.getChildren().add(text);
                    isRender = true;
                }
            }
            if (!isRender) {
                Text text = new Text(word + " ");
                contentBox.getChildren().add(text);
            }
        }
    }

    public void initialize() throws CsvValidationException, IOException, ParseException, URISyntaxException,
            org.json.simple.parser.ParseException {
        getDetailData();
    }

    public void setItem(Item item) {
        this.item = item;
    }
    void setPageNumberReturn(int pageNumber){
        this.seachPageBeforeGo = pageNumber;
    }
    void setSearchQueryReturn(String searchText){
        this.searchText = searchText;
    }
}

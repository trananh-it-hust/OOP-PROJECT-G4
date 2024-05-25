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

    private Stage stage;
    private Scene scene;
    private Parent root;

    private Item item;

    HashMap<String, Set<String>> detailContent;

    @FXML
    private TextFlow contentBox;

    public void switchToMain(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/SearchResults.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void getDetailData() throws IOException, URISyntaxException, org.json.simple.parser.ParseException {
        if (item == null) {
            return;
        }
        detailContent = APICaller.getEntities(item.getContent());
        System.out.println(detailContent);
        createDetailContent();
    }

    public void createDetailContent() {
        boolean isRender = false;
        String[] words = item.getContent().split(" ");
        for (String word : words) {
            isRender = false;
            for (String key : detailContent.keySet()) {
                if (isRender == false && detailContent.get(key).contains(word)) {
                    Text keyword = new Text(key + " ");
                    Text text = new Text(word + " ");
                    text.setStyle("-fx-fill: blue; -fx-underline: true;");
                    keyword.setStyle("-fx-font-weight: bold; -fx-fill: red;");
                    contentBox.getChildren().add(keyword);
                    contentBox.getChildren().add(text);
                    isRender = true;
                }
            }
            if (isRender == false) {
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
}

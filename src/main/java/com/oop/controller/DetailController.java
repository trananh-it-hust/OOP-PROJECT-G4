package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Set;

import com.oop.model.APICaller;
import com.oop.model.Item;
import com.opencsv.exceptions.CsvValidationException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class DetailController extends BaseController {

    private Item item;

    HashMap<String, Set<String>> detailContent;

    @FXML
    private TextFlow contentBox;
    @FXML
    private Button returnButton;
    private String searchText;
    private int seachPageBeforeGo;

    public void getDetailData() throws IOException, URISyntaxException, org.json.simple.parser.ParseException {
        if (item == null) {
            return;
        }
        detailContent = APICaller.getEntities(item.getContent());
        System.out.println(detailContent);
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
        contentBox.setStyle("-fx-padding:10px");
    }

    public void initialize() throws CsvValidationException, IOException, ParseException, URISyntaxException,
            org.json.simple.parser.ParseException {
        getDetailData();
        returnButton.setOnAction(event -> {
            try {
                SwitchController.returnSearchPage(this, event, this.seachPageBeforeGo, this.searchText);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setItem(Item item) {
        this.item = item;
    }

    void setPageNumberReturn(int pageNumber) {
        this.seachPageBeforeGo = pageNumber;
    }

    void setSearchQueryReturn(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public TextField getSearchField() {
        return null;
    }
}

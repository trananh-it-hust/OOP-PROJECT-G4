package com.oop.controller;

import com.oop.model.APICaller;
import com.oop.model.Item;
import com.opencsv.exceptions.CsvValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Vector;

public class TrendController extends BaseController {
    private Item item;
    @FXML
    private VBox trendList;
    @FXML
    private Button returnPage;
    private int pageBefore;
    private String searchText;
    private HashMap<String, Vector<String>> trends;

    public void getTrendData() {
        if (item == null) {
            return;
        }
        try {
            trends = APICaller.trendDectect(item.getContent());
            System.out.println(trends);
            createTrendContent();
        } catch (IOException | URISyntaxException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
            // Hiển thị thông báo lỗi cho người dùng
            Text errorText = new Text("Failed to load trends: " + e.getMessage());
            trendList.getChildren().add(errorText);
        }
    }

    public void createTrendContent() {
        trendList.getChildren().clear();
        for (String trend : trends.keySet()) {
            Vector<String> details = trends.get(trend);
            if (details != null && !details.isEmpty()) {
                Text reasonText = new Text("Reason: " + details.get(0));
                trendList.getChildren().add(reasonText);

                for (int i = 1; i < details.size(); i++) {
                    Text citationText = new Text("Citation " + i + ": " + details.get(i));
                    trendList.getChildren().add(citationText);
                }
            }
        }
    }

    public void initialize() throws CsvValidationException, IOException, ParseException, URISyntaxException,
            org.json.simple.parser.ParseException {
        getTrendData();
        returnPage.setOnAction(event -> {
            try {
                SwitchController.returnSearchPage(this, event, this.pageBefore, this.searchText);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setItem(Item item) {
        this.item = item;
    }

    void setPageNumberReturn(int pageNumber) {
        this.pageBefore = pageNumber;
    }

    void setSearchQueryReturn(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public TextField getSearchField() {
        return null;
    }
}

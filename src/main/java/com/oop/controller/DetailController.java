package com.oop.controller;

import java.io.IOException;
import java.text.ParseException;

import com.oop.model.DetailItem;
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

    private DetailItem detailItem;

    @FXML
    private TextFlow contentBox;

    public void switchToMain(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public DetailItem getDetailData() {
        // Lấy dữ liệu từ model
        detailItem = new DetailItem(12);
        return detailItem;
    }

    public void createDetailContent() {
        // tách các chữ trong content thành các từ
        String[] words = detailItem.getItem().getContent().split(" ");
        for (String word : words) {
            // check trong map detailContent có từ nào trùng với từ hiện tại không
            if (detailItem.getDetailContent().get("People").contains(word)) {
                Text text = new Text(word + " ");
                text.setFill(javafx.scene.paint.Color.RED);
                contentBox.getChildren().add(text);
                continue;
            }
            if (detailItem.getDetailContent().get("Object").contains(word)) {
                Text text = new Text(word + " ");
                text.setFill(javafx.scene.paint.Color.BLUE);
                contentBox.getChildren().add(text);
                continue;
            }
            Text text = new Text(word + " ");
            contentBox.getChildren().add(text);

        }
    }

    public void initialize() throws CsvValidationException, IOException, ParseException {
        // Thêm lắng nghe sự kiện cho Pane khi scene được tạo
        getDetailData();
        createDetailContent();
    }
}

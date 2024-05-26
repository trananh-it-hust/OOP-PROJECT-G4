package com.oop;

import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;

import com.oop.model.Item;
import com.opencsv.exceptions.CsvValidationException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // load the FXML resource
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Main.fxml")));

            // create a scene
            primaryStage.setScene(new Scene(root, 800, 600));
            // prevent resizing
            primaryStage.setResizable(false);
            // set the title
            primaryStage.setTitle("Blockchain Search App");

            // set the icon
            primaryStage.getIcons().add(new javafx.scene.image.Image("/image/icon.png"));

            // show the GUI
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws CsvValidationException, IOException, ParseException {
        Item.readItemsFromCSV();
        launch(args);
    }
}

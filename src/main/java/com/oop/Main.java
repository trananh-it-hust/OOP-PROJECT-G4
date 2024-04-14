package com.oop;

import java.io.IOException;

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
            Parent root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));

            // create a scene
            primaryStage.setScene(new Scene(root));

            // set the title
            primaryStage.setTitle("Blockchain Search App");

            // set the icon
            primaryStage.getIcons().add(new javafx.scene.image.Image("/image/icon.png"));

            // add the stylesheet
            primaryStage.getScene().getStylesheets().add("/styles/Main.css");

            // show the GUI
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        launch(args);
    }
}

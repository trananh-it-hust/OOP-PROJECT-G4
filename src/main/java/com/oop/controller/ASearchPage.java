package com.oop.controller;


import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public abstract class ASearchPage {


    protected VBox suggestions;

    protected Stage stage;

    protected Scene scene;

    protected Parent root;
    public  abstract void addSuggestions(List<String> suggestionsResult) throws IOException;

}

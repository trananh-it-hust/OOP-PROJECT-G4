package com.oop.controller;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ASearchPage {

    protected TextField searchField;

    protected VBox suggestions;
    protected VBox searchResults;
    protected Button nextPage;
    protected Button prevPage;
    protected Stage stage;

    protected Scene scene;

    protected Parent root;
    protected int NumberPage;
    protected ArrayList<Objects> searchResultList;

    public abstract void addSuggestions(List<String> suggestionsResult) throws IOException;

}

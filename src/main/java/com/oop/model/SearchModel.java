package com.oop.model;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SearchModel {

    public static List<Item> searchByTitle(List<Item> items, String keyword) {

        List<Item> searchResults = new ArrayList<>();
        for (Item item : items) {
            if (item.getArticleTitle().toLowerCase().contains(keyword.toLowerCase())) {
                searchResults.add(item);
            }
        }
        return searchResults;
    }

    public static List<String> GetSuggestion(String keyword) {
        // from suggestion.json

        List<String> suggestions = new ArrayList<>();
        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader("/src/main/resources/data/suggestion.json"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        JSONObject jo = (JSONObject) obj;
        JSONArray ja = (JSONArray) jo.get("du_lieu");

        for (int i = 0; i < ja.size(); i++) {
            suggestions.add((String) ja.get(i));
        }

        return suggestions;
    }
}

package com.oop.model;

import java.util.ArrayList;
import java.util.List;

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
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Bitcoin");
        suggestions.add("Ethereum");
        suggestions.add("Ripple");
        suggestions.add("Litecoin");
        return suggestions;
    }
}

package com.oop.sorter;

import com.oop.model.Item;

import java.util.ArrayList;
import java.util.List;

public class AuthorSorter implements ISorter<Item> {

    @Override
    public List<Item> sort(List<Item> list) {
        List<Item> sortedList = new ArrayList<>(list);
        sortedList.sort((item1, item2) -> {
            try {
                return item1.getAuthor().compareTo(item2.getAuthor());
            } catch (NullPointerException e) {
                if (item1.getAuthor() == null && item2.getAuthor() == null) {
                    return 0;
                }
                if (item1.getAuthor() == null) {
                    return -1;
                }
                return 1;
            }
        });
        return sortedList;
    }
}

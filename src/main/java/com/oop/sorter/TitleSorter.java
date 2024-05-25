package com.oop.sorter;

import com.oop.model.Item;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TitleSorter implements ISorter<Item> {

    @Override
    public List<Item> sort(List<Item> list) {
        List<Item> sortedList = new ArrayList<>(list);
        Collections.sort(sortedList, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return item1.getArticleTitle().compareTo(item2.getArticleTitle());
            }
        });
        return sortedList;
    }
}

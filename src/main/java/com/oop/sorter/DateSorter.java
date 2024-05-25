package com.oop.sorter;

import com.oop.model.Item;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateSorter implements ISorter<Item> {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public List<Item> sort(List<Item> list) {
        List<Item> sortedList = new ArrayList<>(list);
        Collections.sort(sortedList, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                try {
                    return sdf.parse(String.valueOf(String.valueOf(item1.getCreationDate()))).compareTo(sdf.parse(String.valueOf(item2.getCreationDate())));
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Invalid date format", e);
                }
            }
        });
        return sortedList;
    }
}

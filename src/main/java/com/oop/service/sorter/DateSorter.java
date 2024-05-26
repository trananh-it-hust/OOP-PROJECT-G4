package com.oop.service.sorter;

import com.oop.model.Item;
import com.oop.service.sorter.ISorter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DateSorter implements ISorter<Item> {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public List<Item> sort(List<Item> list) {
        List<Item> sortedList = new ArrayList<>();
        List<Item> nullDateList = new ArrayList<>();

        for (Item item : list) {
            if (item.getCreationDate() == null || item.getCreationDate().equals("None")) {
                nullDateList.add(item);
            } else {
                sortedList.add(item);
            }
        }

        sortedList.sort((item1, item2) -> {
            try {
                // Kiểm tra nếu là ngày "None" thì trả về một ngày mặc định
                if (item1.getCreationDate().equals("None"))
                    return -1;
                if (item2.getCreationDate().equals("None"))
                    return 1;
                return sdf.parse(item1.getCreationDate()).compareTo(sdf.parse(item2.getCreationDate()));
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid date format", e);
            }
        });

        sortedList.addAll(nullDateList); // Thêm các mục có ngày null vào cuối danh sách
        return sortedList;
    }
}

package com.oop.sorter;

import com.oop.model.Item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class DateSorter implements ISorter<Item> {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public List<Item> sort(List<Item> list) {
        List<Item> sortedList = new ArrayList<>();

        for (Item item : list) {
            if (item.getCreationDate() == null) {
                boolean shouldContinue = getUserConfirmation();
                if (!shouldContinue) {
                    continue;
                }
            }
            sortedList.add(item);
        }

        sortedList.sort(new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                try {
                    return sdf.parse(item1.getCreationDate()).compareTo(sdf.parse(item2.getCreationDate()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Invalid date format", e);
                }
            }
        });

        return sortedList;
    }

    private boolean getUserConfirmation() {
        // This is a placeholder for actual user interaction.
        // Replace this with GUI code (e.g., JOptionPane in Swing) in a real application.
        Scanner scanner = new Scanner(System.in);
        System.out.println("There is an item with a null creation date. Do you want to continue without this item? (yes/no): ");
        String response = scanner.nextLine();
        return response.equalsIgnoreCase("yes");
    }
}

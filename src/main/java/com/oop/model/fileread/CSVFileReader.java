package com.oop.model.fileread;

import com.oop.model.Item;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVFileReader implements IReaderFile{
    @Override
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        String filePath = "src/main/resources/data/data.csv";

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            // Đọc dữ liệu từ file CSV
            List<String[]> csvData = reader.readAll();

            // Kiểm tra xem tệp CSV có dữ liệu hay không trước khi xóa dòng tiêu đề
            if (!csvData.isEmpty()) {
                csvData.remove(0); // Xóa dòng tiêu đề

                // Tạo các đối tượng Item từ dữ liệu CSV
                for (String[] row : csvData) {
                    Item item = createItemFromCSV(row);
                    itemList.add(item);
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return itemList;
    }

    // Phương thức tạo đối tượng Item từ mảng dữ liệu từ file CSV
    private Item createItemFromCSV(String[] data) {
        // Kiểm tra xem mảng có đủ dữ liệu không
        if (data.length < 10) {
            throw new IllegalArgumentException("Invalid CSV data: " + String.join(",", data));
        }

        // Tạo đối tượng Item từ dữ liệu
        Item item = new Item();
        item.setArticleLink(data[0]);
        item.setWebsiteSource(data[1]);
        item.setArticleType(data[2]);
        item.setSummary(data[3]);
        item.setArticleTitle(data[4]);
        item.setContent(data[5]);
        item.setCreationDate(data[6]);
        item.setTags(data[7]);
        item.setAuthor(data[8]);
        item.setCategory(data[9]);

        return item;
    }
}

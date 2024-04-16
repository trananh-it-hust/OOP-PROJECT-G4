package com.oop.model;

import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Item {
    private String articleLink;
    private String websiteSource;
    private String articleType;
    private String articleTitle;
    private String content;
    private Date creationDate;
    private String author;
    private String category;
    private String tags;
    private String summary;

    // Constructors
    public Item() {
        // Default constructor
    }

    public Item(String articleLink, String websiteSource, String articleType, String articleTitle,
            String content, Date creationDate, String author, String category, String tags,
            String summary) {
        this.articleLink = articleLink;
        this.websiteSource = websiteSource;
        this.articleType = articleType;
        this.articleTitle = articleTitle;
        this.content = content;
        this.creationDate = creationDate;
        this.author = author;
        this.category = category;
        this.tags = tags;
        this.summary = summary;
    }

    // get data
    public static List<Item> readItemsFromCSV()
            throws IOException, ParseException, CsvValidationException {
        List<Item> items = new ArrayList<>();

        Reader reader = new FileReader("src/main/resources/data/data.csv");
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build(); // Bỏ qua dòng tiêu đề

        String[] nextRecord;
        while ((nextRecord = csvReader.readNext()) != null) {
            String articleLink = nextRecord[0];
            String websiteSource = nextRecord[1];
            String articleType = nextRecord[2];
            String articleTitle = nextRecord[3];
            String content = nextRecord[4];
            Date creationDate = new SimpleDateFormat("MMMM dd, yyyy, h:mma z", Locale.ENGLISH).parse(nextRecord[5]);
            String author = nextRecord[6];
            String category = nextRecord[7];
            String tags = nextRecord[8];
            String summary = nextRecord[9];

            Item item = new Item(articleLink, websiteSource, articleType, articleTitle, content,
                    creationDate, author, category, tags, summary);
            items.add(item);
        }
        csvReader.close();
        return items;
    }

    // Phương thức render ra UI cho một mục
    public static VBox renderUI(Item item) {
        VBox itemUI = new VBox();
        Label titleLabel = new Label("Title: " + item.articleTitle);
        itemUI.getChildren().add(titleLabel);
        return itemUI;
    }
    // Getters and setters for all properties

    public String getArticleLink() {
        return articleLink;
    }

    public void setArticleLink(String articleLink) {
        this.articleLink = articleLink;
    }

    public String getWebsiteSource() {
        return websiteSource;
    }

    public void setWebsiteSource(String websiteSource) {
        this.websiteSource = websiteSource;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}

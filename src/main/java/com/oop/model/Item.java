package com.oop.model;

import java.util.Date;

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
    private boolean temp;

    // Constructors
    public Item() {
        // Default constructor
    }

    public Item(String articleLink, String websiteSource, String articleType, String articleTitle,
            String content, Date creationDate, String author, String category, String tags,
            String summary, boolean temp) {
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
        this.temp = temp;
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

    public boolean isTemp() {
        return temp;
    }

    public void setTemp(boolean temp) {
        this.temp = temp;
    }
}

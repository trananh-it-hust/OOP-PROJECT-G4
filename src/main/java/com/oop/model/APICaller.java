package com.oop.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class APICaller {
    // Lấy gợi ý
    public static List<String> querySuggest(String input) throws URISyntaxException, IOException, ParseException {
        List<String> res = new ArrayList<String>();
        StringBuffer content = connectAndGetRawData("http://localhost:8000/suggestion?data=", input);
        JSONParser parser = new JSONParser();
        JSONObject jo = (JSONObject) parser.parse(content.toString());
        JSONArray ja = (JSONArray) jo.get("result");
        for (int i = 0; i < ja.size(); i++) {
            res.add((String) ja.get(i));
        }
        return res;
    }

    // Lấy tìm kiếm
    public static List<Item> getSearchResult(String input) throws ParseException, IOException, URISyntaxException {
        List<Item> results = new ArrayList<Item>();
        StringBuffer jsonContent = connectAndGetRawData("http://localhost:8000/search?q=", input);
        String s = jsonContent.toString().replace("NaN", "\"None\"");
        JSONObject jo = (JSONObject) new JSONParser().parse(s);
        JSONArray ja = (JSONArray) jo.get("results");
        for (Object obj : ja) {
            Item item = new Item();
            JSONObject article = (JSONObject) ((JSONObject) obj).get("article");
            item.setArticleLink(article.get("Article link").toString());
            item.setWebsiteSource(article.get("Website source").toString());
            item.setArticleType(article.get("Article type").toString());
            item.setArticleTitle(article.get("Article title").toString());
            item.setContent(article.get("Content").toString());
            item.setCreationDate(article.get("Creation date").toString());
            item.setAuthor(article.get("Author").toString());
            item.setTags(article.get("Tags").toString());
            item.setCategory(article.get("Category").toString());
            item.setSummary(article.get("Summary").toString());
            results.add(item);
        }
        return results;
    }

    // Goi api va lay ket qua vao buffer
    public static StringBuffer connectAndGetRawData(String urlString, String input)
            throws IOException, URISyntaxException {
        StringBuffer content = new StringBuffer();
        String parsedInput = URLEncoder.encode(input, "UTF-8");
        URI uri = new URI(urlString + parsedInput);
        URL url = uri.toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-type", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        if (con.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        }
        return content;
    }
}

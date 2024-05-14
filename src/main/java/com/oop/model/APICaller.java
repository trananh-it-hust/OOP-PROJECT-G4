package com.oop.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class APICaller {
    // Lấy gợi ý
    public static List<String> querySuggest(String input) throws URISyntaxException, IOException, ParseException {
        List<String> res = new ArrayList<String>();
        StringBuffer content = connectAndGetRawData("GET", "http://localhost:8000/suggestion?data=", input);
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
        StringBuffer jsonContent = connectAndGetRawData("GET", "http://localhost:8000/search?q=", input);
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

    // Get entities from paragraph
    public static HashMap<String, Set<String>> getEntities(String input)
            throws IOException, URISyntaxException, ParseException {
        HashMap<String, Set<String>> result = new HashMap<String, Set<String>>();
        StringBuffer jsonContent = connectAndGetRawData("POST", "http://localhost:8000/predict", input);
        String s = jsonContent.toString();
        JSONObject jo = (JSONObject) new JSONParser().parse(s);
        JSONArray ja = (JSONArray) jo.get("entities");
        for (Object joja : ja) {
            if (joja instanceof JSONObject) {
                JSONObject jojajo = (JSONObject) joja;
                for (Object key : jojajo.keySet()) {
                    JSONArray jaja = (JSONArray) jojajo.get(key);
                    Set<String> items = new HashSet<String>();
                    for (Object object : jaja) {
                        JSONArray jojojo = (JSONArray) object;
                        for (Object object2 : jojojo) {
                            if (object2 instanceof String) {
                                items.add((String) object2);
                            }
                        }
                    }
                    result.put((String) key, items);
                }
            }
        }
        return result;
    }

    // Goi api va lay ket qua vao buffer
    public static StringBuffer connectAndGetRawData(String methodType, String urlString, String input)
            throws IOException, URISyntaxException {
        StringBuffer content = new StringBuffer();
        String parsedInput = URLEncoder.encode(input, "UTF-8");
        URI uri = new URI(urlString + parsedInput);
        URL url = uri.toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(methodType);
        con.setRequestProperty("Content-type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        if (methodType.equals("POST")) {
            // Enable sending data
            con.setDoOutput(true);
            // Convert the text data to JSON format
            String jsonInputString = "{\"text\":\"" + input + "\"}";
            // Send the request data
            try (OutputStream os = con.getOutputStream()) {
                byte[] in = jsonInputString.getBytes("utf-8");
                os.write(in, 0, in.length);
            }
        } else if (methodType.equals("GET")) {
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
        }
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
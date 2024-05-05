package com.oop.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class APICaller {
    // Lấy gợi ý
    public static List<String> querySuggest(String input) throws URISyntaxException, IOException {
        List<String> res = new ArrayList<String>();
        String parsedInput = URLEncoder.encode(input, "UTF-8");
        URI uri = new URI("http://localhost:8000/?data=" + parsedInput);
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
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            try {
                BufferedWriter fw = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(
                                        "D:/VScode/Java/git/OOP-PROJECT-G4/src/main/resources/data/suggestion.json",
                                        false),
                                StandardCharsets.UTF_8));
                fw.write(content.toString());
                fw.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
                e.printStackTrace();
            }
        }
        return res;
    }

    // Lấy tìm kiếm
    public List<Item> getSearchResult(String input) {
        List<Item> results = new ArrayList<>();
        return results;
    }
}

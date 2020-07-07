package com.example.newsapp;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static String LOG_TAG = "Test-Case-Error-generated";
    private static String LOG_TAG_output = "Test-Case-Output-generated";

    //Call by direct class name and not by creating its objects
    private QueryUtils() {
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";


        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 );
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Newsdata JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<NewsData> fetchNewsDataFromJson(String newsJson) {
        ArrayList<NewsData> arrayList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(newsJson);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            JSONArray jsonArray = response.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String sectionName = jsonObject.getString("sectionName");
                String title = jsonObject.getString("webTitle");
                String webUrl = jsonObject.getString("webUrl");
                String MergeDate = jsonObject.optString("webPublicationDate");

                String[] dateTime = MergeDate.split("T");
                String date = dateTime[0];
                String time = dateTime[1];
                time = time.substring(0, 5);
                String author = "";
                JSONArray alltags = jsonObject.getJSONArray("tags");
                if (alltags != null) {
                    for (int k = 0; k < alltags.length(); k++) {
                        JSONObject tagsobject = alltags.getJSONObject(k);
                        author = tagsobject.getString("webTitle");
                    }
                    if (author == null) {
                        author = "No Author";
                    }
                } else {
                    author = "No Author";
                }
                Log.i(LOG_TAG_output, time);
                Log.i(LOG_TAG_output, date);
                Log.i(LOG_TAG_output, title);
                Log.i(LOG_TAG_output, webUrl);
                Log.i(LOG_TAG_output, author);
                Log.i(LOG_TAG_output, sectionName);


                NewsData newsData = new NewsData(title, webUrl, sectionName, date, time, author);
                arrayList.add(newsData);
            }

        } catch (JSONException e) {
            Log.i(LOG_TAG, "Can't extract features :" + e.toString());
        }
        return arrayList;
    }

    public static List<NewsData> fetchNewsData(String requestUrl) {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<NewsData> newsData = fetchNewsDataFromJson(jsonResponse);
        return newsData;
    }


}


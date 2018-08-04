package com.example.android.newsapp.feature;


import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Helper methods related to requesting and receiving News data from USGS.
 */


public final class QueryUtils {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    private static String formatDate(String rawDate) {
        String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.US);
        try {
            Date parsedJsonDate = jsonFormatter.parse(rawDate);
            String finalDatePattern = "MMM d, yyy";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDatePattern, Locale.US);
            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Error parsing JSON date: ", e);
            return "";
        }
    }


    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String NewsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(NewsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding NewsList to
        List<News> NewsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(NewsJSON);

            JSONObject jsonResponse = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or NewsList).
            JSONArray NewsArray = jsonResponse.getJSONArray("results");


            // For each News in the NewsArray, create an {@link News} object
            for (int i = 0; i < NewsArray.length(); i++) {

                // Get a single News at position i within the list of NewsList
                JSONObject currentNews = NewsArray.getJSONObject(i);

                // For a given News, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that News.

                // Extract the value for the key called "webTitle"
                String webTitle = currentNews.getString("webTitle");

                // Extract the value for the key called "webUrl"
                String url = currentNews.getString("webUrl");

                // Extract the value for the key called "webPublicationDate"
                String date = currentNews.getString("webPublicationDate");
                date = formatDate(date);

                // Extract the value for the key called "sectionName"
                String section = currentNews.getString("sectionName");

                // Extract the tags from the tagsArray
                JSONArray tagsArray = currentNews.getJSONArray("tags");
                String author = "";

                if (tagsArray.length() == 0) {
                    author = null;
                } else {
                    // To handle if their are multiple authors
                    for (int j = 0; j < tagsArray.length(); j++) {
                        JSONObject firstObject = tagsArray.getJSONObject(j);
                        author += firstObject.getString("webTitle") + ".";
                    }
                }


                // Create a new {@link News} object with the magnitude, location, time,
                // and url from the JSON response.
                News News = new News(webTitle, section, author, url, date);

                // Add the new {@link News} to the list of NewsList.
                NewsList.add(News);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the News JSON results", e);
        }

        // Return the list of NewsList
        return NewsList;
    }


    public static String createStringUrl(String orderBy, String pageCount, String section) {


        Uri.Builder builder = new Uri.Builder();
        if (section.equals("all")) {
            builder.scheme("http")
                    .encodedAuthority("content.guardianapis.com")
                    .appendPath("search")
                    .appendQueryParameter("order-by", orderBy)
                    .appendQueryParameter("show-references", "author")
                    .appendQueryParameter("show-tags", "contributor")
                    .appendQueryParameter("page-size", pageCount)
                    .appendQueryParameter("api-key", "efb96140-65eb-449d-b9b7-f2eea33bd02d");
        } else {
            builder.scheme("http")
                    .encodedAuthority("content.guardianapis.com")
                    .appendPath("search")
                    .appendQueryParameter("order-by", orderBy)
                    .appendQueryParameter("show-references", "author")
                    .appendQueryParameter("show-tags", "contributor")
                    .appendQueryParameter("section", section)
                    .appendQueryParameter("page-size", pageCount)
                    .appendQueryParameter("api-key", "efb96140-65eb-449d-b9b7-f2eea33bd02d");
        }
        String url = builder.build().toString();
        return url;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the News JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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

    /**
     * Query the USGS dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> NewsList = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link News}
        return NewsList;
    }


}





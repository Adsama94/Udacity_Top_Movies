package com.adsama.android.topmovies.Network;

import android.text.TextUtils;
import android.util.Log;

import com.adsama.android.topmovies.Model.Reviews;

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


public final class ReviewQueryUtils {

    private static final String LOG_TAG = ReviewQueryUtils.class.getSimpleName();

    public ReviewQueryUtils() {

    }

    public static List<Reviews> fetchReviewData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<Reviews> reviewsList = extractFeatureFromJson(jsonResponse);
        return reviewsList;
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
            urlConnection.setReadTimeout(20000);
            urlConnection.setConnectTimeout(25000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Reviews JSON results.", e);
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

    private static List<Reviews> extractFeatureFromJson(String reviewJSON) {
        if (TextUtils.isEmpty(reviewJSON)) {
            return null;
        }

        List<Reviews> reviewsList = new ArrayList<>();

        try {
            JSONObject reviewJsonResponse = new JSONObject(reviewJSON);
            JSONArray reviewArray = reviewJsonResponse.getJSONArray("results");
            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject currentReview = reviewArray.getJSONObject(i);
                String reviewId = currentReview.getString("id");
                String reviewAuthor = currentReview.getString("author");
                String reviewContent = currentReview.getString("content");
                String reviewUrl = currentReview.getString("url");
                Reviews reviews = new Reviews(reviewId, reviewAuthor, reviewContent, reviewUrl);
                reviewsList.add(reviews);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewsList;
    }
}

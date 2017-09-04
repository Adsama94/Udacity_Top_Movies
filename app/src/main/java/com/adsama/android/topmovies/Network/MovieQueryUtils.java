package com.adsama.android.topmovies.Network;


import android.text.TextUtils;
import android.util.Log;

import com.adsama.android.topmovies.Model.Movies;

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

public final class MovieQueryUtils {

    private static final String LOG_TAG = MovieQueryUtils.class.getSimpleName();

    private MovieQueryUtils() {

    }

    public static List<Movies> fetchMovieData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<Movies> moviesList = extractFeatureFromJson(jsonResponse);
        return moviesList;
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
            Log.e(LOG_TAG, "Problem retrieving the movies JSON results.", e);
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

    private static List<Movies> extractFeatureFromJson(String movieJSON) {
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        List<Movies> moviesList = new ArrayList<>();

        try {
            JSONObject movieJsonResponse = new JSONObject(movieJSON);
            JSONArray movieArray = movieJsonResponse.getJSONArray("results");
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject currentMovie = movieArray.getJSONObject(i);
                long movieId = currentMovie.getLong("id");
                String movieTitle = currentMovie.getString("original_title");
                String moviePoster = currentMovie.getString("poster_path");
                String movieBackdrop = currentMovie.getString("backdrop_path");
                String movieOverview = currentMovie.getString("overview");
                String movieRating = currentMovie.getString("vote_average");
                String movieReleaseDate = currentMovie.getString("release_date");
                Movies movies = new Movies(movieId,movieTitle, moviePoster, movieBackdrop, movieOverview, movieRating, movieReleaseDate);
                moviesList.add(movies);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moviesList;
    }
}

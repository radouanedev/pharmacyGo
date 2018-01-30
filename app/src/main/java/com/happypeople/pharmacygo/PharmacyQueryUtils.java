package com.happypeople.pharmacygo;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

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

/**
 * Created by Radouane on 10/01/2018.
 */

public class PharmacyQueryUtils {


    public static final String LOG_TAG = PharmacyQueryUtils.class.getSimpleName();


    /**
     * @return List of Pharmacy by String Url
     */
    public synchronized static List<Pharmacy> getPharmacies(String requestUrl) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.i(LOG_TAG,"TEST: fetchEarthquakeData() called ...");
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        URL url = createUrl(requestUrl);
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // TODO Handle the IOException
        }

        List<Pharmacy> pharmacies = extractPharmacies(jsonResponse);

        return pharmacies;

    }


    /**
     * @return List of Pharmacy by Json
     */
    private static List<Pharmacy> extractPharmacies(String json) {

        if(TextUtils.isEmpty(json)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Pharmacy> pharmacies = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of pharmcies objects with the corresponding data.
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray results = jsonRootObject.getJSONArray("results");

            for(int i = 0; i < results.length(); i++) {

                JSONObject geometry = results.getJSONObject(i).getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");

                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");

                LatLng latLng = new LatLng(lat, lng);

                String id = results.getJSONObject(i).getString("id");
                String place_id = results.getJSONObject(i).getString("place_id");

                Pharmacy pharmacy = new Pharmacy(
                        id,
                        latLng,
                        place_id
                );

                pharmacies.add(pharmacy);

            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("Exception", "Problem Exception", e);
        }

        // Return the list of earthquakes
        return pharmacies;
    }


    /**
     * @return new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     * @return json api of Pharmacies
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setReadTimeout(10000 /* milliseconds */);
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
            // TODO: Handle the exception
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     * @return json api of Pharmacies
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine(); // La methode readLine() se boucle tout seul ligne par ligne
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


}

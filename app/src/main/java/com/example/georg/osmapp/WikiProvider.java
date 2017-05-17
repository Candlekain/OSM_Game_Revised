package com.example.georg.osmapp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Georg on 29.11.2016.
 */

public class WikiProvider extends AsyncTask <String, Void, String> {
    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            //URL url = new URL("http://www.wuerzburgwiki.de/w/api.php?action=query&prop=extracts&titles="+params[0].replace(" ","%20")+"&redirects&format=json&exintro&explaintext");
            URL url = new URL("https://de.wikipedia.org/w/api.php?action=query&prop=extracts&titles="+params[0].replace(" ","%20")+"&redirects&format=json&exintro&explaintext");

            // Create the request to Wikipedia, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            JSONObject json = new JSONObject(buffer.toString());
            jsonString = json.getJSONObject("query").getJSONObject("pages").names().getString(0);
            String returnString = json.getJSONObject("query").getJSONObject("pages").getJSONObject(jsonString).getString("extract");
            return returnString;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }
}

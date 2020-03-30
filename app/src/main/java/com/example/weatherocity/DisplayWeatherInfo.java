package com.example.weatherocity;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DisplayWeatherInfo extends AsyncTask<String, Void, String>

{
    @Override
    protected String doInBackground(String... address) {


        //Check if URL is valid
        try {
            URL url = new URL(address[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //Establish connection
            connection.connect();

            //retrieve data
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            //Retrieve data and return it as a String - Done and Working ^_^
            int data = isr.read();
            String content = "";
            char ch;

            while (data != -1) {
                ch = (char) data;
                content += ch;
                data = isr.read();
            }
            return content;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

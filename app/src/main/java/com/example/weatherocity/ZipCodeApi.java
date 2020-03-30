package com.example.weatherocity;

/*There is a bug in the program that should be fixed later. When the zipcode is invalid, it returns
* back to the default settings. Change this later so that the last search reload*/

import android.content.Context;
import android.widget.Toast;

import org.json.JSONObject;

import static android.widget.Toast.LENGTH_SHORT;

public class ZipCodeApi {
   //internal class variables
    private String zc_key = "xT9ITQEsFTBDLPFryszfXZhlWVcYHMGctYW5CA1t65ARk4pdpwDEfD9tNwwn9Wb3";
    private String zc_key2 = "n42yPGSwl3dOB0wUCXQ6TSaV6f2TvINaVo7cdrGUrdjA7EuX6L3D5bAduHfsu4UJ";
    public static String zip_content;
   //"60074";//this will change later

   //for MainActivity
    public static String coords;
    public static String city_state;

public static Boolean isZipValid = true;

    public void getZipData() {


        DisplayWeatherInfo getInfo = new DisplayWeatherInfo();
        String zipcode = MainActivity.zipInput;


        try {
            //connect data
            zip_content = getInfo.execute(
                    "https://www.zipcodeapi.com/rest/" + zc_key2 + "/info.json/" +
                            zipcode + "/degrees").get();

            if(zip_content == null)
                isZipValid = false;

            //create jsonObject
            JSONObject zor = new JSONObject(zip_content);
            //extract data from json file
            String lat,lng,city,state;

           lat = zor.getString("lat");
           lng = zor.getString("lng");
           city = zor.getString("city");
           state = zor.getString("state");

           //assign json objects to declared variables
           coords = lat +","+lng;
           city_state = city+","+state;

        } catch (Exception e) {

            e.printStackTrace();


        }

    }



}

package com.example.weatherocity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    //internal variables
    private String dw_key = "c009feaaec328465b9a982f0f0b5de90";


    private static TextView[] hr_time;
    private static TextView[] hr_temp;
    private static View[] hr_icons;
    private static String unitChange;

    //Shared variable
    public static String zipInput;


    //Current
    TextView city_state;
    TextView current_temp;
    TextView current_cond;
    ImageView current_icon;

    //Hourly
    TextView hr1, hr2, hr3, hr4, hr5, hr6;
    TextView hrtemp_1, hrtemp_2, hrtemp_3, hrtemp_4, hrtemp_5, hrtemp_6;
    ImageView hr_icon1, hr_icon2, hr_icon3, hr_icon4, hr_icon5, hr_icon6;

    //High and Low
    TextView higtemp_display, lowtemp_display;

    //Sunrise and Sunset
    TextView sunriseTime, sunsetTime;


    //set id's and arrays
    public void setViews() {

        //Current temp,condition,and weather icon
        city_state = findViewById(R.id.city_state);
        current_temp = findViewById(R.id.current_temp);
        current_cond = findViewById(R.id.current_cond);
        current_icon = findViewById(R.id.current_icon);

        //hourly time,temps,and weather icon
        hr1 = findViewById(R.id.hr1);
        hr2 = findViewById(R.id.hr2);
        hr3 = findViewById(R.id.hr3);
        hr4 = findViewById(R.id.hr4);
        hr5 = findViewById(R.id.hr5);
        hr6 = findViewById(R.id.hr6);

        hrtemp_1 = findViewById(R.id.hrtemp_1);
        hrtemp_2 = findViewById(R.id.hrtemp_2);
        hrtemp_3 = findViewById(R.id.hrtemp_3);
        hrtemp_4 = findViewById(R.id.hrtemp_4);
        hrtemp_5 = findViewById(R.id.hrtemp_5);
        hrtemp_6 = findViewById(R.id.hrtemp_6);


        hr_icon1 = findViewById(R.id.hr_icon1);
        hr_icon2 = findViewById(R.id.hr_icon2);
        hr_icon3 = findViewById(R.id.hr_icon3);
        hr_icon4 = findViewById(R.id.hr_icon4);
        hr_icon5 = findViewById(R.id.hr_icon5);
        hr_icon6 = findViewById(R.id.hr_icon6);

        //high low
        higtemp_display = findViewById(R.id.higtemp_display);
        lowtemp_display = findViewById(R.id.lowtemp_display);

        //sunrise/sunset
        sunriseTime = findViewById(R.id.sunriseTime);
        sunsetTime = findViewById(R.id.sunsetTime);

        //Arrays
        hr_time = new TextView[]{hr1, hr2, hr3, hr4, hr5, hr6};
        hr_temp = new TextView[]{hrtemp_1, hrtemp_2, hrtemp_3, hrtemp_4, hrtemp_5, hrtemp_6};
        hr_icons = new View[]{hr_icon1, hr_icon2, hr_icon3, hr_icon4, hr_icon5, hr_icon6};
    }

    public void hourlyViews() {
        hr_time = new TextView[]{hr1, hr2, hr3, hr4, hr5, hr6};
        hr_temp = new TextView[]{hrtemp_1, hrtemp_2, hrtemp_3, hrtemp_4, hrtemp_5, hrtemp_6};
    }

    public void getHighLowViews() {
        //high low

        higtemp_display = findViewById(R.id.higtemp_display);
        lowtemp_display = findViewById(R.id.lowtemp_display);
    }

    public void getIcons() {
        hr_icons = new View[]{hr_icon1, hr_icon2, hr_icon3, hr_icon4, hr_icon5, hr_icon6};
    }


    public void displayWeather() {

        String content;
        //Retrieve Async connectivitiy
        DisplayWeatherInfo dwi = new DisplayWeatherInfo();

        ZipCodeApi zp = new ZipCodeApi();
        zp.getZipData();


        try {
            String coords = ZipCodeApi.coords;
            String cityState = ZipCodeApi.city_state;

            String degree = getString(R.string.degree_unicode);
            content = dwi.execute("https://api.darksky.net/forecast/" + dw_key + "/" + coords).get();

            setViews();//call the views
            //Set current display information

            JSONObject jsonObject = new JSONObject(content);
            String currently = jsonObject.getString("currently");

            //Create object to retrieve current weather information
            JSONObject cur_obj = new JSONObject(currently);

            //Retrieve the summary,temperature,and icon for the current weather

            String currentTemp = toSingleInt(cur_obj.getString("temperature"));
            String summary = cur_obj.getString("summary");
            String cur_icon = cur_obj.getString("icon");

            //check temperature to set color change-> Create method
            setColorsByTemp(currentTemp);

            //Set Data
            if(unitChange.equals("Celsius")){
                city_state.setText(cityState);

                current_temp.setText((String.format("%s%s", toCelsius(currentTemp), degree)));
                current_cond.setText(summary);
                iconSelection(cur_icon, current_icon);
            }else {

                city_state.setText(cityState);
                current_temp.setText(String.format("%s%s", currentTemp, degree));
                current_cond.setText(summary);
                iconSelection(cur_icon, current_icon);
            }

            //Retrieve hourly data
            String hourly = jsonObject.getString("hourly");
            JSONObject hr_obj = new JSONObject(hourly);

            String data = hr_obj.getString("data");
            JSONArray d_array = new JSONArray(data);

            displayHourly(d_array);

            //Retrieve sunrise/sunset data
            String daily = jsonObject.getString("daily");
            JSONObject daily_obj = new JSONObject(daily);

            String daily_data = daily_obj.getString("data");
            JSONArray dailyDataArray = new JSONArray(daily_data);

            JSONObject dataRetrieve = dailyDataArray.getJSONObject(0);
            String sunRise = convertTime(dataRetrieve.getString("sunriseTime"));
            String sunSet = convertTime(dataRetrieve.getString("sunsetTime"));
            //set info to textviews
            sunriseTime.setText(sunRise);
            sunsetTime.setText(sunSet);




        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //color change
    public void setColorsByTemp(String temp){
        //Toolbar,background,hourly background(index colors),sunrise/sunset background

        View headerToolbar = findViewById(R.id.header_tlbr);
        View background = findViewById(R.id.background);
        View currentDisplay = findViewById(R.id.current_display);

        //hourly index
        View index_1 = findViewById(R.id.index1);
        View index_2 = findViewById(R.id.index2);
        View index_3 = findViewById(R.id.index3);
        View index_4 = findViewById(R.id.index4);
        View index_5 = findViewById(R.id.index5);
        View index_6 = findViewById(R.id.index6);

        //Sunrise-Sunset view
        View sunrise = findViewById(R.id.sunrise);
        View sunset = findViewById(R.id.sunset);


        int tempIn = Integer.parseInt(temp);

        //warm color
        if(tempIn >=60){
           headerToolbar.setBackgroundResource(R.color.warm_back);
           background.setBackgroundResource(R.color.warm_back);

           currentDisplay.setBackgroundResource(R.color.warm_secondary);
            index_1.setBackgroundResource(R.color.warm_secondary);
            index_2.setBackgroundResource(R.color.warm_secondary);
            index_3.setBackgroundResource(R.color.warm_secondary);
            index_4.setBackgroundResource(R.color.warm_secondary);
           index_5.setBackgroundResource(R.color.warm_secondary);
           index_6.setBackgroundResource(R.color.warm_secondary);
            sunrise.setBackgroundResource(R.color.warm_secondary);
            sunset.setBackgroundResource(R.color.warm_secondary);
       }else{

            headerToolbar.setBackgroundResource(R.color.cool);
            background.setBackgroundResource(R.color.cool);

           currentDisplay.setBackgroundResource(R.color.cool_curr);
            index_1.setBackgroundResource(R.color.cool_curr);
            index_2.setBackgroundResource(R.color.cool_curr);
            index_3.setBackgroundResource(R.color.cool_curr);
            index_4.setBackgroundResource(R.color.cool_curr);
            index_5.setBackgroundResource(R.color.cool_curr);
            index_6.setBackgroundResource(R.color.cool_curr);
            sunrise.setBackgroundResource(R.color.cool_curr);
            sunset.setBackgroundResource(R.color.cool_curr);
        }



    }

    //interface
    public void displayHourly(JSONArray array) throws JSONException {


        String[] hourlyTime = new String[6];
        String[] iconData = new String[6];
        String[] hourlyTemp = new String[6];


        //fill the string arrays with corresponding data
        fillArray(array, hourlyTime, "time", 0);
        fillArray(array, iconData, "icon", 0);
        fillArray(array, hourlyTemp, "temperature", 0);

        //Check the unit type
        if(unitChange.equals("Celsius"))
            hourlyTemp = setMultipleItemsCelsius(hourlyTemp,0);



        setHourlyInfo(hourlyTime, hourlyTemp, 0);
        setHighLowData(hourlyTime, hourlyTemp);
    }

    //set data from JSONArray into array
    public void fillArray(JSONArray array, String[] testingArray, String obj, int counter) throws JSONException {


    //use a switch-case
        switch (obj) {
            case "time":
                if (counter <= testingArray.length - 1) {

                    //get data for current position
                    JSONObject hr_info = array.getJSONObject(counter);
                    testingArray[counter] = convertTime(hr_info.getString(obj));// converts unix time to readable

                    //move to the next index
                    fillArray(array, testingArray, obj, counter + 1);
                }
                break;

            case "icon":
                //call icons array
                getIcons();

                if (counter <= testingArray.length - 1) {
                    //get data for current position
                    JSONObject hr_info = array.getJSONObject(counter);
                    testingArray[counter] = hr_info.getString(obj);// place object in the index

                    //call iconSelection -> sets hourly icons
                    iconSelection(testingArray[counter], hr_icons[counter]);

                    //move onto the next index
                    fillArray(array, testingArray, obj, counter + 1);
                }
                break;
            case "temperature":
                if (counter <= testingArray.length - 1) {

                    //get data for current position
                    JSONObject hr_info = array.getJSONObject(counter);
                    testingArray[counter] = toSingleInt(hr_info.getString(obj));// converts double to int

                    //move onto the next index
                    fillArray(array, testingArray, obj, counter + 1);
                }
                break;
        }

    }


    //set the text for hourly display
    public void setHourlyInfo(String[] hourlyTime, String[] hourlyTemp, int counter) {


        String degree = getString(R.string.degree_unicode);
        //call the views
        hourlyViews();

        //check the unit type

        //set hourly times and temperatures
        if (counter <= hourlyTime.length - 1) {


            hr_time[counter].setText(hourlyTime[counter]);
            hr_temp[counter].setText(String.format("%s%s", hourlyTemp[counter], degree));


            setHourlyInfo(hourlyTime, hourlyTemp, counter + 1);
        }


    }


    public void setHighLowData(String[] hourlyTime, String[] hourlyTemp) {

        String degree = getString(R.string.degree_unicode);
        int last = hourlyTemp.length - 1;
        int high_index,low_index;

        //retrieve views for high and low
        getHighLowViews();

        //check unit type
        if(unitChange.equals("Celsius")){
            int high = Integer.parseInt(hourlyTemp[last]);
            int low = Integer.parseInt(hourlyTemp[0]);

            high_index = findHighLow(hourlyTemp, high, last, "high", 0);
            low_index = findHighLow(hourlyTemp, low, 0, "low", 0);

        }else{//Default: Fahrenheit

        //set high and low
        double high = Double.parseDouble(hourlyTemp[last]);//grabs the last index
        double low = Double.parseDouble(hourlyTemp[0]);//grabs the first
        //convert to int
        int h = (int) high;
        int l = (int) low;

        high_index = findHighLow(hourlyTemp, h, last, "high", 0);
        low_index = findHighLow(hourlyTemp, l, 0, "low", 0);
        }

        //set the high and low views
        higtemp_display.setText(String.format("%s%s", hourlyTemp[high_index], degree));
        lowtemp_display.setText(String.format("%s%s", hourlyTemp[low_index], degree));


    }

    //find the high and low temps of the day
    public int findHighLow(String[] array, int highLow, int index, String type, int i) {

        if (i == array.length)
            return index;

        double convert = Double.parseDouble(array[i]);
        int test = (int) convert;

        switch (type) {
            case ("low"):
                if (test < highLow) {
                    highLow = test;
                    index = i;
                }
                break;

            case ("high"):
                if (test > highLow) {
                    highLow = test;
                    index = i;
                }
                break;
        }

        return (findHighLow(array, highLow, index, type, i + 1));
    }


    //sets icon images for hourly weather
    public void iconSelection(String icon, View icon_image) {


        //replace punctuation w/ a space
        String replace = icon.replaceAll("-", " ");


        //initiate switch case
        switch (replace) {

            case "clear day":
                icon_image.setBackgroundResource(R.drawable.clear_day);
                break;
            case "clear night":
                icon_image.setBackgroundResource(R.drawable.clear_night);
                break;
            case "cloudy":
                icon_image.setBackgroundResource(R.drawable.cloudy);
                break;
            case "fog":
                icon_image.setBackgroundResource(R.drawable.fog);
                break;
            case "rain":
                icon_image.setBackgroundResource(R.drawable.rain);
                break;
            case "partly cloudy day":
                icon_image.setBackgroundResource(R.drawable.partly_cloudy_day);
                break;
            case "partly cloudy night":
                icon_image.setBackgroundResource(R.drawable.partly_cloudy_night);
                break;
            case "sleet":
                icon_image.setBackgroundResource(R.drawable.sleet);
                break;
            case "snow":
                icon_image.setBackgroundResource(R.drawable.snow);
                break;
            case "wind":
                icon_image.setBackgroundResource(R.drawable.wind);
                break;
        }


    }


    //converts temp from double to single int
    public String toSingleInt(String value) {

        double val = Double.valueOf(value);
        int we = (int) val;
        return String.valueOf(we);
    }

    //converts from unix time to readable time
    public String convertTime(String input) {

        //convert time String into a long: 2 versions
        long epochTime = Long.valueOf(input);
        //short epT = Short.valueOf(input);

        //convert into milliseconds
        Date time = new Date(epochTime * 1000L);


        //create time format to be displayed
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm aa");//changes this to 12 hour am/pm

        String re = sdf.format(time);

        //return formatted time
        return re;

    }


    //converts from Fahrenheit to Celsius
    public String toCelsius(String value) {

        value = toSingleInt(value);
        int convert = (Integer.parseInt(value) - 32) * 5 / 9;
        return (String.valueOf(convert));
    }
    //converts multiple items from Fahrenheit to Celsius
    public String[]setMultipleItemsCelsius(String[] arrayIn,int counter){

        if(counter == arrayIn.length)
            return arrayIn;

        arrayIn[counter] = toSingleInt(arrayIn[counter]);
        int convert = (Integer.parseInt(arrayIn[counter])-32)*5/9;
        arrayIn[counter] = String.valueOf(convert);

        return setMultipleItemsCelsius(arrayIn, counter+1);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Retrieve data from settings
        SharedPreferences pfs = PreferenceManager.
                getDefaultSharedPreferences(getBaseContext());

        zipInput = pfs.getString("zip_info", "60605");
        unitChange = pfs.getString("unit_info", "Fahrenheit");


        //Show all the weather data
        displayWeather();

        //Tests if the zip code is valid: Message will display if true
        if(!ZipCodeApi.isZipValid) {

            ZipCodeApi.isZipValid = false;

            Context context = getApplicationContext();
            CharSequence text = "ZipCode invalid!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context,text,duration);
            toast.show();
            toast.setGravity(Gravity.TOP| Gravity.CENTER, 0, 0);

        }
        //Connect toolbar
        Toolbar appToolBar = findViewById(R.id.header_tlbr);
        setSupportActionBar(appToolBar);
        setTitle("");




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       //inflate menu
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //add refresh button here?
        switch(item.getItemId()){
            case R.id.menu_settings:
               startActivity(new Intent(this,Settings.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.weatherocity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class Settings extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      /* Button settings_btn = findViewById(R.id.settings_btn);
       settings_btn.setOnClickListener(v-> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });*/


/*
        Toolbar settings = findViewById(R.id.settings_tlbr);
        setSupportActionBar(settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           // getSupportActionBar().setLogo(R.mipmap.ic_launcher);
           // getSupportActionBar().setDisplayUseLogoEnabled(true);
        }*/


        getFragmentManager().beginTransaction().
                replace(android.R.id.content, new FragmentPreferenceItem()).commit();


    }

    public void previous(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public static class FragmentPreferenceItem extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_preference);
        }
    }
}

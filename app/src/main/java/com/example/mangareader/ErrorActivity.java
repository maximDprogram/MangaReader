package com.example.mangareader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

    }

    public void onBackPressed() {
        finish();
    }
}


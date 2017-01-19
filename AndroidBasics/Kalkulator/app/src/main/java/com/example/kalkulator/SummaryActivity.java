package com.example.kalkulator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import static org.apache.log4j.BasicConfigurator.configure;


public class SummaryActivity extends AppCompatActivity {
    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        configure();
        logger.getRootLogger();
        logger.getRootLogger().setLevel(Level.ERROR);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_summary);
        layout.addView(textView);
    }

    public void backToMain(View v) {
        finish();
    }
}

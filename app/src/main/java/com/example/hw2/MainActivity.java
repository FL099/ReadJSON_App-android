package com.example.hw2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private Button loadBtn;
    private TextView outView;
    private int pgNum = 1;
    private static final String KEY_TEXT = "resultText";
    private static final String KEY_INT = "Seitennummer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadBtn = findViewById(R.id.btn_load);
        outView = findViewById(R.id.output_view);
        outView.setMovementMethod(new ScrollingMovementMethod());

        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBtn.setEnabled(false);
                outView.setText("loading...");

                loadWebResult(pgNum);
                pgNum++;
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_TEXT)) {
                outView.setText(savedInstanceState.getString(KEY_TEXT));
                pgNum = savedInstanceState.getInt(KEY_INT);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TEXT, outView.getText().toString());
        outState.putInt(KEY_INT, pgNum);
    }

    private void loadWebResult(int pgNum) {

        WebRunnable webRunnable = new WebRunnable("https://api.magicthegathering.io/v1/cards?page="+ pgNum);
        new Thread(webRunnable).start();

    }

    class WebRunnable implements Runnable {

        URL url;

        WebRunnable(String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            Handler mainHandler = new Handler(Looper.getMainLooper());

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");



                String out = "";


                if (scanner.hasNext()) {

                    JSONObject root = new JSONObject(scanner.next());
                    JSONArray results = root.getJSONArray("cards");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);

                        MagicCard mc = new MagicCard(result);
                        out += mc.getCardData();


                    }

                    String finalOut = out;
                    mainHandler.post(() -> outView.setText(finalOut));
                }


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            mainHandler.post(() -> loadBtn.setEnabled(true));
        }
    }
}
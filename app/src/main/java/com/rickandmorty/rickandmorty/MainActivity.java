package com.rickandmorty.rickandmorty;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    List<Character> characterList;
    RecyclerView all_characters_recyclerView;
    GridLayoutManager gridLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.characterList = new ArrayList<>();
        all_characters_recyclerView = findViewById(R.id.all_characters_recyclerView);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        all_characters_recyclerView.setHasFixedSize(true);
        all_characters_recyclerView.setLayoutManager(gridLayoutManager);

        new Load_Characters(characterList, all_characters_recyclerView, MainActivity.this).execute("https://rickandmortyapi.com/api/character");

    }

}
package com.rickandmorty.rickandmorty;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;

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

public class Detail extends AppCompatActivity {

    List<Character> characterList;
    RecyclerView all_characters_recyclerView;
    GridLayoutManager gridLayoutManager;

    Character character = null;
    LinearLayout back_button;
    ImageView character_image;
    ImageView status_type;
    TextView character_name,current_location,first_seen_in,status,also_from;
    List<String> all_characters_from_origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        all_characters_from_origin = new ArrayList<>();
        back_button = findViewById(R.id.back_button);
        character_image = findViewById(R.id.character_image);
        character_name = findViewById(R.id.character_name);
        current_location = findViewById(R.id.current_location);
        first_seen_in = findViewById(R.id.first_seen_in);
        status = findViewById(R.id.status);
        status_type = findViewById(R.id.status_type);
        also_from = findViewById(R.id.also_from);

        characterList = new ArrayList<>();
        all_characters_recyclerView = findViewById(R.id.all_characters_recyclerView);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        all_characters_recyclerView.setHasFixedSize(true);
        all_characters_recyclerView.setLayoutManager(gridLayoutManager);

        Intent intent = getIntent();
        character = (Character)intent.getSerializableExtra("character");
        System.out.println("-----------------------------------------------------");
        System.out.println(character.getId());
        System.out.println(character.getName());
        System.out.println(character.getGender());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(Detail.this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(35f);
        circularProgressDrawable.start();

        Glide.with(Detail.this)
                .load(character.getImage())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(15)))
                .placeholder(circularProgressDrawable)
                .into(character_image);
        character_name.setText(character.getName());
        current_location.setText(character.getLocation());
        status.setText(character.getStatus());
        first_seen_in.setText(character.getEpisode());
        also_from.setText("Also from \"" + character.getLocation() + "\"");
        switch (character.getStatus()){
            case "Alive":
                status_type.setImageDrawable(getDrawable(R.drawable.status_alive));
                break;
            case "Dead":
                status_type.setImageDrawable(getDrawable(R.drawable.status_dead));
                break;
            case "unknown":
                status_type.setImageDrawable(getDrawable(R.drawable.status_unknown));
                break;
        }
        new Load_character_from_origin().execute(character.getLocation_url());



        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    public class Load_character_from_origin extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
                StringBuilder buffer = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            System.out.println("resultat origin  == "+ result);


            JSONObject jsonObjectresult = null;
            try {
                if( result != null ){
                    jsonObjectresult = new JSONObject(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = "https://rickandmortyapi.com/api/character/";
            if (jsonObjectresult != null) {
                try {
                    JSONArray jsonArray = jsonObjectresult.getJSONArray("residents");
                    for (int i = 0; i < jsonArray.length(); i++){
                        String resident = jsonArray.getString(i);
                        String[] url_split = resident.split("/");
                        if (i < jsonArray.length()-1)
                            url = url + url_split[url_split.length-1] + ",";
                        else
                            url = url + url_split[url_split.length-1];
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("URL_characters" + url);
                new Load_Characters_from().execute(url);
            }
        }
    }



    public class Load_Characters_from extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
                StringBuilder buffer = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            System.out.println("resultat test == "+ result);



            JSONArray result_array = null;
            try {
                if( result != null ){
                    result_array = new JSONArray(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                try {
                    if( result != null ) {
                        JSONObject jsonObject = new JSONObject(result);
                        result_array = new JSONArray();
                        result_array.put(jsonObject);
                    }
                }catch (Exception ee){
                    ee.printStackTrace();
                }
            }

            if (result_array != null) {

                for (int i = 0; i < result_array.length();i++){
                    try {
                        JSONObject jsonObject = result_array.getJSONObject(i);
                        Character newCharacter = new Character();
                        newCharacter.setId(jsonObject.getString("id"));
                        newCharacter.setName(jsonObject.getString("name"));
                        newCharacter.setStatus(jsonObject.getString("status"));
                        newCharacter.setSpecies(jsonObject.getString("species"));
                        newCharacter.setType(jsonObject.getString("type"));
                        newCharacter.setGender(jsonObject.getString("gender"));
                        newCharacter.setImage(jsonObject.getString("image"));
                        newCharacter.setUrl(jsonObject.getString("url"));
                        newCharacter.setCreated(jsonObject.getString("created"));
                        newCharacter.setLocation(jsonObject.getJSONObject("location").getString("name"));
                        newCharacter.setLocation_url(jsonObject.getJSONObject("location").getString("url"));

                        String episode_url = jsonObject.getJSONArray("episode").getString(0);
                        String[] episode_url_split = episode_url.split("/");
                        newCharacter.setEpisode_id(episode_url_split[episode_url_split.length-1]);

                        characterList.add(newCharacter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                String url_episode_list = "https://rickandmortyapi.com/api/episode/";
                for (int i = 0; i < characterList.size(); i++){
                    if (i < characterList.size()-1){
                        url_episode_list += characterList.get(i).getEpisode_id() + ",";
                    }else {
                        url_episode_list += characterList.get(i).getEpisode_id();
                    }
                }

                System.out.println("show_url = " + url_episode_list);
                new Load_first_episode(characterList,all_characters_recyclerView,Detail.this).execute(url_episode_list);
            }
        }
    }

}
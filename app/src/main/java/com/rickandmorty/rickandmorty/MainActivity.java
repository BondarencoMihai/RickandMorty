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

        new Load_Characters().execute();

    }


    public class Load_Characters extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://rickandmortyapi.com/api/character");
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
//                connection.setDoInput(true);
//                connection.setDoOutput(true);

//                OutputStream os = connection.getOutputStream();
////                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//////                writer.write("");
////                writer.flush();
////                writer.close();
//                os.close();

                connection.connect();


                System.out.println("print stree =" + connection.getResponseCode() + "\nmesajul de raspuns = " + connection.getResponseMessage());
//                InputStream stream = connection.getInputStream();
//                reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
                reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "UTF-8"),  8);
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
            System.out.println("resultat == "+ result);


            JSONObject jsonObjectresult = null;
            try {
                if( result != null ){
                    jsonObjectresult = new JSONObject(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonObjectresult != null) {
                JSONArray result_array = null;
                try {
                    result_array = jsonObjectresult.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                        characterList.add(newCharacter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                CharacterAdapter characterAdapter = null;
                try {
                    characterAdapter = new CharacterAdapter(characterList,getApplicationContext());
                }catch (Exception e){
                    e.getStackTrace();
                }
                all_characters_recyclerView.setAdapter(characterAdapter);
            }else{

            }


        }
    }
}
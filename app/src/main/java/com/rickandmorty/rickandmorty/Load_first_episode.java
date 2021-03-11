package com.rickandmorty.rickandmorty;

import android.content.Context;
import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Load_first_episode extends AsyncTask<String, String, String> {

    List<Character> characterList;
    RecyclerView all_characters_recyclerView;
    Context context;
    public Load_first_episode(List<Character> characterList,RecyclerView all_characters_recyclerView, Context context){
        this.characterList = characterList;
        this.all_characters_recyclerView = all_characters_recyclerView;
        this.context = context;
    }

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
        System.out.println("ALL_episode = " + result);
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

            for (int i = 0; i < result_array.length();i++) {
                try {
                    JSONObject episode_obj = result_array.getJSONObject(i);
                    for (int x = 0; x < characterList.size(); x ++){
                        if (characterList.get(x).getEpisode_id().equals(episode_obj.getString("id"))){
                            characterList.get(x).setEpisode(episode_obj.getString("name"));
                        }
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            CharacterAdapter characterAdapter = null;
            try {
                characterAdapter = new CharacterAdapter(characterList,context);
            }catch (Exception e){
                e.getStackTrace();
            }
            all_characters_recyclerView.setAdapter(characterAdapter);
        }
    }
}

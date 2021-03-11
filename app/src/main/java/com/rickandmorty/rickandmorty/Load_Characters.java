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

public class Load_Characters extends AsyncTask<String, String, String> {

    private List<Character> characterList;
    private RecyclerView all_characters_recyclerView;
    private Context context;

    public Load_Characters(List<Character> characterList,RecyclerView all_characters_recyclerView, Context context){
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
            new Load_first_episode(characterList,all_characters_recyclerView,context).execute(url_episode_list);
        }else{

        }


    }
}

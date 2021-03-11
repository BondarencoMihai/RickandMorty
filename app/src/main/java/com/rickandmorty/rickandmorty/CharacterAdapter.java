package com.rickandmorty.rickandmorty;

import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder> {
    List<Character> characterList;
    Context context;


    public CharacterAdapter(List<Character> characterList, Context context){
            this.characterList = characterList;
            this.context = context;
        }


    @Override
    public CharacterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_layout, parent, false);
            return new ViewHolder(layout);
        }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Character character = characterList.get(position);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(35f);
        circularProgressDrawable.start();


        Glide.with(context)
                .load(character.getImage())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(15)))
                .placeholder(circularProgressDrawable)
                .into(holder.character_image);

        holder.character_name.setText(character.getName());
        holder.character_origin.setText(character.getLocation());
        holder.character_episode.setText(character.getEpisode());
        holder.curen_character.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Detail.class);
                intent.putExtra("character", character);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                if (context instanceof Detail){
                    ((Detail) context).finish();
                }

            }
        });
    }


    @Override
    public int getItemCount() {
            return this.characterList.size();
            }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView character_image;
        private TextView character_name,character_origin,character_episode;
        private LinearLayout curen_character;


        public ViewHolder(View view) {
            super(view);
            this.character_image = view.findViewById(R.id.character_image);
            this.character_name = view.findViewById(R.id.character_name);
            this.character_origin = view.findViewById(R.id.character_origin);
            this.character_episode = view.findViewById(R.id.character_episode);
            this.curen_character = view.findViewById(R.id.curen_character);
        }

}
}
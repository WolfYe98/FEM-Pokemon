package com.example.pokemon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.pokemon.firestore.documents.PokemonEntity;

public class PokemonDetailsActivity extends AppCompatActivity {
    private TextView tvName;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvLiked;
    private TextView tvDisliked;
    private ImageView ivPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_details);

        this.tvName = findViewById(R.id.tvPokemonDetailsName);
        this.tvHeight = findViewById(R.id.tvPokemonDetailsHeight);
        this.tvWeight = findViewById(R.id.tvPokemonDetailsWeight);
        this.tvLiked = findViewById(R.id.tvPokemonDetailsLikedUsers);
        this.tvDisliked = findViewById(R.id.tvPokemonDetailsDislikedUsers);
        this.ivPokemon = findViewById(R.id.ivPokemonDetails);
        this.setDetails();
    }

    private void setDetails() {
        Intent intent = getIntent();
        PokemonEntity pokemon = intent.getParcelableExtra("pokemon_details", PokemonEntity.class);
        Glide.with(this)
                .load(pokemon.getImageUrl())
                .fitCenter()
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher_foreground)
                        .error(R.mipmap.ic_launcher_foreground)
                )
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(this.ivPokemon);
        this.setDetailsText(pokemon);
    }

    private void setDetailsText(PokemonEntity pokemon) {
        String heigthString = Double.valueOf(pokemon.getHeight()) / 10.0 + " M";
        String weightString = Double.valueOf(pokemon.getWeight()) / 10.0 + " KG";
        this.tvName.setText(pokemon.getName().toUpperCase());
        this.tvHeight.setText(heigthString);
        this.tvWeight.setText(weightString);
        this.tvLiked.setText(pokemon.getLikedUsers().size() + " USERS");
        this.tvDisliked.setText(pokemon.getDislikedUsers().size() + " USERS");
    }

}
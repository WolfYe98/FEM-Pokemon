package com.example.pokemon.firestore;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.pokemon.PokemonDetailsActivity;
import com.example.pokemon.R;
import com.example.pokemon.firestore.documents.PokemonEntity;

public class PokemonEntityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final ImageView ivItemPokemon;
    private final TextView tvPokemonTitle;
    private final LinearLayout llItem;
    private PokemonEntity pokemon;

    public PokemonEntityHolder(@NonNull View itemView) {
        super(itemView);
        this.ivItemPokemon = itemView.findViewById(R.id.ivItemPokemon);
        this.tvPokemonTitle = itemView.findViewById(R.id.tvPokemonTitle);
        this.llItem = itemView.findViewById(R.id.llItem);
        itemView.setOnClickListener(this);
    }

    public void bind(PokemonEntity pokemon, String uid) {
        this.pokemon = pokemon;
        Glide.with(itemView.getContext())
                .load(pokemon.getImageUrl())
                .fitCenter()
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher_foreground)
                        .error(R.mipmap.ic_launcher_foreground)
                )
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(this.ivItemPokemon);
        this.tvPokemonTitle.setText(pokemon.getName().toUpperCase());
        if (pokemon.getLikedUsers().contains(uid)) {
            this.llItem.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
        } else {
            this.llItem.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.primaryColor));
        }
    }

    @Override
    public void onClick(View v) {
        Log.i("item", "clicked: " + this.pokemon.getName());
        Intent intent = new Intent(itemView.getContext(), PokemonDetailsActivity.class);
        intent.putExtra("pokemon_details", this.pokemon);
        itemView.getContext().startActivity(intent);
    }
}

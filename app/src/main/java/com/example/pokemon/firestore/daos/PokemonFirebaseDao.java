package com.example.pokemon.firestore.daos;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pokemon.firestore.documents.PokemonEntity;
import com.example.pokemon.pokeapiREST.models.Pokemon;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PokemonFirebaseDao {
    private static final String COLLECTION_NAME = "pokemon";
    private static final CollectionReference pokemonCollection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    private static final String LIKED_USERS = "likedUsers";
    private static final String DISLIKED_USERS = "dislikedUsers";
    private final String LOG_TAG;

    private final Activity activity;

    public PokemonFirebaseDao(@NonNull Activity activity) {
        this.activity = activity;
        this.LOG_TAG = activity.getLocalClassName() + " -> PokemonFirebaseDao";
    }

    public Query getPokemonsLikedByUserQuery(String uid) {
        return pokemonCollection.where(Filter.or(Filter.arrayContains(LIKED_USERS, uid), Filter.arrayContains(DISLIKED_USERS, uid)));
    }

    public void addOrUpdatePokemonWithUser(@NonNull Pokemon pokemonModel, String userID, boolean liked) {
        PokemonEntity pokemon = new PokemonEntity(pokemonModel);
        DocumentReference reference = pokemonCollection.document(pokemon.getId());
        reference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().exists()) {
                        pokemon.addLikedOrDislikedUser(liked, userID);
                        pokemonCollection.document(pokemon.getId()).set(pokemon)
                                .addOnFailureListener(activity,
                                        e -> Log.e(LOG_TAG, "Failed to write pokemon to db", e)
                                );
                    } else if (task.getResult().exists()) {
                        PokemonEntity dbPokemon = task.getResult().toObject(PokemonEntity.class);
                        dbPokemon.addLikedOrDislikedUser(liked, userID);
                        pokemonCollection.document(dbPokemon.getId()).update(LIKED_USERS, dbPokemon.getLikedUsers())
                                .addOnCompleteListener(task1 -> {
                                    Log.i(LOG_TAG, "Update complete");
                                });
                        pokemonCollection.document(dbPokemon.getId()).update(DISLIKED_USERS, dbPokemon.getDislikedUsers())
                                .addOnCompleteListener(task1 -> {
                                    Log.i(LOG_TAG, "Update complete");
                                });
                    } else {
                        Log.i(LOG_TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}

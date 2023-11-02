package com.example.pokemon;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemon.firestore.PokemonEntityHolder;
import com.example.pokemon.firestore.daos.PokemonFirebaseDao;
import com.example.pokemon.firestore.documents.PokemonEntity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HistoryActivity extends AppCompatActivity {
    private static final String LOG_TAG = "HistoryActivity";

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    private String uid;
    private PokemonFirebaseDao pokemonFirebaseDao;
    private Query pokemonByUserQuery;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        this.uid = FirebaseAuth.getInstance().getUid();
        pokemonFirebaseDao = new PokemonFirebaseDao(this);
        pokemonByUserQuery = pokemonFirebaseDao.getPokemonsLikedByUserQuery(this.uid);
        this.recyclerView = findViewById(R.id.rvHistory);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final RecyclerView.Adapter adapter = newAdapter();
        this.recyclerView.setAdapter(adapter);
    }

    @NonNull
    private RecyclerView.Adapter newAdapter() {
        FirestoreRecyclerOptions<PokemonEntity> options =
                new FirestoreRecyclerOptions.Builder<PokemonEntity>()
                        .setQuery(pokemonByUserQuery, PokemonEntity.class)
                        .setLifecycleOwner(this)
                        .build();
        Log.i(LOG_TAG, "creating new adapter");
        return new FirestoreRecyclerAdapter<PokemonEntity, PokemonEntityHolder>(options) {
            @NonNull
            @Override
            public PokemonEntityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new PokemonEntityHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.history_item, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull PokemonEntityHolder holder, int position, @NonNull PokemonEntity model) {
                holder.bind(model, uid);
            }
        };
    }
}
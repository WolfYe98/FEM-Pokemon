package com.example.pokemon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.pokemon.firestore.daos.PokemonFirebaseDao;
import com.example.pokemon.pokeapiREST.models.Pokemon;
import com.example.pokemon.pokeapiREST.services.PokemonRESTAPIService;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 2019;
    private static final int MAX_ID = 1000;
    private static final int MIN_ID = 1;
    private static String BASE_URL = "https://pokeapi.co/";
    private static String LOG_TAG = "Pokemon - Main";
    private static String LOG_TAG_ERROR = "ERROR: Pokemon - Main";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private TextView tvUserName;
    private TextView tvPokemonName;
    private Button btStart;
    private Button btLike;
    private Button btDislike;
    private LinearLayout llButtons;
    private ImageView ivPokemon;
    private PokemonRESTAPIService pokemonService;
    private Pokemon currentPokemon;
    private PokemonFirebaseDao pokemonFirebaseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuthListener = this::signIn;
        tvUserName = findViewById(R.id.tvUserName);
        tvPokemonName = findViewById(R.id.tvPokemonName);
        ivPokemon = findViewById(R.id.ivPokemon);
        this.addBtns();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pokemonService = retrofit.create(PokemonRESTAPIService.class);
        pokemonFirebaseDao = new PokemonFirebaseDao(this);
    }

    private void addBtns() {
        btStart = findViewById(R.id.btStart);
        btStart.setOnClickListener(this::btnStartClick);

        llButtons = findViewById(R.id.llButtons);

        btLike = findViewById(R.id.btLike);
        btLike.setOnClickListener(btn -> this.btnLikeDislikeClicked(btn, true));
        btDislike = findViewById(R.id.btDislike);
        btDislike.setOnClickListener(btn -> this.btnLikeDislikeClicked(btn, false));
    }

    private void btnLikeDislikeClicked(View btn, boolean liked) {
        pokemonFirebaseDao.addOrUpdatePokemonWithUser(this.currentPokemon, this.mFirebaseAuth.getUid(), liked);
        this.getPokemonFromExternalAPI();
    }

    private void signIn(FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            this.btStart.setEnabled(false);
            startActivityForResult(
                    AuthUI.getInstance().
                            createSignInIntentBuilder().
                            setAvailableProviders(Collections.singletonList(
                                    new AuthUI.IdpConfig.EmailBuilder().build()
                            )).
                            setIsSmartLockEnabled(!BuildConfig.DEBUG, true).
                            build(),
                    RC_SIGN_IN);
        } else {
            Log.i(LOG_TAG, "User: " + user.getEmail() + " loged in");
            this.btStart.setEnabled(true);
            tvUserName.setText(user.getDisplayName());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.opLogout:
                this.signOut();
                Log.i(LOG_TAG, "Signed out");
                return true;
            case R.id.opHistory:
                this.openHistoryActivity();
                return true;
        }
        return true;
    }

    private void openHistoryActivity() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    private void signOut() {
        this.mFirebaseAuth.signOut();
        this.tvUserName.setText(getString(R.string.pokemonTitle));
        this.llButtons.setVisibility(View.GONE);
        this.btStart.setVisibility(View.VISIBLE);
        this.ivPokemon.setImageDrawable(getDrawable(R.mipmap.ic_launcher_foreground));
        this.tvPokemonName.setText("");
        this.currentPokemon = null;
    }

    private void btnStartClick(View view) {
        this.btStart.setVisibility(View.GONE);
        this.getPokemonFromExternalAPI();
    }

    private void createSnackBarWithMessageID(int id) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(id),
                Snackbar.LENGTH_LONG
        ).show();
    }

    private void getPokemonFromExternalAPI() {
        Random random = new Random();
        int randomID = (random.nextInt(MAX_ID - MIN_ID) + MIN_ID) % MAX_ID;
        Call<Pokemon> pokemonAsyncCall = pokemonService.getPokemonByID(randomID);
        pokemonAsyncCall.enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                Pokemon pokemon = response.body();
                Log.i(LOG_TAG, "Pokemon " + pokemon.getName() + " getted");
                llButtons.setVisibility(View.VISIBLE);
                setPokemonImage(pokemon.getSprites().getFrontDefault());
                tvPokemonName.setText(pokemon.getName().toUpperCase());
                currentPokemon = pokemon;
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                Log.i(LOG_TAG_ERROR, Objects.requireNonNull(t.getMessage()));
                llButtons.setVisibility(View.GONE);
                btStart.setVisibility(View.VISIBLE);
                tvPokemonName.setText("");
                createSnackBarWithMessageID(R.string.errorMessage);
                currentPokemon = null;
            }
        });
    }

    private void setPokemonImage(String url) {
        Log.i(LOG_TAG, "Setting pokemon image");
        Glide.with(this)
                .load(url)
                .fitCenter()
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher_foreground)
                        .error(R.mipmap.ic_launcher_foreground)
                )
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(this.ivPokemon);
    }

}
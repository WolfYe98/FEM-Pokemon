package com.example.pokemon.pokeapiREST.services;

import com.example.pokemon.pokeapiREST.models.Pokemon;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PokemonRESTAPIService {
    @GET("api/v2/pokemon/{id}")
    Call<Pokemon> getPokemonByID(@Path("id") Integer id);
}

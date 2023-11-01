package com.example.pokemon.firestore.documents;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.pokemon.pokeapiREST.models.Pokemon;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@IgnoreExtraProperties
public class PokemonEntity implements Parcelable {
    @PropertyName("id")
    private String id;
    @PropertyName("height")
    private Integer height;
    @PropertyName("name")
    private String name;
    @PropertyName("image_url")
    private String imageUrl;
    @PropertyName("weight")
    private Integer weight;
    @PropertyName("likedUsers")
    List<String> likedUsers;
    @PropertyName("dislikedUsers")
    List<String> dislikedUsers;
    public PokemonEntity(){

    }
    public PokemonEntity(Pokemon pokemonModel){
        this.fromModel(pokemonModel);
    }
    private void fromModel(Pokemon pokemonModel){
        this.id = pokemonModel.getId().toString();
        this.name = pokemonModel.getName();
        this.height = pokemonModel.getHeight();
        this.imageUrl = pokemonModel.getSprites().getFrontDefault();
        this.weight = pokemonModel.getWeight();
    }
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public Integer getHeight() {
        return height;
    }

    public void setHeight(@NonNull Integer height) {
        this.height = height;
    }
    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }
    @NonNull
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@NonNull String imageUrl) {
        this.imageUrl = imageUrl;
    }
    @NonNull
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(@NonNull Integer weight) {
        this.weight = weight;
    }

    public List<String> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(List<String> likedUsers) {
        this.likedUsers = likedUsers;
    }

    public List<String> getDislikedUsers() {
        return dislikedUsers;
    }

    public void setDislikedUsers(List<String> dislikedUsers) {
        this.dislikedUsers = dislikedUsers;
    }
    public void addLikedOrDislikedUser(boolean liked, String userID){
        if(liked){
            this.addLikedUser(userID);
        } else{
            this.addDislikedUser(userID);
        }
    }

    private void addLikedUser(String userID) {
        if(this.userNotIncluded(userID)){
            this.likedUsers.add(userID);
        }else if(this.dislikedUsers != null && this.dislikedUsers.contains(userID)){
            this.dislikedUsers.remove(userID);
            this.likedUsers.add(userID);
        }
    }
    private void addDislikedUser(String userID) {

        if(this.userNotIncluded(userID)){
            this.dislikedUsers.add(userID);
        } else if(this.likedUsers != null && this.likedUsers.contains(userID)){
            this.likedUsers.remove(userID);
            this.dislikedUsers.add(userID);
        }
    }
    private boolean userNotIncluded(String userID){
        if(this.likedUsers == null){
            this.likedUsers = new ArrayList<>();
        }
        if(this.dislikedUsers == null){
            this.dislikedUsers = new ArrayList<>();
        }
        return !this.likedUsers.contains(userID) && !this.dislikedUsers.contains(userID);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokemonEntity that = (PokemonEntity) o;
        return id.equals(that.id)
                && height.equals(that.height) && name.equals(that.name)
                && imageUrl.equals(that.imageUrl) && weight.equals(that.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, height, name, imageUrl, weight);
    }

    @Override
    public String toString() {
        return "PokemonEntity{" +
                "id=" + id +
                ", height=" + height +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", weight=" + weight +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.height);
        dest.writeString(this.name);
        dest.writeString(this.imageUrl);
        dest.writeInt(this.weight);
        dest.writeList(this.likedUsers);
        dest.writeList(this.dislikedUsers);
    }
}

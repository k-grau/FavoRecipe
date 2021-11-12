package com.karlgrauers.favorecipe.models.recipe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


/*
 * Modellklass för receptobjekt. Används för att skapa
 * objekt från API-anrop till Edamams recept-api och
 * för att skapa objekt vars värden lagras i databasen
 * Fält annoterade med '@SerializedName' får
 * sina värden från fält i API-svaret vars namn
 * motsvarar strängen i '@SerializedName'.
 */

@Entity(tableName = "Recipes", indices = {@Index(value = {"id"}, unique = true)})
public class Recipe implements Serializable {

    public Recipe() { }


    /*Uri är unik för varje recept som kommer från
    Edamams API, därför används denna som id. */
    @NonNull
    @PrimaryKey
    @SerializedName("uri")
    @Expose
    private String id = "";

    //Receptets titel
    @SerializedName("label")
    @Expose
    private String label = null;

    //Receptets bild
    @SerializedName("image")
    @Expose
    private String image = null;

    //Receptets url
    @SerializedName("url")
    @Expose
    private String url = null;

    //Typ av kök (alltså franska, italienska, nordiska osv)
    @SerializedName("cuisineType")
    @Expose
    private List<String> cuisineType = null;

    //Typ av måltid
    @SerializedName("mealType")
    @Expose
    private List<String> mealType = null;

    //Typ av maträtt
    @SerializedName("dishType")
    @Expose
    private List<String> dishType = null;

    //Ingredienser som ingår i recept
    @SerializedName("ingredientLines")
    @Expose
    private List<String> ingredientLines = null;


    /* Getters för samtliga fält i klass. */
    @NonNull
    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getLabel() {
        return label;
    }

    public String getImage() {
        return image;
    }

    public List<String>  getCuisineType() {
        return cuisineType;
    }

    public List<String>  getMealType() {
        return mealType;
    }

    public List<String>  getDishType() {
        return dishType;
    }

    public List<String>  getIngredientLines() {
        return ingredientLines;
    }

    /* Setters för samtliga fält i klass. */
    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void  setCuisineType(List<String> cuisineType) {
        this.cuisineType = cuisineType;
    }

    public void  setMealType(List<String> mealType) {
        this.mealType = mealType;
    }

    public void  setDishType(List<String> dishType) {
        this.dishType = dishType;
    }

    public void  setIngredientLines(List<String> ingredientLines) {
        this.ingredientLines = ingredientLines;
    }
}

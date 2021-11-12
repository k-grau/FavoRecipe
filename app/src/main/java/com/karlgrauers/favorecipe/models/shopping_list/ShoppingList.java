package com.karlgrauers.favorecipe.models.shopping_list;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.karlgrauers.favorecipe.models.recipe.Recipe;
import java.io.Serializable;
import java.util.Date;


/*
 * Modellklass för shoppinglisteobjekt. Används för
 * att skapa objekt av shoppinglistor vars värden läggs
 * till i databasen.
 * Klassen har endast fält för id, receptid och tid då
 * objektet skapades. All receptdata kommer direkt från
 * receptobjekt som kopplas ihop med shoppinglistan
 * genom sitt id -som motsvaras av 'recipeId' i denna klass.
 * I databasen har tabell för recept och tabell för shoppinglistor
 * en Många till 1-relation.
 */


@Entity(tableName = "ShoppingLists",
        foreignKeys = {@ForeignKey(entity = Recipe.class, parentColumns = "id", childColumns = "recipeId")})

public class ShoppingList implements Serializable {

    public ShoppingList() { }

    //Primärnyckel
    @PrimaryKey(autoGenerate = true)
    private int id;

    //Främmande nyckel, motsvaras av ett recepts unika id (alltså dess uri)
    @NonNull
    private String recipeId = "";

    //Tid då objekt skapades
    private Date createdAt = null;

    /*Fält annoterat med '@Ignore' då datan i detta
    * ej ska sparas i databasen. Detta fält används i
    * av metod i 'ShoppingListAdapter' när datan plattas
    * ut och ett receptobjekt kopplas på varje shoppingliste-objekt. */
    @Ignore
    private Recipe recipe = null;


    /*Getters för fält */
    public int getId() {
        return id;
    }

    @NonNull
    public String getRecipeId() {
        return recipeId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Recipe getRecipe() {
        return recipe;
    }


    /*Setters för fält */
    public void setId(int id) {
        this.id = id;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setRecipeId(@NonNull String recipeId) {
        this.recipeId = recipeId;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}

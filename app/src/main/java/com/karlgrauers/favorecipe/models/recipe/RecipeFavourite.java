package com.karlgrauers.favorecipe.models.recipe;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;


/*
 * Modellklass för receptfavoritobjekt. Används för
 * att skapa objekt av receptfavoriter vars värden läggs
 * till i databasen.
 * Klassen har endast fält för id, receptid och tid då
 * objektet skapades. All receptdata kommer direkt från
 * receptobjekt som kopplas ihop med favoritobjekt
 * genom sitt id -som motsvaras av 'recipeId' i denna klass.
 * I databasen har tabell för recept och tabell för favoriter
 * en 1 till 1-relation.
 */


@Entity(tableName = "RecipeFavourites",
        foreignKeys = {@ForeignKey(entity = Recipe.class, parentColumns = "id", childColumns = "recipeId")},
        indices = {@Index(value = {"recipeId"}, unique = true)})
public class RecipeFavourite {

    //Primärnyckel
    @PrimaryKey(autoGenerate = true)
    private int id;

    //Främmande nyckel, motsvaras av ett recepts unika id (alltså dess uri)
    private String recipeId;

    //Tid då objektet skapades
    private Date createdAt;


    /* Getters för fält */
    public int getId() {
        return this.id;
    }

    @NonNull
    public String getRecipeId() {
        return this.recipeId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }


    /* Setters för fält */
    public void setId(int id) {
        this.id = id;
    }

    public void setRecipeId(@NonNull String recipeId) {
        this.recipeId = recipeId;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

package com.karlgrauers.favorecipe.models.recipe;

import androidx.room.Embedded;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/*
 * Modellklass för receptobjekt. Används för att skapa
 * objekt från API-anrop till Edamams recept-api.
 * Fält annoterade med '@SerializedName' får
 * sina värden från fält i API-svaret vars namn
 * motsvarar strängen i '@SerializedName'.
 *
 * Denna klass motsvarar träff i resultatlistan
 * som returneras vid anrop till Edamams API.
 */


public class RecipeHit implements Serializable {

    //Referens till klass Recipe
    @Embedded
    @SerializedName("recipe")
    @Expose
    private Recipe recipe;

    //Getter för fält recipe
    public Recipe getRecipe() {
        return recipe;
    }

    //Setter för fält recipe
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }




}

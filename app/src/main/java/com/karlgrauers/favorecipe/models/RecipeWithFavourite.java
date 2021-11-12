package com.karlgrauers.favorecipe.models;
import androidx.room.Embedded;
import androidx.room.Relation;
import com.karlgrauers.favorecipe.models.recipe.Recipe;
import com.karlgrauers.favorecipe.models.recipe.RecipeFavourite;
import org.jetbrains.annotations.Nullable;

/*
 * Modellklass för receptfavorit. Här skapas
 * objekt som innehåller både ett receptobjekt
 * och ett favoritobjekt. De kopplas ihop
 * genom receptobjektets id som motsvaras
 * av favoritobjektets 'recipeId'
 * Relationen mellan tabellerna i databasen är 1 till 1.
 */


public class RecipeWithFavourite {
    @Embedded
    private Recipe recipe;

    /*Definiera relation mellan föräldrakolumn och
      barnkolumn. I detta fall är föräldrakolumnen
      'id' i recepttabellen som motsvaras av 'recipeId'
      i favorittabellen
     */
    @Nullable
    @Relation(parentColumn = "id", entityColumn = "recipeId")
    private RecipeFavourite recipeFavourite;

    //Getter för receptobjekt
    public Recipe getRecipe() {
        return recipe;
    }

    //Getter för favoritobjekt
    public RecipeFavourite getFavourite() {
        return recipeFavourite;
    }

    //Setter för receptobjekt
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    //Getter för receptobjekt
    public void setRecipeFavourite(@androidx.annotation.Nullable RecipeFavourite recipeFavourite) {
        this.recipeFavourite = recipeFavourite;
    }
}

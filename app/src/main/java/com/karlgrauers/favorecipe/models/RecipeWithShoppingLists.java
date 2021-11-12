package com.karlgrauers.favorecipe.models;
import androidx.room.Embedded;
import androidx.room.Relation;
import com.karlgrauers.favorecipe.models.recipe.Recipe;
import com.karlgrauers.favorecipe.models.shopping_list.ShoppingList;
import java.util.List;

/*
 * Modellklass för recept med associerade shoppinglistor.
 * Här skapas objekt som innehåller både receptobjekt
 * och shoppingliste-objekt. De kopplas ihop
 * genom receptobjektets id som motsvaras
 * av shoppinglisteobjektens 'recipeId'
 * Relationen mellan tabellerna i databasen är 1 till Många (ett
 * recept kan alltså ha flera shoppinglistor kopplat till sig).
 */


public class RecipeWithShoppingLists {
    @Embedded
    private Recipe recipe;


    /*Definiera relation mellan föräldrakolumn och
     barnkolumn. I detta fall är föräldrakolumnen
     'id' i recepttabellen som motsvaras av 'recipeId'
     i shoppinglistetabellen.
    */
    @Relation(parentColumn = "id", entityColumn = "recipeId")
    private List<ShoppingList> shoppingLists;

    //Getter för recept
    public Recipe getRecipe() {
        return recipe;
    }

    //Getter för lista med shoppinglistor
    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    //Setter för recept
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }


    //Setter för lista med shoppinglistor
    public void setShoppingLists(List<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }
}

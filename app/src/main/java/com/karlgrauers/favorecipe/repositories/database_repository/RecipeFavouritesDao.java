package com.karlgrauers.favorecipe.repositories.database_repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.karlgrauers.favorecipe.models.RecipeWithFavourite;
import com.karlgrauers.favorecipe.models.recipe.Recipe;
import com.karlgrauers.favorecipe.models.recipe.RecipeFavourite;

import java.util.List;

@Dao
public abstract class RecipeFavouritesDao implements DbBaseDao<Recipe, RecipeFavourite> {

    /*
     * Abstrakt klass som implementerar interface DbBaseDao med typerna
     * 'Recipe' och 'RecipeFavourite'
     */


    //Hämta samtliga receptfavoriter ur databas. Konstruerar objekt som kombinerar recept och receptfavorit.
    //Annoterad med @Transaction för att säkerställa att databasoperation sker atomiskt.
    @Transaction
    @Query("SELECT Recipes.id, Recipes.label, Recipes.dishType, Recipes.mealType, Recipes.dishType, Recipes.cuisineType,  " +
            "Recipes.image, Recipes.url, Recipes.ingredientLines, RecipeFavourites.recipeId, RecipeFavourites.createdAt " +
            "FROM Recipes INNER JOIN RecipeFavourites ON Recipes.id = RecipeFavourites.recipeId ORDER BY createdAt DESC")
    public abstract LiveData<List<RecipeWithFavourite>> getAll();


    /**
     * Radera samtliga receptfavoriter.
     * Radera först receptfavoriterna och kontrollera
     * sedan om recept helt saknar barn, om ja
     * radera recept.
     * Annoterad med @Transaction för att säkerställa
     * att databasoperation sker atomiskt.
     * @return påverkat antal rader i databasen.
     */
    @Transaction
    @Override
    public int deleteAll() throws Exception {
        int rows = 0;
        rows = deleteAllFromChildTable();

        if(rows > 0) {
            deleteChildlessFromParentTable();
        }
        return rows;
    }


    /**
     * Radera receptfavorit via id.
     * Radera först receptfavorit och kontrollera
     * sedan om recept helt saknar barn, om ja
     * radera recept.
     * Annoterad med @Transaction för att säkerställa
     * att databasoperation sker atomiskt.
     * @return påverkat antal rader i databasen.
     */
    @Transaction
    @Override
    public long delete(RecipeFavourite recipeFavourite) throws Exception {
        long rows = 0;
        rows = deleteFromChildTable(recipeFavourite);

        if(rows > 0) {
            deleteChildlessFromParentTable();
        }
        return rows;
    }


    //Radera receptfavorit
    @Query("DELETE FROM RecipeFavourites")
    public abstract int deleteAllFromChildTable() throws Exception;


    /**
     * Lägg till receptfavorit och associerat recept i databasen.
     * Annoterad med @Transaction för att säkerställa att databasoperation
     * sker atomiskt.
     * @param recipe innehåller receptobjekt
     * @param recipeFavourite innehåller receptfavoritobjekt
     * @return antal påverkade rader i databasen
     */
    @Transaction
    @Override
    public long insert(Recipe recipe, RecipeFavourite recipeFavourite) throws Exception {
        long rows = 0;
        String recipeId = getParentId(recipeFavourite.getRecipeId()).getValue();

        if(recipeId == null) {
             insertIntoParentTable(recipe);
        }
        rows = insertIntoChildTable(recipeFavourite);
        return rows;
    }


    //Hämta recept via dess id
    @Query("SELECT id FROM Recipes WHERE id = :id")
    public abstract LiveData<String> getParentId(String id);

    //Hämta samtliga receptfavoriter via recept-id
    @Query("SELECT recipeId FROM RecipeFavourites WHERE recipeId = :id")
    public abstract LiveData<String> getChildId(String id);

    //Hämta enkild receptfavorit genom recept-id
    @Query("SELECT * FROM RecipeFavourites WHERE recipeId = :id")
    public abstract RecipeFavourite getChildEntityById(String id);

}

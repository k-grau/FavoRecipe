package com.karlgrauers.favorecipe.repositories.database_repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.karlgrauers.favorecipe.models.RecipeWithShoppingLists;
import com.karlgrauers.favorecipe.models.recipe.Recipe;
import com.karlgrauers.favorecipe.models.shopping_list.ShoppingList;

import java.util.List;

@Dao
public abstract class ShoppingListDao implements DbBaseDao<Recipe, ShoppingList> {

    /*
     * Abstrakt klass som implementerar interface DbBaseDao med typerna
     * 'Recipe' och 'ShoppingList'
     */

    //Hämta samtliga shoppinglistor ur databas. Konstruerar objekt som kombinerar recept med shoppinglistor.
    //Annoterad med @Transaction för att säkerställa att databasoperation sker atomiskt.
    @Transaction
    @Query("SELECT Recipes.id, Recipes.label, Recipes.ingredientLines, Recipes.url, Recipes.image, " +
            "Recipes.dishType, Recipes.mealType, Recipes.cuisineType FROM Recipes")
    public abstract LiveData<List<RecipeWithShoppingLists>> getAll();



    /**
     * Radera samtliga shoppinglistor.
     * Radera först shoppinglistorna och kontrollera
     * sedan vilka recept som helt saknar barn, om barn
     * saknas, radera recept.
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
     * Radera shoppinglista via id.
     * Radera först shoppinglista och kontrollera
     * sedan om recept helt saknar barn, om ja
     * radera recept.
     * Annoterad med @Transaction för att säkerställa
     * att databasoperation sker atomiskt.
     * @return påverkat antal rader i databasen.
     */
    @Transaction
    @Override
    public long delete(ShoppingList shoppingList) throws Exception {
        long rows = 0;
        rows = deleteFromChildTable(shoppingList);

        if(rows > 0) {
            deleteChildlessFromParentTable();
        }
        return rows;
    }

    //Radera samtliga shoppinglistor
    @Query("DELETE FROM ShoppingLists")
    public abstract int deleteAllFromChildTable() throws Exception;


    /**
     * Lägg till shoppinglista och associerat recept i databasen.
     * Annoterad med @Transaction för att säkerställa att databasoperation
     * sker atomiskt.
     * @param recipe innehåller receptobjekt
     * @param shoppingList innehåller shoppingListeObjekt
     * @return antal påverkade rader i databasen
     */
    @Transaction
    @Override
    public long insert(Recipe recipe, ShoppingList shoppingList) throws Exception {
        long rows = 0;
        String uri = getParentId(shoppingList.getRecipeId()).getValue();

        if(uri == null) {
            insertIntoParentTable(recipe);
        }
        rows = insertIntoChildTable(shoppingList);

        return rows;
    }

    //Hämta recept baserat på id
    @Query("SELECT id FROM Recipes WHERE id = :id")
    public abstract LiveData<String> getParentId(String id);
}

package com.karlgrauers.favorecipe.repositories.database_repository;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;



/*
 * Interface som implementeras i abstrakta klasserna
 * 'RecipeFavouritesDao' och 'ShoppingListDao'. Definierar
 * gemensamma metoder för databasoperationer. Utnyttjar generiska
 * typer (T och S) för databasentiteter. T motsvaras av recept i
 * både 'RecipeFavouritesDao' och 'ShoppingListDao'.
 * S är antingen shoppinglista eller receptfavorit.
 */


interface DbBaseDao<T, S> {

    //Lägg till föräldraentitet och barnentitet i respektive tabeller
    // (föräldraentitet är alltid recept, barnentitet är antingen receptfavorit eller shoppinglista)
    @Insert
    long insert(T parentEntity, S childEntity) throws Exception;

    //Lägg till föräldraentitet i tabell
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIntoParentTable(T entity) throws Exception;

    //Lägg till barnentitet i tabell
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertIntoChildTable(S entity) throws Exception;

    //Radera entitet ur tabell
    @Delete
    long delete(S entity) throws Exception;

    //Radera barnentiet ur tabell
    @Delete
    int deleteFromChildTable(S entity) throws Exception;

    //Radera samtliga entiteter ur tabell
    @Delete
    int deleteAll() throws Exception;

    //Radera föräldraentiteter som saknar barnentiteter. Rent konkret innebär
    // detta att när en shoppinglista eller receptfavorit raderas kontrolleras
    // om receptet som är dess förälder helt saknar associerade barn. Om så är fallet
    // raderas även receptet.
    @Query("DELETE FROM Recipes WHERE NOT EXISTS(" +
            "SELECT 1 FROM RecipeFavourites WHERE Recipes.id = RecipeFavourites.recipeId) " +
            "AND NOT EXISTS(SELECT 1 FROM ShoppingLists WHERE Recipes.id = ShoppingLists.recipeId)")
    void deleteChildlessFromParentTable() throws Exception;
}

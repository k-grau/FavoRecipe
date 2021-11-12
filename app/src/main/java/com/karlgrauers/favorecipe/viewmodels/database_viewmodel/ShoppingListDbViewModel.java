package com.karlgrauers.favorecipe.viewmodels.database_viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import com.karlgrauers.favorecipe.models.RecipeWithShoppingLists;
import com.karlgrauers.favorecipe.models.recipe.Recipe;
import com.karlgrauers.favorecipe.models.shopping_list.ShoppingList;
import com.karlgrauers.favorecipe.repositories.database_repository.ShoppingListDbRepository;
import java.util.List;

/*
 * Klass som agerar "brygga" mellan klass 'ShoppingListsDbRepository'
 * och UI-klasser. Ärver av DatabaseViewModel.
 */


public class ShoppingListDbViewModel extends DatabaseViewModel {
    private final ShoppingListDbRepository SHOPPING_LIST_REPOSITORY;
    private LiveData<List<RecipeWithShoppingLists>> allShoppingLists;

    /**
     * Konstruktor. Kallar konstruktor i superklass och instansierar
     * klass 'ShoppingListsDbRepository'.
     * @param application innehåller instans av applikationsklass.
     */
    public ShoppingListDbViewModel(@NonNull Application application) {
        super(application);
        SHOPPING_LIST_REPOSITORY = new ShoppingListDbRepository(application);
        affectedRows = SHOPPING_LIST_REPOSITORY.getAffectedRows();
    }


    /**
     * Instansiera 'ShoppingListsDbRepository' och initialisera
     * samtliga shoppinglistor.
     */
    @Override
    public void init() {
        SHOPPING_LIST_REPOSITORY.init();
        allShoppingLists = SHOPPING_LIST_REPOSITORY.getAll();
    }

    /**
     * Radera samtliga shoppinglistor genom att kalla
     * 'deleteAll' i 'ShoppingListsDbRepository'.
     */
    public void deleteAll() {
        SHOPPING_LIST_REPOSITORY.deleteAll();
    }

    /**
     * Raderar shoppinglista i databas genom att kalla på metod 'delete'
     * i 'ShoppingListsDbRepository'.
     *  @param shoppingList innehåller objekt av shoppinglista.
     */
    public void delete(ShoppingList shoppingList) {
        SHOPPING_LIST_REPOSITORY.delete(shoppingList);
    }

    /**
     * Lägger till shoppinglista i databas genom att kalla på metod 'insert'
     * i 'ShoppingListsDbRepository'.
     *  @param recipe det recept som är associerat med shoppinglistan.
     *  @param shoppingList innehåller objekt av shoppinglista.
     */
    public void insert(Recipe recipe, ShoppingList shoppingList) {
        SHOPPING_LIST_REPOSITORY.insert(recipe, shoppingList);
    }

    /**
     * Implementation av metod i superklass. Nollställer värde på 'affectedRows'
     * genom att kalla metod 'resetAffectedRows' i klass 'DatabaseRepository'.
     */
    @Override
    public void resetAffectedRows() {
        SHOPPING_LIST_REPOSITORY.resetAffectedRows();
    }


    //Getter för LiveData-objekt 'allShoppingLists'. Får sitt värde från motsvarande objekt i
    //'ShoppingListsDbRepository'. Observeras i UI-klass.
    public LiveData<List<RecipeWithShoppingLists>> getAllShoppingLists() {
        return allShoppingLists;
    }



}

package com.karlgrauers.favorecipe.repositories.database_repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.karlgrauers.favorecipe.database.AppDatabase;
import com.karlgrauers.favorecipe.models.RecipeWithShoppingLists;
import com.karlgrauers.favorecipe.models.recipe.Recipe;
import com.karlgrauers.favorecipe.models.shopping_list.ShoppingList;
import java.util.List;


/*
 * Repository-klass för att hantera databasoperationer
 * på tabell med shoppinglistor. Ärver av superklass
 * 'DatabaseRepository'
 */

public class ShoppingListDbRepository extends  DatabaseRepository {
    private final ShoppingListDao SHOPPING_LIST_DAO;
    private LiveData<List<RecipeWithShoppingLists>> allShoppingLists;



    /**
     * Konstruktor. Kallar konstruktor i superklass
     * och initialiserar abstrakt klass 'ShoppingListDao'
     * med instans av appens databas.
     * @param application innehåller instans av applikationsklass
     */
    public ShoppingListDbRepository(Application application) {
        super(application);
        SHOPPING_LIST_DAO = DATABASE.shoppingListDao();
    }


    /**
     * Initialisera samtliga shoppinglistor.
     */
    @Override
    public void init() {
        allShoppingLists = SHOPPING_LIST_DAO.getAll();
    }


    /**
     * Lägg till shoppinglista i databasen.
     * Koden i metoden exekveras asynkront och
     * antal påverkade rader postas till LiveData-objektet
     * 'AFFECTED_ROWS' när operation är klar.
     * @param recipe det receptobjekt som är associerat med shoppinglisteobjektet
     * @param shoppingList innehåller objekt av shoppinglista
     */
    public void insert(final Recipe recipe, final ShoppingList shoppingList) {
        AppDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> {
            try{
                affectedRows = SHOPPING_LIST_DAO.insert(recipe, shoppingList);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                AFFECTED_ROWS.postValue(affectedRows);
            }
        });
        affectedRows = -1;
    }



    /**
     * Radera shoppingLista från databasen.
     * Koden i metoden exekveras asynkront och
     * antal påverkade rader postas till LiveData-objektet
     * 'AFFECTED_ROWS' när operation är klar.
     * @param shoppingList innehåller objekt av shoppingLista
     */
    public void delete(final ShoppingList shoppingList) {
        AppDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> {
            try{
                affectedRows = SHOPPING_LIST_DAO.delete(shoppingList);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                AFFECTED_ROWS.postValue(affectedRows);
            }
        });
        affectedRows = -1;
    }


    /**
     * Radera samtliga shoppinglistor från databasen
     * Koden i metoden exekveras asynkront och
     * antal påverkade rader postas till LiveData-objektet
     * 'AFFECTED_ROWS' när operation är klar.
     */
    @Override
    public void deleteAll() {
        AppDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> {
            try{
                affectedRows = SHOPPING_LIST_DAO.deleteAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                AFFECTED_ROWS.postValue(affectedRows);
            }
        });
        affectedRows = -1;
    }

    //Getter för samtliga shoppinglistor, används av klass ShoppingListDbViewModel
    public LiveData<List<RecipeWithShoppingLists>> getAll() {
        return allShoppingLists;
    }

}

package com.karlgrauers.favorecipe.repositories.database_repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.karlgrauers.favorecipe.database.AppDatabase;
import com.karlgrauers.favorecipe.models.RecipeWithFavourite;
import com.karlgrauers.favorecipe.models.recipe.Recipe;
import com.karlgrauers.favorecipe.models.recipe.RecipeFavourite;
import java.util.List;


/*
 * Repository-klass för att hantera databasoperationer
 * på tabell med receptfavoriter. Ärver av superklass
 * 'DatabaseRepository'
 */

public class RecipeFavouritesDbRepository extends DatabaseRepository {
    private final RecipeFavouritesDao FAVOURITES_DAO;
    private LiveData<String> RECIPE_FAVOURITE_ID = new MutableLiveData<>();
    private LiveData<List<RecipeWithFavourite>> allRecipeFavourites;


    /**
     * Konstruktor. Kallar konstruktor i superklass
     * och initialiserar abstrakt klass 'RecipeFavouritesDao'
     * med instans av appens databas.
     * @param application innehåller instans av applikationsklass
     */
    public RecipeFavouritesDbRepository(Application application) {
        super(application);
        FAVOURITES_DAO = DATABASE.recipeFavouritesDao();
    }


    /**
     * Initialisera samtliga receptfavoriter i databas.
     */
    @Override
    public void init() {
        allRecipeFavourites = FAVOURITES_DAO.getAll();
    }


    /**
     * Initialisera samtliga recept i receptdatabas
     * och sätt recept-id för aktuell receptfavorit.
     * @param parentId innehåller det unika id för recept
     *                 som kommer från Edamams api.
     */
    public void init(String parentId) {
        allRecipeFavourites = FAVOURITES_DAO.getAll();
        RECIPE_FAVOURITE_ID = FAVOURITES_DAO.getChildId(parentId);
    }


    /**
     * Lägg till receptfavorit i databasen.
     * Koden i metoden exekveras asynkront och
     * antal påverkade rader postas till LiveData-objektet
     * 'AFFECTED_ROWS' när operation är klar.
     * @param recipe det recept som är associerat med receptfavortiobjektet
     * @param recipeFavourite innehåller objekt av receptfavorit
     */
    public void insert(final Recipe recipe, final RecipeFavourite recipeFavourite) {
        AppDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> {
            try{
                affectedRows = FAVOURITES_DAO.insert(recipe, recipeFavourite);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                AFFECTED_ROWS.postValue(affectedRows);
            }
        });
        affectedRows = -1;
    }


    /**
     * Radera receptfavorit från databasen.
     * Koden i metoden exekveras asynkront och
     * antal påverkade rader postas till LiveData-objektet
     * 'AFFECTED_ROWS' när operation är klar.
     * @param recipeFavourite innehåller objekt av receptfavorit
     */
    public void delete(final RecipeFavourite recipeFavourite) {
        AppDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> {
            try{
                affectedRows = FAVOURITES_DAO.delete(recipeFavourite);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
               AFFECTED_ROWS.postValue(affectedRows);
            }
        });
        affectedRows = -1;
    }



    /**
     * Radera receptfavorit från databasen
     * med hjälp av dess id.
     * Koden i metoden exekveras asynkront och
     * antal påverkade rader postas till LiveData-objektet
     * 'AFFECTED_ROWS' när operation är klar.
     * @param id innehåller aktuellt id för receptfavorit.
     */
    public void deleteById(final String id) {
        AppDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> {
            try{
                affectedRows = FAVOURITES_DAO.delete(getRecipeFavouriteById(id));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                AFFECTED_ROWS.postValue(affectedRows);
            }
        });
        affectedRows = -1;
    }


    /**
     * Radera samtliga receptfavoriter från databasen
     * Koden i metoden exekveras asynkront och
     * antal påverkade rader postas till LiveData-objektet
     * 'AFFECTED_ROWS' när operation är klar.
     */
    @Override
    public void deleteAll() {
        AppDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> {
            try{
                affectedRows = FAVOURITES_DAO.deleteAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                AFFECTED_ROWS.postValue(affectedRows);
            }
        });
        affectedRows = -1;
    }


    //Getter för LiveData-objekt med samtliga receptfavoriter, används i klass 'RecipeFavouritesDbViewModel'
    public LiveData<List<RecipeWithFavourite>> getAll() {
        return allRecipeFavourites;
    }

    //Getter för LiveData-objekt med aktuellt receptfavorit-id, används i klass 'RecipeFavouritesDbViewModel'
    public LiveData<String> getId() {
        return RECIPE_FAVOURITE_ID;
    }


    //Getter för LiveData-objekt med receptfavorit baserat på id, används i klass 'RecipeFavouritesDbViewModel'
    private RecipeFavourite getRecipeFavouriteById(String id) {
        return FAVOURITES_DAO.getChildEntityById(id);
    }
}

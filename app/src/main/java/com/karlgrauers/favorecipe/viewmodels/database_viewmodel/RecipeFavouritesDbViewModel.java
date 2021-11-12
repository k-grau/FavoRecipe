package com.karlgrauers.favorecipe.viewmodels.database_viewmodel;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import com.karlgrauers.favorecipe.models.RecipeWithFavourite;
import com.karlgrauers.favorecipe.models.recipe.Recipe;
import com.karlgrauers.favorecipe.models.recipe.RecipeFavourite;
import com.karlgrauers.favorecipe.repositories.database_repository.RecipeFavouritesDbRepository;
import java.util.List;


/*
 * Klass som agerar "brygga" mellan klass 'RecipeFavouritesDbRepository'
 * och UI-klasser. Ärver av DatabaseViewModel.
 */

public class RecipeFavouritesDbViewModel extends DatabaseViewModel {
    private final RecipeFavouritesDbRepository FAVOURITES_REPOSITORY;
    private LiveData<List<RecipeWithFavourite>> allRecipeFavourites;
    private LiveData<String> recipeId;


    /**
     * Konstruktor. Kallar konstruktor i superklass och instansierar
     * klass 'RecipeFavouritesDbRepository'.
     * @param application innehåller instans av applikationsklass.
     */
    public RecipeFavouritesDbViewModel(@NonNull Application application) {
        super(application);
        FAVOURITES_REPOSITORY = new RecipeFavouritesDbRepository(application);
    }

    /**
     * Initialisera repository med unikt recept-id.
     * Används när ett enskild receptfavorit visas i UI-klass.
     */
    public void init(String recipeId) {
        FAVOURITES_REPOSITORY.init(recipeId);
        this.recipeId = FAVOURITES_REPOSITORY.getId();
        affectedRows = FAVOURITES_REPOSITORY.getAffectedRows();
    }

    /**
     * Initialisera repository med samtliga recept.
     * Används när lista med samtliga receptfavoriter
     * visas i UI-klass.
     */
    @Override
    public void init() {
        FAVOURITES_REPOSITORY.init();
        allRecipeFavourites = FAVOURITES_REPOSITORY.getAll();
        recipeId = FAVOURITES_REPOSITORY.getId();
        affectedRows = FAVOURITES_REPOSITORY.getAffectedRows();
    }

    /**
     * Lägger till receptfavorit i databas genom att kalla på metod 'insert'
     * i 'RecipeFavouritesDbRepository'.
     *  @param recipe det recept som är associerat med receptfavortiobjektet.
     *  @param recipeFavourite innehåller objekt av receptfavorit.
     */
    public void insert(Recipe recipe, RecipeFavourite recipeFavourite) {
        FAVOURITES_REPOSITORY.insert(recipe, recipeFavourite);

    }

    /**
     * Raderar receptfavorit i databas genom att kalla på metod 'delete'
     * i 'RecipeFavouritesDbRepository'.
     *  @param recipeFavourite innehåller objekt av receptfavorit.
     */
    public void delete(RecipeFavourite recipeFavourite) {
        FAVOURITES_REPOSITORY.delete(recipeFavourite);
    }

    /**
     * Raderar receptfavorit i databas via id genom att kalla på metod 'deleteById'
     * i 'RecipeFavouritesDbRepository'.
     *  @param id innehåller id för den receptfavorit som ska raderas.
     */
    public void deleteById(String id) {
        FAVOURITES_REPOSITORY.deleteById(id);
    }


    /**
     * Raderar samtliga receptfavoriter i databas genom att kalla på metod 'deleteAll'
     * i 'RecipeFavouritesDbRepository'.
     */
    public void deleteAll() {
        FAVOURITES_REPOSITORY.deleteAll();
    }


    /**
     * Implementation av metod i superklass. Nollställer värde på 'affectedRows'
     * genom att kalla metod 'resetAffectedRows' i klass 'DatabaseRepository'.
     */
    @Override
    public void resetAffectedRows() {
        FAVOURITES_REPOSITORY.resetAffectedRows();
    }

    //Getter för LiveData-objekt 'allRecipeFavourites'. Får sitt värde från motsvarande objekt i
    //'RecipeFavouritesDbRepository'.
    public LiveData<List<RecipeWithFavourite>> getAll() {
        return allRecipeFavourites;
    }

    //Getter för LiveData-objekt receptId. Får sitt värde från motsvarande objekt i
    //'RecipeFavouritesDbRepository'. Observeras i UI-klass.
    public LiveData<String> getId() {
        return recipeId;
    }
}

package com.karlgrauers.favorecipe.viewmodels.api_viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.karlgrauers.favorecipe.models.recipe.RecipeResponse;
import com.karlgrauers.favorecipe.repositories.api_repository.RecipeSearchApiRepository;

/*
 * ViewModel-klass som agerar "brygga" mellan UI-klasser
 * och klass 'RecipeSearchApiRepository'. Ärver av android-interna
 * klassen AndroidViewModel.
 */


public class RecipeSearchApiViewModel extends AndroidViewModel {
    private RecipeSearchApiRepository repository;
    private LiveData<RecipeResponse> recipeResponseLiveData;

    /**
     * Konstruktor. Kallar konstruktor i superklass.
     * @param application innehåller instans av applikationsklass
     */
    public RecipeSearchApiViewModel(@NonNull Application application) {
        super(application);
    }


    /**
     * Instansiera 'RecipeSearchApiRepository'.
     * Associera LiveData-objekt 'recipeResponseLiveData'
     * med motsvarande LiveData-objekt i 'RecipeSearchApiRepository'.
     */
    public void init() {
        repository = new RecipeSearchApiRepository();
        recipeResponseLiveData = repository.getRecipesResponseLiveData();
    }

    /**
     * Sök recept genom att kalla metod 'searchRecipes' i
     * klass 'RecipeSearchApiRepository'.
     * @param keywords innehåller sökord angivna av användare.
     */
    public void searchRecipes(String keywords) {
        repository.searchRecipes(keywords);
    }

    /**
     * Få nästa sida av sökresultat genom att kalla metod
     * 'getNextPage' i klass 'RecipeSearchApiRepository'.
     * @param paginationUrl den url som leder till nästa sida av sökresultat.
     */
    public void getNextPage(String paginationUrl) {
        repository.getNextPage(paginationUrl);
    }

    //Getter för LiveData-objekt. Observeras i UI-klasser.
    public LiveData<RecipeResponse> getRecipeResponseLiveData() {
        return recipeResponseLiveData;
    }
}

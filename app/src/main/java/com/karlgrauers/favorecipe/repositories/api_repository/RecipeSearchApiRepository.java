package com.karlgrauers.favorecipe.repositories.api_repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.karlgrauers.favorecipe.models.recipe.RecipeResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * Repository-klass för att hantera anrop till Edamams
 * recept-api. Ärver av superklass ApiRepository.
 *
 */



public class RecipeSearchApiRepository extends ApiRepository {
    private RecipeSearchApiService recipeApiService;
    private final MutableLiveData<RecipeResponse> RECIPE_RESPONSE_LIVEDATA;


    /**
     * Konstruktor. Sätter bas-url och svarskod för
     * begränsning av API-anrop genom att kalla superklassens
     * konstruktor. Intitaliserar det LiveData-objekt
     * vilket innehåller resultaten från api-anropen.
     */
    public RecipeSearchApiRepository() {
        super("https://api.edamam.com/api/recipes/", 429);
        RECIPE_RESPONSE_LIVEDATA = new MutableLiveData<>();
    }


    /**
     * Intialisera retrofit med bas-rul och
     * interface 'RecipeApiService'. Själva metoden
     * kallas i superklassens konstruktor.
     */
    @Override
    protected void initRetroFit() {
        recipeApiService = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RecipeSearchApiService.class);
    }


    /**
     * Sök recept via anrop till Edamams recept-api.
     * Koden i metoden exekveras asynkront och när
     * svar erhållits från api postas detta i
     * LiveData-objektet 'RECIPES_RESPONSE_LIVEDATA'
     * @param keywords innehåller sökord inskrivna
     *                 av användare.
     */
    public void searchRecipes(String keywords) {
        recipeApiService.searchRecipes(keywords)
                .enqueue(new Callback<RecipeResponse>() {

                    //Om svar erhålles från api, posta kroppen i svaret till LiveData-objekt
                    @Override
                    public void onResponse(@NonNull Call<RecipeResponse> call, @NonNull Response<RecipeResponse> response) {
                        if(response.body() != null) {
                            RECIPE_RESPONSE_LIVEDATA.postValue(response.body());
                        }
                    }

                    //Om api-anrop fallerar, posta null till LiveData-objekt
                    @Override
                    public void onFailure(@NonNull Call<RecipeResponse> call, @NonNull Throwable t) {
                        RECIPE_RESPONSE_LIVEDATA.postValue(null);
                    }
                });
    }


    /**
     * Hämta nästa sida med sökträffar i svaret från
     * anropet till Edamams api. Posta i sin tur detta
     * svar i LiveData-objektet 'RECIPES_RESPONSE_LIVEDATA'.
     * Koden i metoden exekveras asynkront
     * @param url innehåller den url som används för att få
     *            nästa sida med sökträffar.
     */
    public void getNextPage(String url) {
        recipeApiService.getNextPage(url)
                .enqueue(new Callback<RecipeResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RecipeResponse> call, @NonNull Response<RecipeResponse> response) {


                        /* Om en vi får kod för begränsning av sökträffar returnera
                        från metod. Begränsningar av sökträffar kan ske om man anropat
                         api för många gånger på en minut (jag använder ett obetalt utvecklarkonto
                         hos Edamam så det finns begräsning på hur många api-anrop som tillåts per minut)*/
                        if(response.code() == CALL_LIMIT_RESPONSE) {
                            return;
                        }


                        //Om svar är null, returner från metod. Annars posta till LiveData-objekt.
                        if(response.body() == null) {
                            return;
                        }
                        RECIPE_RESPONSE_LIVEDATA.postValue(response.body());
                    }


                    //Om api-anrop fallerar, posta null till LiveData-objekt
                    @Override
                    public void onFailure(@NonNull Call<RecipeResponse> call, @NonNull Throwable t) {
                        RECIPE_RESPONSE_LIVEDATA.postValue(null);
                    }
                });
    }

    //Getter för LiveData-objekt. Används av klass 'RecipeSearchApiViewModel'
    public LiveData<RecipeResponse> getRecipesResponseLiveData() {
        return RECIPE_RESPONSE_LIVEDATA;
    }
}

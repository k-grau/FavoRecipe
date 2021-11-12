package com.karlgrauers.favorecipe.repositories.api_repository;

import com.karlgrauers.favorecipe.BuildConfig;
import com.karlgrauers.favorecipe.models.recipe.RecipeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface RecipeSearchApiService {

    /*
     * Interface för anrop till Edamams recept-api.
     * Används av klass 'RecipeSearchApiRepository'
     *
     */


    //Fält för att bygga url för api-anrop. Api-nyckel och app-id i separat fil.
    String VERSION = "v2?";
    String TYPE = "type=public";
    String APP_ID = "&app_id=" + BuildConfig.RECIPE_APP_ID;
    String APP_KEY = "&app_key=" + BuildConfig.RECIPE_API_KEY;
    String FIELDS = "&imageSize=LARGE&field=uri&field=ingredientLines&field=label&field=image&field=url&field=cuisineType&field=mealType&field=dishType";

    //Sök recept med nyckelord. Nyckelord sätts i queryparameter 'q'
    @GET(VERSION + TYPE + APP_ID + APP_KEY + FIELDS)
    Call<RecipeResponse> searchRecipes(@Query("q") String keywords);

    //Få nästa sida med sökresultat, genom url till denna sida
    @GET()
    Call<RecipeResponse> getNextPage(@Url String url);
}

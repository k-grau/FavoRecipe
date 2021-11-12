package com.karlgrauers.favorecipe.adapters.recipe_adapter;
import android.content.Context;
import androidx.annotation.NonNull;
import com.karlgrauers.favorecipe.adapters.OnAdapterClickListener;
import com.karlgrauers.favorecipe.models.recipe.RecipeHit;
import com.karlgrauers.favorecipe.utils.Converters;
import java.util.ArrayList;
import java.util.List;


/*
 * Subklass som ärver av 'RecipeAdapter' Har två ansvar:
 * Att binda receptdata från API-anrop till varje receptvy
 * och hantera listan med recept - det vill säga lägga till och
 * ta bort recept.
 */

public class RecipeSearchApiAdapter extends RecipeAdapter {
    private final List<RecipeHit> HITS = new ArrayList<>();


    /**
     * Konstruktor.
     * @param context innehåller applikationskontexten.
     * @param adapterClickListener innehåller klass som
     *                             ska implementera interface
     *                             'OnAdapterClickListener'
     */
    public RecipeSearchApiAdapter(Context context, OnAdapterClickListener adapterClickListener) {
        super(context, adapterClickListener, false);
    }



    /**
     * Bind receptdata till viewholder. Metoden kallas för
     * varje recept som finns i lista 'HITS'.
     * @param holder den viewholder till vilken datan ska bindas,
     *               i detta fall innehåller den 'layout_recipe_item'
     * @param position den position i listan på vilken receptet
     *                 befinner sig.
     *
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        RecipeHit recipeHit = HITS.get(position);
        holder.TV_LABEL.setText(recipeHit.getRecipe().getLabel());

        //Sätt klicklyssnare på recepttitel. Vid klick omdirigeras användare till receptet via dess url.
        holder.TV_LABEL.setOnClickListener(v -> ADAPTER_CLICK_LISTENER.onAdapterItemLinkClicked(recipeHit.getRecipe(), v));


        if (recipeHit.getRecipe().getImage() != null) {
            setImage(holder.itemView, recipeHit.getRecipe(), holder.IV_DISH_IMAGE);

            //Sätt klicklyssnare på receptbild. Vid klick omdirigeras användare till receptet via dess url.
            holder.IV_DISH_IMAGE.setOnClickListener(v -> ADAPTER_CLICK_LISTENER.onAdapterItemLinkClicked(recipeHit.getRecipe(), v));
        }

        if (recipeHit.getRecipe().getDishType() != null) {
            String dishType = Converters.listToString(recipeHit.getRecipe().getDishType(), DELIMITER, CHARS_TO_DELETE);
            dishType = Converters.firstToUpperCase(dishType);
            holder.TV_DISH_TYPE.setText(dishType);
        }

        if (recipeHit.getRecipe().getMealType() != null) {
            String mealType = Converters.listToString(recipeHit.getRecipe().getMealType(), DELIMITER, CHARS_TO_DELETE);
            mealType = Converters.firstToUpperCase(mealType);
            holder.TV_MEAL_TYPE.setText(mealType);
        }

        if (recipeHit.getRecipe().getCuisineType() != null) {
            String cuisineType = Converters.listToString(recipeHit.getRecipe().getCuisineType(), DELIMITER, CHARS_TO_DELETE);
            cuisineType = Converters.firstToUpperCase(cuisineType);
            holder.TV_CUISINE_TYPE.setText(cuisineType);
        }
    }


    /**
     * Getter för receptlista.
     * @return storlek på listan.
     */
    @Override
    public int getItemCount() {
        return HITS.size();
    }


    /**
     * Setter för receptlista. Lägger
     * till recept i listen.
     * @param hits de recept som ska läggas till.
     */
    public void setHits(List<RecipeHit> hits) {
        this.HITS.addAll(hits);
        notifyItemInserted(hits.size());
    }

    /**
     * Rensa receptlista.
     */
    public void clearHits() {
        this.HITS.clear();
        notifyDataSetChanged();
    }
}

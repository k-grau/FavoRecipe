package com.karlgrauers.favorecipe.adapters.recipe_adapter;
import android.content.Context;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import com.karlgrauers.favorecipe.adapters.OnAdapterClickListener;
import com.karlgrauers.favorecipe.models.RecipeWithFavourite;
import com.karlgrauers.favorecipe.utils.Converters;
import java.util.ArrayList;
import java.util.List;


/*
 * Subklass som ärver av 'RecipeAdapter' Har tre ansvar:
 * Att binda receptdata från databas till varje receptvy.
 * Att hantera listan med receptfavoriter - det vill säga lägga till och
 * ta bort receptfavoriter. Och att filitrera listan med receptfavoriter så att
 * användare enkelt kan söka bland dem.
 */

public class RecipeFavouritesDbAdapter extends RecipeAdapter {
    private final List<RecipeWithFavourite> RECIPE_FAVOURITES = new ArrayList<>();
    private final List<RecipeWithFavourite> UNFILTERED_RECIPE_FAVOURITES = new ArrayList<>();


    /**
     * Konstruktor.
     * @param context innehåller applikationskontexten.
     * @param adapterClickListener innehåller klass som
     *                             ska implementera interface
     *                             'OnAdapterClickListener'
     */
    public RecipeFavouritesDbAdapter(Context context, OnAdapterClickListener adapterClickListener) {
        super(context, adapterClickListener, true);
    }

    /**
     * Bind receptdata till viewholder. Metoden kallas för
     * varje recept som finns i lista 'RECIPE_FAVOURITES'.
     * @param holder den viewholder till vilken datan ska bindas,
     *               i detta fall innehåller den 'layout_recipe_item'
     * @param position den position i listan på vilken receptet
     *                 befinner sig.
     *
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        RecipeWithFavourite recipeFavourite = RECIPE_FAVOURITES.get(position);

        holder.TV_LABEL.setText(recipeFavourite.getRecipe().getLabel());
        holder.TV_LABEL.setPaintFlags(holder.TV_LABEL.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //Sätt klicklyssnare på recepttitel. Vid klick omdirigeras användare till receptet via dess url.
        holder.TV_LABEL.setOnClickListener(v -> ADAPTER_CLICK_LISTENER.onAdapterItemLinkClicked(recipeFavourite, v));

        //Sätt klicklyssnare på stängkryss. Vid klick raderas receptet ur databas.
        holder.ivDeleteCross.setOnClickListener(v -> ADAPTER_CLICK_LISTENER.onAdapterItemDeleteClicked(recipeFavourite));

        if (recipeFavourite.getRecipe().getImage() != null) {
            setImage(holder.itemView, recipeFavourite.getRecipe(), holder.IV_DISH_IMAGE);

            //Sätt klicklyssnare på receptbild. Vid klick omdirigeras användare till receptet via dess url.
            holder.IV_DISH_IMAGE.setOnClickListener(v -> ADAPTER_CLICK_LISTENER.onAdapterItemLinkClicked(recipeFavourite, v));
        }

        if (recipeFavourite.getRecipe().getDishType() != null) {
            String dishType = Converters.listToString(recipeFavourite.getRecipe().getDishType(), DELIMITER, CHARS_TO_DELETE);
            dishType = Converters.firstToUpperCase(dishType);
            holder.TV_DISH_TYPE.setText(dishType);
        }

        if (recipeFavourite.getRecipe().getMealType() != null) {
            String mealType = Converters.listToString(recipeFavourite.getRecipe().getMealType(), DELIMITER, CHARS_TO_DELETE);
            mealType = Converters.firstToUpperCase(mealType);
            holder.TV_MEAL_TYPE.setText(mealType);
        }

        if (recipeFavourite.getRecipe().getCuisineType() != null) {
            String cuisineType = Converters.listToString(recipeFavourite.getRecipe().getCuisineType(), DELIMITER, CHARS_TO_DELETE);
            cuisineType = Converters.firstToUpperCase(cuisineType);
            holder.TV_CUISINE_TYPE.setText(cuisineType);
        }
    }


    /**
     * Getter för receptfavoritlista.
     * @return storlek på listan.
     */
    @Override
    public int getItemCount() {
        return RECIPE_FAVOURITES.size();
    }


    /**
     * Setter för favoritreceptlistor. Rensar både listorna
     * 'RECIPE_FAVOURITES' och 'UNFILTERED_RECIPE_FAVOURITES'
     * och lägger till nya receptfavoriter i dessa. Metod kallas vid
     * databasoperation såsom att lägga till receptfavorit eller ta bort receptfavorit.
     * @param recipeFavourites lista med receptobjekt som har ett favoritobjekt påkopplat.
     */
    public void setRecipeFavourites(List<RecipeWithFavourite> recipeFavourites) {
        RECIPE_FAVOURITES.clear();
        UNFILTERED_RECIPE_FAVOURITES.clear();
        UNFILTERED_RECIPE_FAVOURITES.addAll(recipeFavourites);
        RECIPE_FAVOURITES.addAll(UNFILTERED_RECIPE_FAVOURITES);
        notifyDataSetChanged();
    }

    /**
     * Setter för filtrerad receptfavoritlista. Rensar listan
     * 'RECIPE_FAVOURITES' och lägger till filtrerade receptfavoriter
     * i denna.
     * @param recipeFavourites de filtrerade receptfavoriterna.
     */
    private void setFilteredRecipeFavourites(List<RecipeWithFavourite> recipeFavourites) {
        RECIPE_FAVOURITES.clear();
        RECIPE_FAVOURITES.addAll(recipeFavourites);
        notifyDataSetChanged();
    }

    /**
     * Filtrerar receptfavoriter beroende på söksträng (som skrivs in av användare).
     * Loopar lista med ofiltrerade receptfavoriter och lägger till de
     * som matchar söksträng i array 'filteredRecipes'. Kallar sedan
     * på metod 'setFilteredRecipeFavourites' med denna lista som argument.
     * @param text söksträng innehåller sökord inskrivet av användare.
     */
    public void filter(String text) {
        ArrayList<RecipeWithFavourite> filteredRecipes = new ArrayList<>();
        for (RecipeWithFavourite fav : UNFILTERED_RECIPE_FAVOURITES) {
            if (fav.getRecipe().getLabel().toLowerCase().contains(text.toLowerCase())) {
                filteredRecipes.add(fav);
            }
        }
        setFilteredRecipeFavourites(filteredRecipes);
    }
}

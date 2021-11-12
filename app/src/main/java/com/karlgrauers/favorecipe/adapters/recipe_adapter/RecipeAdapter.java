package com.karlgrauers.favorecipe.adapters.recipe_adapter;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.karlgrauers.favorecipe.R;
import com.karlgrauers.favorecipe.adapters.OnAdapterClickListener;
import com.karlgrauers.favorecipe.models.recipe.Recipe;


/*
 * Abstrakt superklass för de adaptrar som hanterar recept, alltså
 * 'RecipeFavouritesDbAdapter' och 'RecipeSearchApiAdapter'.
 * Ärver av androidinterna klassen RecyclerView. Har ansvar för
 * att skapa viewholder och implementera metod 'setImage' som
 * används i båda subklasser.
 */

public abstract class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {
    protected final static String DELIMITER = ", ";
    protected final static int CHARS_TO_DELETE = 2;
    protected final Context CONTEXT;
    private final boolean DATA_IS_LOCAL;
    protected final OnAdapterClickListener ADAPTER_CLICK_LISTENER;



    /**
     * Konstruktor.
     * @param context innehåller applikationskontexten.
     * @param adapterClickListener innehåller klass som
     *                             ska implementera interface
     *                             'OnAdapterClickListener'
     * @param dataIsLocal true om subklassen använder data från
     *                    appens databas, false om subklass
     *                    använder data från externt api.
     */
    public RecipeAdapter(Context context, OnAdapterClickListener adapterClickListener, boolean dataIsLocal) {
        CONTEXT = context;
        DATA_IS_LOCAL = dataIsLocal;
        ADAPTER_CLICK_LISTENER = adapterClickListener;
    }




    /**
     * Skapa viewholder med layouten 'layout_recipe_item' som gäller
     * för varje receptobjekt i listan.
     * @param parent innehåller föräldravy att placera layouten i.
     * @param viewType innehåller id för den typ av vy som ska skapas
     *                 (används ej)
     * @return instans av statiska inre klassen 'RecipeHolder'
     */
    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recipe_item, parent, false);
        return new RecipeHolder(itemView, DATA_IS_LOCAL, CONTEXT);
    }


    /**
     * Sätter bild för recept i lista med hjälp av biblioteket Glide. Sätter
     * även recepts URL som tag på bilden så det blir möjligt att
     * klicka bild för att komma till receptet.
     * @param itemView innehåller vyn för recept-item.
     * @param recipe innehåller receptobjekt.
     * @param ivDishImage innehåller imageview som bild ska laddas in i.
     */
    protected void setImage(View itemView, Recipe recipe, ImageView ivDishImage) {
        String imageUrl = recipe.getImage()
                .replace(CONTEXT.getString(R.string.http_non_secure), CONTEXT.getString(R.string.http_secure));

        Glide.with(itemView)
                .load(imageUrl)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners((int) CONTEXT.getResources().getDimension(R.dimen.light_corner_radius))))
                .into(ivDishImage);

        if(recipe.getUrl() != null) {
            ivDishImage.setTag(recipe.getUrl());
        }
    }


    /*
     * Inre statisk klass med ansvar för att definiera de vyer
     * som ska ingå i viewholdern.
     */
    static class RecipeHolder extends RecyclerView.ViewHolder {
        final protected TextView TV_LABEL, TV_DISH_TYPE, TV_MEAL_TYPE, TV_CUISINE_TYPE;
        final protected ShapeableImageView IV_DISH_IMAGE;
        protected ImageView ivDeleteCross;


        /**
         * Konstruktor.
         * @param itemView innehåller den vy som ska användas
         *                 för varje recept-item, i detta fall
         *                 'layout_recipe_item'.
         * @param dataIsLocal vid true läggs en vy med delete-kryss
         *                    i högre övre hörnet på varje recept-item.
         *                    Vid false läggs inget kryss på recept-item.
         */
        public RecipeHolder(@NonNull View itemView, boolean dataIsLocal, Context context) {
            super(itemView);

            TV_LABEL = itemView.findViewById(R.id.recipe_item_label);
            TV_DISH_TYPE = itemView.findViewById(R.id.recipe_item_dish_type);
            TV_CUISINE_TYPE = itemView.findViewById(R.id.recipe_item_cuisine_type);
            TV_MEAL_TYPE = itemView.findViewById(R.id.recipe_item_meal_type);
            IV_DISH_IMAGE = itemView.findViewById(R.id.recipe_item_iv_recipe_image);

            TV_LABEL.setPaintFlags(TV_LABEL.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            //Sätt rundade hörn på bildcontainer
            IV_DISH_IMAGE.setShapeAppearanceModel(IV_DISH_IMAGE.getShapeAppearanceModel()
                    .toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, context.getResources().getDimension(R.dimen.light_corner_radius))
                    .build());


            if(dataIsLocal) {
                ivDeleteCross = itemView.findViewById(R.id.recipe_item_iv_delete_cross);
                ivDeleteCross.setVisibility(View.VISIBLE);
            }
        }
    }
}

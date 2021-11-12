package com.karlgrauers.favorecipe.adapters;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.karlgrauers.favorecipe.R;
import com.karlgrauers.favorecipe.models.RecipeWithShoppingLists;
import com.karlgrauers.favorecipe.models.shopping_list.ShoppingList;
import com.karlgrauers.favorecipe.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/*
 * Adapterklass för att hantera shoppinglistor i recyclerview. Ärver
 * av androidinterna klassen RecyclerView. Ansvarsområden är föjande:
 * Skapa viewholder och binda shoppinglistdata till viewholder. Hantera
 * lista med shoppinglistor - alltså ta bort och lägga till i denna.
 * Filtrera lista så användare enkelt kan söka bland sina shoppinglistor.
 */


public class ShoppingListDbAdapter extends RecyclerView.Adapter<ShoppingListDbAdapter.ShoppingListHolder>{
    private final List<ShoppingList> SHOPPING_LISTS = new ArrayList<>();
    private final List<ShoppingList> UNFILTERED_SHOPPING_LISTS = new ArrayList<>();
    private final OnAdapterClickListener ADAPTER_CLICK_LISTENER;
    private final Context CONTEXT;


    /**
     * Konstruktor.
     * @param context innehåller applikationskontexten.
     * @param adapterClickListener innehåller klass som
     *                             ska implementera interface
     *                             'OnAdapterClickListener'
     */
    public ShoppingListDbAdapter(OnAdapterClickListener adapterClickListener, Context context) {
        ADAPTER_CLICK_LISTENER = adapterClickListener;
        CONTEXT = context;
    }


    /**
     * Skapa viewholder med layouten 'layout_shoppinglist_item' som gäller
     * för varje shoppingobjekt i listan.
     * @param parent innehåller föräldravy att placera layouten i.
     * @param viewType innehåller id för den typ av vy som ska skapas
     *                 (används ej)
     * @return instans av statiska inre klassen 'ShoppingListHolder'
     */
    @NonNull
    @Override
    public ShoppingListDbAdapter.ShoppingListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_shopping_list_item, parent, false);
        return new ShoppingListHolder(itemView);
    }



    /**
     * Bind shoppinglist-data till viewholder. Metoden kallas för
     * varje shoppinglista som finns i lista 'SHOPPING_LIST'.
     * @param holder den viewholder till vilken datan ska bindas,
     *               i detta fall innehåller den 'layout_shopping_list_item'
     * @param position den position i listan på vilken shoppinglistan
     *                 befinner sig.
     */
    @Override
    public void onBindViewHolder(@NonNull ShoppingListHolder holder, int position) {
        ShoppingList shoppingList = SHOPPING_LISTS.get(position);

        StringBuilder ingredients = new StringBuilder();

        for(String ingredient : shoppingList.getRecipe().getIngredientLines()) {
            ingredients.append(String.format("%s\n", ingredient));
        }
        holder.TV_LABEL.setText(shoppingList.getRecipe().getLabel());
        holder.TV_CREATED_AT.setText(CONTEXT.getResources().getString(R.string.shopping_list_created_at,
                Utils.formatDate(shoppingList.getCreatedAt())));
        holder.TV_SHOPPING_LIST_LINES.setText(ingredients);

        //Sätt klicklyssnare på stängkryss. Vid klick raderas shoppinglista ur databas.
        holder.IV_DELETE_CROSS.setOnClickListener(v -> ADAPTER_CLICK_LISTENER.onAdapterItemDeleteClicked(shoppingList));

        //Sätt klicklyssnare på vy med text 'View recipe'. Vid klick omdirigeras användare till receptet via dess url.
        holder.TV_VIEW_SHOPPING_LIST.setOnClickListener(v -> ADAPTER_CLICK_LISTENER.onAdapterItemLinkClicked(shoppingList, v));
    }


    /**
     * Platta ut datan från 'recipeWithShoppingLists' och konvertera till lista av typ 'ShoppingList'.
     * Detta är något av ett hack och görs eftersom recepttabell och tabell för shoppinglsitor har
     * en 1 till många-relation i databasen. Ett recept kan alltså ha flera shoppinglistor.
     * Således består varje objekt i 'recipeWithShoppingListsa av ett receptobjekt med
     * en lista av shoppinglistor. Loopen nedan "vänder" på förhållandet och kopplar på ett
     * receptobjekt på varje shoppinglista istället. Rent tekniskt omvandlas alltså
     * 1 till många-relationen till en 1 till 1-relation. Loopen sker dessutom baklänges
     * så shoppinglistorna blir sorterade efter datum, med senast tillagd först.
     * @param recipeWithShoppingLists innehåller lista av receptobjekt med listor av
     *                                shoppinglistor påkopplade.
     * */
    private List <ShoppingList> convertToShoppingListsArray(List <RecipeWithShoppingLists> recipeWithShoppingLists) {
        List<ShoppingList> shoppingLists = new ArrayList<>();

        for(int i = recipeWithShoppingLists.size() - 1; i >= 0; i--) {
            for(int j = recipeWithShoppingLists.get(i).getShoppingLists().size() - 1; j >= 0; j--) {
                recipeWithShoppingLists.get(i).getShoppingLists().get(j).setRecipe(recipeWithShoppingLists.get(i).getRecipe());
                shoppingLists.add(recipeWithShoppingLists.get(i).getShoppingLists().get(j));
            }
        }
        return shoppingLists;
    }


    /**
     * Getter för shoppingliste-lista.
     * @return storlek på listan.
     */
    @Override
    public int getItemCount() {
        return SHOPPING_LISTS.size();
    }


    /**
     * Setter för shoppinglistor. Rensar både listorna
     * 'SHOPPING_LISTS' och 'UNFILTERED_SHOPPING_LISTS',
     * konverterar sedan 'recipeWithShoppingLists' till
     * lista av typ 'ShoppingList' och lägger till denna i
     * båda listorna. Metod kallas vid databasoperation såsom
     * att lägga till eller ta bort shoppinglista.
     * @param recipeWithShoppingLists innehåller lista med receptobjekt
     *                                som har lista av shoppingliste-objekt påkopplade
     */
    public void setShoppingLists(List<RecipeWithShoppingLists> recipeWithShoppingLists) {
        SHOPPING_LISTS.clear();
        UNFILTERED_SHOPPING_LISTS.clear();
        UNFILTERED_SHOPPING_LISTS.addAll(convertToShoppingListsArray(recipeWithShoppingLists));
        SHOPPING_LISTS.addAll(UNFILTERED_SHOPPING_LISTS);
        notifyDataSetChanged();
    }



    /**
     * Setter för filtrerad shoppingliste-lista. Rensar listan
     * 'SHOPPING_LISTS' och lägger till filtrerade shoppingListor
     * i denna.
     * @param shoppingLists de filtrerade shoppinglistorna.
     */
    private void setFilteredShoppingLists(List<ShoppingList> shoppingLists) {
        SHOPPING_LISTS.clear();
        SHOPPING_LISTS.addAll(shoppingLists);
        notifyDataSetChanged();
    }


    /**
     * Filtrerar shoppinglistor beroende på söksträng (som skrivs in av användare).
     * Loopar lista med ofiltrerade shoppinglistorr och lägger till de
     * som matchar söksträng i array 'filteredShoppingLists'. Kallar sedan
     * på metod 'setFilteredRecipeShoppingLists' med denna lista som argument.
     * @param text söksträng innehåller sökord inskrivet av användare.
     */
    public void filter(String text) {
        ArrayList<ShoppingList> filteredShoppingLists = new ArrayList<>();
        for (ShoppingList shoppingList : UNFILTERED_SHOPPING_LISTS) {
            if (shoppingList.getRecipe().getLabel().toLowerCase().contains(text.toLowerCase())) {
                filteredShoppingLists.add(shoppingList);
            }
        }
        setFilteredShoppingLists(filteredShoppingLists);
    }


    /*
     * Inre statisk klass med ansvar för att definiera de vyer
     * som ska ingå i viewholdern.
     */
    static class ShoppingListHolder extends RecyclerView.ViewHolder {
        final private TextView TV_LABEL, TV_SHOPPING_LIST_LINES, TV_CREATED_AT, TV_VIEW_SHOPPING_LIST;
        final private ImageView IV_DELETE_CROSS;


        /**
         * Konstruktor.
         * @param itemView innehåller den vy som ska användas
         *                 för varje shoppingliste-item, i detta fall
         *                 'layout_shopping_list_item'.
         */
        public ShoppingListHolder(@NonNull View itemView) {
            super(itemView);

            TV_LABEL = itemView.findViewById(R.id.shopping_list_item_tv_label);
            TV_SHOPPING_LIST_LINES = itemView.findViewById(R.id.shopping_list_item_tv_lines);
            TV_CREATED_AT = itemView.findViewById(R.id.shopping_list_item_tv_created_at);
            TV_VIEW_SHOPPING_LIST = itemView.findViewById(R.id.shopping_list_item_tv_view_shopping_list);
            IV_DELETE_CROSS = itemView.findViewById(R.id.shopping_list_item_iv_delete_cross);
            TV_VIEW_SHOPPING_LIST.setPaintFlags(TV_VIEW_SHOPPING_LIST.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        }
    }


}

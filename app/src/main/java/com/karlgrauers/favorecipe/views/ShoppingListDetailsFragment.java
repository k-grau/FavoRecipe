package com.karlgrauers.favorecipe.views;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.karlgrauers.favorecipe.R;
import com.karlgrauers.favorecipe.models.shopping_list.ShoppingList;
import com.karlgrauers.favorecipe.utils.UIControls;
import com.karlgrauers.favorecipe.utils.Utils;


/*
 * Klass med fragment för att visa shoppingliste-detaljer
 * Shoppinglistan visas i en scrollbar cardview och har länk
 * till receptet den är skapad av. Möjlighet finns även att dela
 * shoppinglistan (med valfri app i telefon) via en ikon i optionsmenyn.
 */


public class ShoppingListDetailsFragment extends Fragment implements View.OnClickListener {
    private ShoppingList shoppingList;
    private Context context;
    private StringBuilder ingredients;
    MenuItem shareIcon;


    /**
     * Initialiserar fragment.
     * @param savedInstanceState innehåller
     * instansdata om sådan är tillgänlig, annars
     * null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ingredients = new StringBuilder();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

        //Fånga upp shoppingliste-objekt från bundle och lagra det i fältet 'shoppingList'
        if(getArguments() != null) {
            this.shoppingList = (ShoppingList) getArguments().getSerializable(getString(R.string.bundle_key_shopping_list_details));
        }
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);
    }


    /**
     * Context-variabel sätts först när fragment
     * lagts till i sin 'host'-vy (alltså 'MainActivity')
     * eftersom vi inte vill riskera att få en nullad aktivitetskontext.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     * Initialiserar vyer.
     * @param inflater innehåller instans
     *      av LayoutInflater.
     * @param container innehåller topp-
     *      vy att placera barnvy i, alltså 'MainActivity'.
     * @return den vy som skapats.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list_details, container, false);
        initViews(view);
        return view;
    }


    /**
     * När optionsmeny skapas sätts referenser till ikon
     * för att dela shoppinglista. Denna referens
     * är nödvändiga då ikonens färg ändras dynaminskt.
     * @param menu innehåller den meny i vilken ikonen finns.
     * @param inflater  innehåller instans av layoutinflater.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.overflow_menu_shopping_list_details, menu);
        shareIcon = menu.findItem(R.id.action_share_shopping_list);
    }


    /**
     * Lyssnare för ikoner i options-menyn. Kallar på relevant
     * metoder vid klick, i detta fall metod för att dela
     * shoppinglista.
     * @param item innehåller det menyobjekt som klickats.
     * @return kall till super-klass.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_share_shopping_list) {

            //Sätt ikonfärg till orange när shoppinglista ska delas.
            UIControls.setIconColor(shareIcon, ContextCompat.getColor(context, R.color.orange_brown));
            shareShoppingList();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Initialiserar fragmentets vyer.
     * @param view innehåller vy som skapades
     *             i metod 'onCreateView'
     */
    private void initViews(View view) {
        TextView tvLabel = view.findViewById(R.id.fragment_shopping_list_details_tv_label);
        TextView tvCreatedAt = view.findViewById(R.id.fragment_shopping_list_details_tv_created_at);
        TextView tvLines = view.findViewById(R.id.fragment_shopping_list_details_tv_lines);
        TextView tvViewRecipe = view.findViewById(R.id.fragment_shopping_list_details_tv_view_recipe);

        /* Ingredienserna till shoppinglista läggs i variabel "ingredients" som
           kontrolleras för null och för att vara tom, innan något lagras i den.
           Detta görs eftersom vyn behåller datan när fragment hamnar på backstack.
           Därmed ska ingredienser endast ritas ut om variabel 'ingredients' är tom,
           för att undvika dubbel utritning när användare navigerar tillbaka från annat fragment*/
        if(ingredients == null || ingredients.toString().isEmpty()) {
            for (String ingredient : shoppingList.getRecipe().getIngredientLines()) {
                ingredients.append(String.format("%s\n\n", ingredient));
            }
        }

        tvViewRecipe.setPaintFlags(tvViewRecipe.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvViewRecipe.setText(R.string.textview_view_recipe);
        tvLabel.setText(shoppingList.getRecipe().getLabel());
        tvCreatedAt.setText(context.getResources().getString(R.string.shopping_list_created_at,
                Utils.formatDate(shoppingList.getCreatedAt())));
        tvLines.setText(ingredients);
        tvViewRecipe.setOnClickListener(this);
    }


    /**
     * Dela shopping-lista genom att omdirigera till telefonens
     * väljar-app. På så sätt kan användare själv välja
     * hur recept ska delas, ex. via sms eller mejl.
     */
    private void shareShoppingList() {
        if(shoppingList == null || shoppingList.getRecipe().getIngredientLines() == null || shoppingList.getRecipe().getUrl() == null) {
            UIControls.setIconColor(shareIcon, ContextCompat.getColor(context, R.color.white));
            return;
        }

        //Dela shoppinglista
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType(context.getResources().getString(R.string.intent_share_type_text));
        intent.putExtra(Intent.EXTRA_SUBJECT, String.format("%s%s\n", shoppingList.getRecipe().getLabel(), " — Shopping list"));
        intent.putExtra(Intent.EXTRA_TEXT, String.format("%s%s%s", ingredients, "Full recipe: ", shoppingList.getRecipe().getUrl()));
        startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.intent_share_title)));
    }


    /**
     * Hantera klick.
     * @param view innehåller den vy som klickats
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fragment_shopping_list_details_tv_view_recipe) {

            //Instansiera navController
            final NavController navController = Navigation.findNavController(view);

            /* Lägg det recept som är kopplat till shoppinglistan i ett bundle.
            Navigera sedan till fragment 'RecipeDetailsFragment' via navigeringsaktionen
            'action_shopping_lists_details_to_recipe_details' som finns definierad
            i /navigation/app_navgraph.xml */
            final Bundle bundle = new Bundle();
            bundle.putSerializable(context.getString(R.string.bundle_key_recipe_details), shoppingList.getRecipe());
            navController.navigate(R.id.action_shopping_lists_details_to_recipe_details, bundle);
        }
    }


    /**
     * När fragment återupptas, sätts
     * färg på delningsikon till vit.
     * Detta görs eftersom ikonfärg sätts
     * till orange när shoppinglista delas.
     */
    @Override
    public void onResume() {
        super.onResume();
        if(shareIcon != null) {
            UIControls.setIconColor(shareIcon, ContextCompat.getColor(context, R.color.white));
        }
    }
}

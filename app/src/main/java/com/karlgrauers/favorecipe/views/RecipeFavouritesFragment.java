package com.karlgrauers.favorecipe.views;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.karlgrauers.favorecipe.R;
import com.karlgrauers.favorecipe.adapters.recipe_adapter.RecipeFavouritesDbAdapter;
import com.karlgrauers.favorecipe.adapters.OnAdapterClickListener;
import com.karlgrauers.favorecipe.models.RecipeWithFavourite;
import com.karlgrauers.favorecipe.view_components.dialogbox.OnDialogClickListener;
import com.karlgrauers.favorecipe.utils.UIControls;
import com.karlgrauers.favorecipe.view_components.dialogbox.DialogBoxFragment;
import com.karlgrauers.favorecipe.viewmodels.database_viewmodel.RecipeFavouritesDbViewModel;


/*
 * Klass med fragment för listvy av recept. Denna klass utnyttjar
 * samma layout för listvyn som klass 'RecipeSearchFragment' På varje
 * receptobjekt finns även ett kryss som kan klickas för att radera
 * favoritreceptet ur databasen. Vyn innehåller också funktionalitet
 * för att filtrera bland receptfavoriterna. Möjlighet finns också
 * att radera samtliga recept, via en ikon i optionemenyn.
 */


public class RecipeFavouritesFragment extends Fragment implements View.OnClickListener, OnAdapterClickListener, OnDialogClickListener {
    private Context context;
    private ActionBar actionBar;
    private RecipeFavouritesDbViewModel recipeFavouritesViewModel;
    private RecipeFavouritesDbAdapter recipeFavouritesAdapter;
    private ProgressBar pbProgressBar;
    private EditText etRecipeFilter;
    private LinearLayout llSeparator;
    private ConstraintLayout clFilterContainer;
    private TextView tvNoRecipes;
    private ImageView ivFilterCloseIcon;
    private MenuItem deleteAllIcon;
    private String dbSuccessMessage = "", dbErrorMessage = "", dbWarningMessage = "";


    /**
     * Initialiserar fragment.
     * @param savedInstanceState innehåller
     * instansdata om sådan är tillgänlig, annars
     * null.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

        if(recipeFavouritesAdapter == null) {
            recipeFavouritesAdapter = new RecipeFavouritesDbAdapter(context, this);
        }
        //Sätt generiskt varningsmeddelande som ska visas om användare vill radera alla recept
        dbWarningMessage = getResources().getString(R.string.warning_message_db_delete_all,
                "favourite recipes");
    }


    /**
     * Initialiserar view-model och vyer
     * samt sätter textförändringslyssnare.
     * @param inflater innehåller instans
     *      av LayoutInflater.
     * @param container innehåller topp-
     *      vy att placera barnvy i, alltså 'MainActivity'.
     * @return den vy som skapats.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_favourites, container, false);
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initViews(view);
        setTextChangedListener();
        initViewModel();
        return view;
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
     * När optionsmeny skapas sätts referenser till ikon
     * för att radera samtliga favoriter. Denna referens
     * är nödvändig då ikonens färg ändras dynamiskt.
     * @param menu innehåller den meny i vilka ikonerna finns.
     * @param inflater  innehåller instans av layoutinflater.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.overflow_menu_list_view, menu);
        deleteAllIcon = menu.findItem(R.id.action_delete_all);
    }


    /**
     * Lyssnare för ikoner i options-menyn. Kallar på relevanta
     * metoder vid klick (i detta fall endast 'deleteAll').
     * @param item innehåller det menyobjekt som klickats.
     * @return kall till super-klass.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /* Om användare väljer att radera samtliga recept visas dialogbox med
        ok-val och cancel-val. Vid ok-klick raderas alla receptfavoriter ur databasen.
        Vid cancel försvinner dialogruta.*/
        if(item.getItemId() == R.id.action_delete_all) {
            UIControls.setIconColor(deleteAllIcon, ContextCompat.getColor(context, R.color.orange_brown));
            DialogBoxFragment dialogBox = DialogBoxFragment.newInstance(getResources().getString(R.string.warning_header_db_delete_all),
                    dbWarningMessage, true, new OnDialogClickListener() {
                        @Override
                        public void onDialogOkClicked(DialogInterface dialogInterface) {
                            deleteAllFavourites();
                            UIControls.setIconColor(deleteAllIcon, ContextCompat.getColor(context, R.color.white));
                        }

                        @Override
                        public void onDialogCancelClicked(DialogInterface dialogInterface) {
                            UIControls.setIconColor(deleteAllIcon, ContextCompat.getColor(context, R.color.white));
                        }
                    });

            dialogBox.show(requireActivity().getSupportFragmentManager(), getResources().getString(R.string.dialog_tag));
            dialogBox.setCancelable(false);

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialiserar fragmentets vyer.
     * @param view innehåller vy som skapades
     *             i metod 'onCreateView'
     */
    private void initViews(View view) {
        ivFilterCloseIcon = view.findViewById(R.id.iv_filter_close_icon);
        pbProgressBar = view.findViewById(R.id.pb_result_progressbar);
        llSeparator = view.findViewById(R.id.ll_bottom_separator);
        etRecipeFilter = view.findViewById(R.id.et_filter);
        tvNoRecipes = view.findViewById(R.id.tv_no_results);
        RecyclerView recyclerView = view.findViewById(R.id.rw_results);
        clFilterContainer = view.findViewById(R.id.cl_filter_container);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        etRecipeFilter.setHint(getResources().getString(R.string.hint_filter_favourites));
        ivFilterCloseIcon.setOnClickListener(this);
        pbProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recipeFavouritesAdapter);
    }


    /**
     * Initialiserar viewModel 'RecipeFavouritesDbViewModel' och lyssnar
     * på LiveData-objektet 'allRecipesFavourites' i denna.
     * Datan i detta kommer från tabeller för recept och receptfavoriter
     * i appens databas.
     */
    private void initViewModel() {
        if(recipeFavouritesViewModel == null) {
            recipeFavouritesViewModel = new ViewModelProvider(this).get(RecipeFavouritesDbViewModel.class);
        }

        recipeFavouritesViewModel.init();

        //Observera 'allRecipeFavourites'
        recipeFavouritesViewModel.getAll().observe(getViewLifecycleOwner(), allRecipesFavourites -> {
        pbProgressBar.setVisibility(View.GONE);

            //Om allRecipeFavourites är null eller tom har databasfel inträffat eller så saknas receptfavoriter.
            if (allRecipesFavourites == null || allRecipesFavourites.isEmpty()) {
                tvNoRecipes.setVisibility(View.VISIBLE);
                llSeparator.setVisibility(View.GONE);
                clFilterContainer.setVisibility(View.GONE);
                setHasOptionsMenu(false);

                //Databasfel har inträffat, returnera från metod och skriv ut felmeddelande.
                if(allRecipesFavourites == null) {
                    tvNoRecipes.setText(getResources().getString(R.string.error_message_db_recipe_get_all));
                    return;
                }
                //Receptfavoriter saknas, skriv ut meddelande om detta.
                tvNoRecipes.setText(getResources().getString(R.string.no_recipes_in_favourites));

            } else {
                clFilterContainer.setVisibility(View.VISIBLE);
                llSeparator.setVisibility(View.VISIBLE);
                setHasOptionsMenu(true);
            }

            //Skicka receptfavoriter till adapter för utritning i recyclerview.
            recipeFavouritesAdapter.setRecipeFavourites(allRecipesFavourites);


            /* Om sträng från filtrering-edittext inte
            är tom - kalla på adapters filtreringsfunktion med denna sträng.*/
            if(!etRecipeFilter.getText().toString().equals("")) {
                recipeFavouritesAdapter.filter(etRecipeFilter.getText().toString());
            }
        });

        /* Observera LiveData-objekt med rader från receptfavorit-tabell i databasen.
        Detta görs för att veta huruvida favorit-recept-tabell i databasen förändrats,
        alltså om favorit lagts till eller tagits bort. */
        recipeFavouritesViewModel.getAffectedRows().observe(getViewLifecycleOwner(), affectedRows -> {
            if(affectedRows == null) {
                return;
            }

            /*Om antalet förändrade rader är noll eller mindre har databasoperation gått fel.
            Skriv ut felmeddelande i dialogruta. Annars har operation lyckats, skriv
            ut lyckades-meddelande i toast*/
            if(affectedRows <= 0) {
                DialogBoxFragment dialogBox = DialogBoxFragment.newInstance(getResources().getString(R.string.error_header_db),
                        dbErrorMessage, false, this);
                dialogBox.show(requireActivity().getSupportFragmentManager(), getResources().getString(R.string.dialog_tag));
            } else {
                UIControls.displayToast(dbSuccessMessage, context);
            }
            recipeFavouritesViewModel.resetAffectedRows();
        });
    }

    /**
     * Sätt textförändringslyssnare på filtreringsfunktionens edittext.
     */
    private void setTextChangedListener() {
        etRecipeFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            //Om text finns i edittext, visa ikon för att rensa fältet på text. Annars dölj denna ikon.
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")) {
                    ivFilterCloseIcon.setVisibility(View.GONE);
                } else {
                    ivFilterCloseIcon.setVisibility(View.VISIBLE);
                }
            }

            //Filtrera favoritrecept-lista baserat på vad som skrivs in i edittext-fältet.
            @Override
            public void afterTextChanged(Editable editable) {
                recipeFavouritesAdapter.filter(editable.toString());
            }
        });
    }


    /**
     * Radera samtliga receptfavoriter.
     * Sätter text-strängar för felmeddelande
     * och lyckades-meddelande.
     */
    private void deleteAllFavourites() {
        dbSuccessMessage = getResources().getString(R.string.success_message_db_delete_all,
                "recipes from favourites");
        dbErrorMessage = getResources().getString(R.string.error_message_db_delete_all,
                "favourite recipes");
        recipeFavouritesViewModel.deleteAll();
    }


    /**
     * Hantera klick.
     * @param view innehåller den vy som klickats
     */
    @Override
    public void onClick(View view) {

        //Om kryss-ikon i filtrerings edittext klickas, rensa text.
        if(view.getId() == R.id.iv_filter_close_icon) {
            etRecipeFilter.setText("");
            recipeFavouritesAdapter.filter("");
        }
    }

    /**
     * Hantera klick på raderaikon på vy i recylerview
     * (som sätts av adaptern, därav namnet på metoden).
     * @param object innehåller det objekt som skickas
     *               med vid klick. i detta fall
     *               ett receptfavoritobjekt.
     */
    @Override
    public void onAdapterItemDeleteClicked(Object object) {

        //Radera receptfavorit från databas. Sätt felmeddelande och lyckades-meddelande.
        RecipeWithFavourite recipeWithFavourite = (RecipeWithFavourite) object;
        dbSuccessMessage = getResources().getString(R.string.success_message_db_recipe_add_or_delete,
                "Removed", recipeWithFavourite.getRecipe().getLabel(), "from");
        dbErrorMessage = getResources().getString(R.string.error_message_db_recipe_add_or_delete,
                "delete", recipeWithFavourite.getRecipe().getLabel(), "from");
        recipeFavouritesViewModel.delete(recipeWithFavourite.getFavourite());
    }

    /**
     * Hantera klick på länk i vy i recylerview
     * (som sätts av adaptern, därav namnet på metoden).
     * @param view den vy som klickats.
     * @param object innehåller det objekt som skickas
     *               med vid klick. i detta fall
     *               ett receptfavoritobjekt.
     */
    @Override
    public void onAdapterItemLinkClicked(Object object, View view) {
        RecipeWithFavourite recipeWithFavourite = (RecipeWithFavourite) object;

        //Instansiera navController
        final NavController navController = Navigation.findNavController(view);

        /* Lägg det recept som är kopplat till receptfavoritobjektet i ett bundle. Navigera sedan till
        fragment 'RecipeDetailsFragment' via navigeringsaktionen
        'action_recipe_favourites_to_recipe_details' som finns definierad i /navigation/app_navgraph.xml*/
        final Bundle bundle = new Bundle();
        bundle.putSerializable(context.getString(R.string.bundle_key_recipe_details), recipeWithFavourite.getRecipe());
        navController.navigate(R.id.action_recipe_favourites_to_recipe_details, bundle);
    }


    //Stubbe som måste implementeras. Används ej av klass.
    @Override
    public void onDialogOkClicked(DialogInterface dialogInterface) { }

    //Stubbe som måste implementeras. Används ej av klass.
    @Override
    public void onDialogCancelClicked(DialogInterface dialogInterface) { }
}

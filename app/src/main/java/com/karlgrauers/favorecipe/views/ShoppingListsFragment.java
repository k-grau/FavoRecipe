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
import com.karlgrauers.favorecipe.adapters.ShoppingListDbAdapter;
import com.karlgrauers.favorecipe.adapters.OnAdapterClickListener;
import com.karlgrauers.favorecipe.models.RecipeWithShoppingLists;
import com.karlgrauers.favorecipe.view_components.dialogbox.OnDialogClickListener;
import com.karlgrauers.favorecipe.models.shopping_list.ShoppingList;
import com.karlgrauers.favorecipe.utils.UIControls;
import com.karlgrauers.favorecipe.view_components.dialogbox.DialogBoxFragment;
import com.karlgrauers.favorecipe.viewmodels.database_viewmodel.ShoppingListDbViewModel;




/*
 * Klass med fragment för listvy av shoppinglistor. På varje
 * shoppingliste-objekt finns ett kryss som kan klickas för att radera
 * shoppinglistan ur databasen. Vyn innehåller också funktionalitet
 * för att filtrera bland shoppinglistorna. Möjlighet finns också
 * att radera samtliga shoppinglistor, via en ikon i optionemenyn.
 */

public class ShoppingListsFragment extends Fragment implements View.OnClickListener, OnAdapterClickListener, OnDialogClickListener {
    private Context context;
    private ActionBar actionBar;
    private ShoppingListDbViewModel shoppingListViewModel;
    private ShoppingListDbAdapter shoppingListAdapter;
    private ProgressBar pbProgressBar;
    private EditText etShoppingListsFilter;
    private LinearLayout llSeparator;
    private ConstraintLayout clFilterContainer;
    private TextView tvNoShoppingLists;
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

        if(shoppingListAdapter == null) {
            shoppingListAdapter = new ShoppingListDbAdapter( this, getContext());
        }
        //Sätt generiskt varningsmeddelande som ska visas om användare vill radera alla shoppinglistor.
        dbWarningMessage = getResources().getString(R.string.warning_message_db_delete_all,
                "shopping lists");
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
        View view = inflater.inflate(R.layout.fragment_shopping_lists, container, false);
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
     * för att radera samtliga shoppinglistor. Denna referens
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

        /* Om användare väljer att radera samtliga shoppinglistor visas dialogbox med
        ok-val och cancel-val. Vid ok-klick raderas alla shoppinglistor ur databasen.
        Vid cancel försvinner dialogruta.*/
        if(item.getItemId() == R.id.action_delete_all) {
            UIControls.setIconColor(deleteAllIcon, ContextCompat.getColor(context, R.color.orange_brown));
            DialogBoxFragment dialogBox = DialogBoxFragment.newInstance(getResources().getString(R.string.warning_header_db_delete_all),
                    dbWarningMessage, true, new OnDialogClickListener() {
                        @Override
                        public void onDialogOkClicked(DialogInterface dialogInterface) {
                            deleteAllShoppingLists();
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
        etShoppingListsFilter = view.findViewById(R.id.et_filter);
        tvNoShoppingLists = view.findViewById(R.id.tv_no_results);
        RecyclerView recyclerView = view.findViewById(R.id.rw_results);
        clFilterContainer = view.findViewById(R.id.cl_filter_container);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        etShoppingListsFilter.setHint(getResources().getString(R.string.hint_filter_shopping_lists));
        ivFilterCloseIcon.setOnClickListener(this);
        pbProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(shoppingListAdapter);
    }


    /**
     * Initialiserar viewModel 'ShoppingListsDbViewModel' och lyssnar
     * på LiveData-objektet 'allShoppingLists' i denna.
     * Datan i detta kommer från tabeller för recept och shoppinglistor
     * i appens databas.
     */
    private void initViewModel() {
        if(shoppingListViewModel == null) {
            shoppingListViewModel = new ViewModelProvider(this).get(ShoppingListDbViewModel.class);
        }

        shoppingListViewModel.init();

        //Observera 'allShoppingLists'.
        shoppingListViewModel.getAllShoppingLists().observe(getViewLifecycleOwner(), allShoppingLists -> {
            pbProgressBar.setVisibility(View.GONE);
            int shoppingListsInList = 0;

            //Om 'allShoppingsLists' är null har databasfel inträffat. Returnera från metod och skriv ut felmeddelande.
            if(allShoppingLists == null) {
                tvNoShoppingLists.setVisibility(View.VISIBLE);
                llSeparator.setVisibility(View.GONE);
                clFilterContainer.setVisibility(View.GONE);
                tvNoShoppingLists.setText(getResources().getString(R.string.error_message_db_shopping_lists_get_all));
                return;
            }


            /* Kontrollera samtliga objekt i 'allShoppingLists'. Detta görs eftersom
            recepttabell och shoppinglsite-tabell har en Många till 1-relation. Ett recept
            kan alltså ha flera shoppinglistor. När 'allShoppingLists' hämtas från databasen
            innehåller det en lista receptobjekt som i sin tur har listor med shoppinglisteobjekt påkopplade.
            Problemet är att 'allShoppingLists' kan innehålla receptobjekt som saknar shoppinglisteobjekt,
            listan blir därmed null. Loopen nedan kontrollerar för sådana nullvärden och räknar
            upp variabeln shoppingListsInList om receptobjekten har shoppinglisteobjekt i sina listor. */
            for(RecipeWithShoppingLists recipeWithShoppingLists : allShoppingLists) {
                if(recipeWithShoppingLists.getShoppingLists() != null) {
                    shoppingListsInList += recipeWithShoppingLists.getShoppingLists().size();
                }
            }


            /*Om 'allShoppingsLists' är tom eller variabel shoppingListsInList är 0 saknas shoppinglistor
            i databasen. Skriv ut meddelande om detta och avaktivera optionsmeny.*/

            if (allShoppingLists.isEmpty() || shoppingListsInList <= 0) {
                tvNoShoppingLists.setVisibility(View.VISIBLE);
                llSeparator.setVisibility(View.GONE);
                clFilterContainer.setVisibility(View.GONE);
                setHasOptionsMenu(false);
                tvNoShoppingLists.setText(getResources().getString(R.string.no_shopping_lists_created));

            } else {
                clFilterContainer.setVisibility(View.VISIBLE);
                llSeparator.setVisibility(View.VISIBLE);
                setHasOptionsMenu(true);
            }

            //Skicka shoppinglistor till adapter för utritning i recyclerview.
            shoppingListAdapter.setShoppingLists(allShoppingLists);

            /* Om sträng från filtrering-edittext inte
            är tom - kalla på adapters filtreringsfunktion med denna sträng.*/
            if(!etShoppingListsFilter.getText().toString().equals("")) {
                shoppingListAdapter.filter(etShoppingListsFilter.getText().toString());
            }

        });


        /* Observera LiveData-objekt med rader från shoppinglistet-tabell i databasen.
         Detta görs för att veta huruvida shoppingliste-tabell i databasen förändrats,
         alltså om shoppinglistor lagts till eller tagits bort. */
        shoppingListViewModel.getAffectedRows().observe(getViewLifecycleOwner(), affectedRows -> {
            if(affectedRows == null) {
                return;
            }

            /*Om antalet förändrade rader är noll eller mindre
             har databasoperation gått fel. Skriv ut felmeddelande i dialogruta.
             Annars har operation lyckats, skriv ut lyckades-meddelande i toast*/
            if(affectedRows <= 0) {
                DialogBoxFragment dialogBox = DialogBoxFragment.newInstance(getResources().getString(R.string.error_header_db),
                        dbErrorMessage, false, this);
                dialogBox.show(requireActivity().getSupportFragmentManager(), getResources().getString(R.string.dialog_tag));
            } else {
                UIControls.displayToast(dbSuccessMessage, context);
            }
            shoppingListViewModel.resetAffectedRows();
        });
    }

    /**
     * Sätt textförändringslyssnare på filtreringsfunktionens edittext.
     */
    private void setTextChangedListener() {
        etShoppingListsFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //Om text finns i edittext, visa ikon för att rensa fältet på text. Annars dölj denna ikon.
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")) {
                    ivFilterCloseIcon.setVisibility(View.GONE);
                } else {
                    ivFilterCloseIcon.setVisibility(View.VISIBLE);
                }
            }

            //Filtrera shoppinglista-lista baserat på vad som skrivs in i edittext-fältet.
            @Override
            public void afterTextChanged(Editable editable) {
                shoppingListAdapter.filter(editable.toString());
            }
        });
    }


    /**
     * Hantera klick.
     * @param view innehåller den vy som klickats
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.iv_filter_close_icon) {

            //Om kryss-ikon i filtrerings edittext klickas, rensa på text
            etShoppingListsFilter.setText("");
            shoppingListAdapter.filter("");
        }
    }


    /**
     * Hantera klick på radera-ikon på vy i recylerview
     * (som sätts av adaptern, därav namnet på metoden).
     * @param object innehåller det objekt som skickas
     *               med vid klick. i detta fall
     *               ett shoppingliste-objekt.
     */
    @Override
    public void onAdapterItemDeleteClicked(Object object) {
        ShoppingList shoppingList = (ShoppingList) object;

        //Radera shoppinglista från databas. Sätt felmeddelande och lyckades-meddelande.
        dbSuccessMessage = getResources().getString(R.string.success_message_db_shopping_list_add_or_delete,
                "Removed", shoppingList.getRecipe().getLabel(), "from");
        dbErrorMessage = getResources().getString(R.string.error_message_db_shopping_list_add_or_delete,
                "delete", shoppingList.getRecipe().getLabel(), "from");
        shoppingListViewModel.delete(shoppingList);
    }


    /**
     * Hantera klick på länk på vy i recylerview
     * (som sätts av adaptern, därav namnet på metoden).
     * @param view den vy som klickats.
     * @param object innehåller det objekt som skickas
     *               med vid klick. i detta fall
     *               ett shoppingliste-objekt.
     */

    @Override
    public void onAdapterItemLinkClicked(Object object, View view) {
        //Instansiera navController
        final NavController navController = Navigation.findNavController(view);

         /* Lägg shoppinglist-objekt i Bundle. Navigera sedan till fragment
         'ShoppingListDetails' via navigeringsaktionen 'action_shopping_lists_to_shopping_list_details'
         som finns definierad i /navigation/app_navgraph.xml */
        final Bundle bundle = new Bundle();
        bundle.putSerializable(context.getString(R.string.bundle_key_shopping_list_details), (ShoppingList) object);
        navController.navigate(R.id.action_shopping_lists_to_shopping_list_details, bundle);
    }


    /**
     * Radera samtliga shoppinglistor.
     * Sätter text-strängar för felmeddelande
     * och lyckades-meddelande.
     */
    private void deleteAllShoppingLists() {
        dbSuccessMessage = getResources().getString(R.string.success_message_db_delete_all,
                "shopping lists");
        dbErrorMessage = getResources().getString(R.string.error_message_db_delete_all,
                "shopping lists");
        shoppingListViewModel.deleteAll();
    }

    //Stubbe som måste implementeras. Används ej av klass.
    @Override
    public void onDialogOkClicked(DialogInterface dialogInterface) { }

    //Stubbe som måste implementeras. Används ej av klass.
    @Override
    public void onDialogCancelClicked(DialogInterface dialogInterface) { }
}

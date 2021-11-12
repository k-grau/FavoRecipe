package com.karlgrauers.favorecipe.views;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.karlgrauers.favorecipe.R;
import com.karlgrauers.favorecipe.models.recipe.RecipeFavourite;
import com.karlgrauers.favorecipe.view_components.dialogbox.OnDialogClickListener;
import com.karlgrauers.favorecipe.models.recipe.Recipe;
import com.karlgrauers.favorecipe.models.shopping_list.ShoppingList;
import com.karlgrauers.favorecipe.utils.UIControls;
import com.karlgrauers.favorecipe.view_components.dialogbox.DialogBoxFragment;
import com.karlgrauers.favorecipe.viewmodels.database_viewmodel.RecipeFavouritesDbViewModel;
import com.karlgrauers.favorecipe.viewmodels.database_viewmodel.ShoppingListDbViewModel;
import java.util.Calendar;


/*
 * Klass med fragment för detaljvyn för recept. Vyn består av
 * en webview i vilken recepten visas. En viktig komponent är också
 * optionsmenyn. Med hjälp av denna kan användare lägga till recept
 * i favoriter, dela recept och skapa shoppinglistor.
 */

public class RecipeDetailsFragment extends Fragment implements OnDialogClickListener {
    private WebView wvRecipe;
    private RecipeFavouritesDbViewModel recipeFavouritesViewModel;
    private ShoppingListDbViewModel shoppingListViewModel;
    private Recipe recipe;
    private ActionBar actionBar;
    private MenuItem overflowMenuIcon;
    private MenuItem favouriteIcon;
    private boolean isFavourite = false;
    private String dbSuccessMessage = "", dbErrorMessage = "";
    private Context context;


    /**
     * Initialiserar fragment.
     * @param savedInstanceState innehåller
     * instansdata om sådan är tillgänlig, annars
     * null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

        //Data från det recept som klickades i förgående vy (sökvyn)
        if(getArguments() != null) {
            this.recipe = (Recipe) getArguments().getSerializable(getString(R.string.bundle_key_recipe_details));
        }
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);
        setOptionsMenuListener();
    }


    /**
     * Initialiserar view-model och vyer
     * samt sätter titel (receptets namn) i Actionbar.
     * @param inflater innehåller instans
     *      av LayoutInflater.
     * @param container innehåller topp-
     *      vy att placera barnvy i, alltså 'MainActivity'.
     * @return den vy som skapats.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_details,
                container, false);

        RelativeLayout rlViewContainer = view.findViewById(R.id.fragment_recipeview_container);
        actionBar.setTitle(recipe.getLabel());
        initRecipeViewModel();
        initWebView(view);
        initShoppingListViewModel();
        return rlViewContainer;
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
     * När optionsmeny skapas sätts referenser till ikoner
     * för favorit och för att expandera menyn. Dessa referenser
     * är nödvändiga då dessa ikoners färg ändras dynaminskt.
     * @param menu innehåller den meny i vilka ikonerna finns.
     * @param inflater  innehåller instans av layoutinflater.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.overflow_menu_recipe_details, menu);
        overflowMenuIcon = menu.findItem(R.id.icon_expand_overflowmenu);
        favouriteIcon = menu.findItem(R.id.action_favourite);
    }


    /**
     * Lyssnare för ikoner i options-menyn. Kallar på relevanta
     * metoder vid klick.
     * @param item innehåller det menyobjekt som klickats.
     * @return kall till super-klass.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_favourite) {
            if(isFavourite) {
                removeFromFavourites();
            } else {
                addToFavourites();
            }
        } else if(item.getItemId() == R.id.action_share_recipe) {
            shareRecipe();
        } else if(item.getItemId() == R.id.action_create_shopping_list) {
            createShoppingList();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Initialiserar webview i vilken receptet öppnas.
     * @param view innehåller föräldravyn till den webview
     *             som ska initialiseras.
     */
    private void initWebView(View view) {
        ProgressBar pbProgress = view.findViewById(R.id.fragment_recipeview_progress_bar);
        wvRecipe = view.findViewById(R.id.fragment_recipeview_web_view);
        String URL = recipe.getUrl().replace(getString(R.string.http_non_secure), getString(R.string.http_secure));


        //Koppla på klient och konfigurera webbview
        wvRecipe.setWebViewClient(new WebViewClient() {

            // Överskugga URL-laddning på alla Android-versioner med högre API-nivå än 25
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    return false;
                }

                /* Om en förfrågan ej är en omdirigering (alltså inte sker automatiskt)
                    skickas användare till extern app. Exempelvis: Om användare klickar
                    länk på sidan i webview så öppnas denna länk i telefonens standardwebbläsare
                    och inte i samma webview. */
                if(!request.isRedirect()) {
                    try{
                        Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                        view.getContext().startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }

            /* Så fort någon del av den laddade sidan är synlig i webview:en
            sätts webview till synlig och progressbar till osynlig. */
            @Override
            public void onPageCommitVisible (WebView view, String url) {
                if(wvRecipe != null) {
                    wvRecipe.setVisibility(View.VISIBLE);
                    pbProgress.setVisibility(View.GONE);
                }
            }
        });
        wvRecipe.getSettings().setJavaScriptEnabled(true);
        wvRecipe.loadUrl(URL);
    }


    /**
     * Initialiserar viewModel 'RecipeFavouritesViewModel'.
     */
    private void initRecipeViewModel() {
        if(recipeFavouritesViewModel == null) {
            recipeFavouritesViewModel = new RecipeFavouritesDbViewModel(requireActivity().getApplication());
        }

        /* Initialisera viewmodel med id från det recept som visas i vyn.
           Om detta recept finns i databasen läggs dess id till i LiveData-objekt
           i viewmodel. Annars läggs null till i LiveData-objektet*/
        recipeFavouritesViewModel.init(recipe.getId());


        //Observera LiveData-objekt med recept-id.
        recipeFavouritesViewModel.getId().observe(getViewLifecycleOwner(), recipeId -> {

            //Sätt färg på favoritikon beroende på om recept är favorit eller ej.
            isFavourite = recipeId != null;
            toggleFavouriteIcon();
        });


        /* Observera LiveData-objekt med rader från receptfavorit-tabell i databasen.
           Detta görs för att veta huruvida favorit-recept-tabell i databasen förändrats,
           alltså om favorit lagts till eller tagits bort. */
        recipeFavouritesViewModel.getAffectedRows().observe(getViewLifecycleOwner(), affectedRows -> {
            if(affectedRows == null) {
                return;
            }

            /*Om antalet förändrade rader är noll eller mindre
              har databasoperation gått fel. Skriv ut felmeddelande i dialogruta.
              Annars har operation lyckats, skriv ut lyckades-meddelande i toast*/
            if(affectedRows <= 0) {
                DialogBoxFragment dialogBox = DialogBoxFragment.newInstance(getResources().getString(R.string.error_header_db), dbErrorMessage, false, this);
                dialogBox.show(requireActivity().getSupportFragmentManager(), getResources().getString(R.string.dialog_tag));
            } else {
                UIControls.displayToast(dbSuccessMessage, context);
                isFavourite = !isFavourite;
                toggleFavouriteIcon();
            }
            recipeFavouritesViewModel.resetAffectedRows();
        });
    }


    /* Observera LiveData-objekt med rader från shoppinslistor-tabell i databasen.
       Detta görs för att veta huruvida shoppinglistor-tabell i databasen förändrats,
       alltså om favorit lagts till eller tagits bort. */
    private void initShoppingListViewModel() {
        if(shoppingListViewModel == null) {
            shoppingListViewModel = new ShoppingListDbViewModel(requireActivity().getApplication());
        }
        shoppingListViewModel.init();

        shoppingListViewModel.getAffectedRows().observe(getViewLifecycleOwner(), affectedRows -> {
            if(affectedRows == null) {
                return;
            }

            /*Om antalet förändrade rader är noll eller mindre
              har databasoperation gått fel. Skriv ut felmeddelande i dialogruta.
              Annars har operation lyckats, skriv ut lyckades-meddelande i toast*/
            DialogBoxFragment dialogBox;
            if(affectedRows <= 0) {
                dialogBox = DialogBoxFragment.newInstance(getResources().getString(R.string.error_header_db),
                        dbErrorMessage, false, this);
            } else {
                dialogBox = DialogBoxFragment.newInstance(getResources().getString(R.string.success_header_db_shopping_list_created),
                        dbSuccessMessage, false, this);
            }
            dialogBox.show(requireActivity().getSupportFragmentManager(), getResources().getString(R.string.dialog_tag));
            shoppingListViewModel.resetAffectedRows();
        });
    }


    /**
     * Lägg till recept i favoriter. Skapar nytt receptfavoritobjekt
     * och sparar detta samt aktuellt receptobjekt till databasen.
     */
    private void addToFavourites() {
        dbSuccessMessage = getResources().getString(R.string.success_message_db_recipe_add_or_delete,
                "Added", recipe.getLabel(), "to");
        dbErrorMessage = getResources().getString(R.string.error_message_db_recipe_add_or_delete,
                "add", recipe.getLabel(), "to");

         /*Favoritrecept innehåller endast id, datum och recept-id
           som refererar till aktuellt recept i recepttabell. De båda
           tabellerna sammankopplas genom 1 till 1-relation*/
        RecipeFavourite recipeFavourite = new RecipeFavourite();
        recipeFavourite.setRecipeId(recipe.getId());
        recipeFavourite.setCreatedAt(Calendar.getInstance().getTime());

        //Spara både recept och receptfavorit.
        recipeFavouritesViewModel.insert(recipe, recipeFavourite);
    }

    /**
     * Ta bort receptfavorit ur databasen.
     * Sätt text-strängar för felmeddelande
     * och lyckades-meddelande.
     */
    private void removeFromFavourites() {
        dbSuccessMessage = getResources().getString(R.string.success_message_db_recipe_add_or_delete,
                "Removed", recipe.getLabel(), "from");
        dbErrorMessage = getResources().getString(R.string.error_message_db_recipe_add_or_delete,
                "delete", recipe.getLabel(), "from");
        recipeFavouritesViewModel.deleteById(recipe.getId());
    }


    /**
     * Lägg till shoppinglista. Skapar ny shoppinglista
     * och sparar denna samt aktuellt receptobjekt till databasen.
     */
    private void createShoppingList() {
        dbSuccessMessage = getResources().getString(R.string.success_message_db_shopping_list_created,
                recipe.getLabel(), getResources().getString(R.string.nav_menu_shopping_lists));
        dbErrorMessage = getResources().getString(R.string.error_message_db_shopping_list_create_or_delete, "create", recipe.getLabel());

        /*Shoppinglista innehåller endast id, datum och recept-id
          som refererar till aktuellt recept i recepttabell. De båda
          tabellerna sammankopplas genom 1 till många-relation*/
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setRecipeId(recipe.getId());
        shoppingList.setCreatedAt(Calendar.getInstance().getTime());
        shoppingListViewModel.insert(recipe, shoppingList);
    }

    /**
     * Dela recept genom att omdirigera till telefonens
     * väljar-app. På så sätt kan användare själv välja
     * hur recept ska delas, ex. via sms eller mejl.
     */
    private void shareRecipe() {
        if(recipe == null || recipe.getUrl() == null) {
            return;
        }
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType(context.getResources().getString(R.string.intent_share_type_text));

        //Dela recept-titel och url
        intent.putExtra(Intent.EXTRA_SUBJECT, recipe.getLabel());
        intent.putExtra(Intent.EXTRA_TEXT, recipe.getUrl());
        startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.intent_share_title)));
    }

    /**
     * Sätt lyssnare på optionsmeny. Om den är
     * vald är ikonen för menyexpandering orange,
     * annars vit.
     */
    private void setOptionsMenuListener() {
        if(actionBar == null) {
            return;
        }
        actionBar.addOnMenuVisibilityListener(isVisible -> {
            if (isVisible) {
                UIControls.setIconColor(overflowMenuIcon, ContextCompat.getColor(context, R.color.orange_brown));
            } else {
                UIControls.setIconColor(overflowMenuIcon, ContextCompat.getColor(context, R.color.white));
            }
        });
    }

    /**
     * Sätt fär på favorit ikonen beroende på om
     * recept är favorit eller ej. Om favorit
     * är ikon orange, annars vit.
     */
    private void toggleFavouriteIcon() {
        int favouriteIconColor;
        if(isFavourite) {
            favouriteIconColor = ContextCompat.getColor(context, R.color.orange_brown);
        } else {
            favouriteIconColor = ContextCompat.getColor(context, R.color.white);
        }
        UIControls.setIconColor(favouriteIcon, favouriteIconColor);
    }

    /**
     * Nulla instans av webview när fragment förstörs.
     */
    private void destroyWebView() {
        if (wvRecipe != null) {
            wvRecipe = null;
        }
    }
    /**
     * Kalla destroyWebView när
     * fragment förstörs.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyWebView();
    }

    //Stubbe, måste implementeras. Används ej av klass
    @Override
    public void onDialogOkClicked(DialogInterface dialogInterface) { }

    //Stubbe, måste implementeras. Används ej av klass
    @Override
    public void onDialogCancelClicked(DialogInterface dialogInterface) { }

}

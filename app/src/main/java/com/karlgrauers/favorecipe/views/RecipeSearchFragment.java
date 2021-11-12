package com.karlgrauers.favorecipe.views;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.flexbox.FlexboxLayout;
import com.karlgrauers.favorecipe.R;
import com.karlgrauers.favorecipe.adapters.recipe_adapter.RecipeSearchApiAdapter;
import com.karlgrauers.favorecipe.adapters.OnAdapterClickListener;
import com.karlgrauers.favorecipe.listeners.SwipeListener;
import com.karlgrauers.favorecipe.models.recipe.Recipe;
import com.karlgrauers.favorecipe.models.recipe.RecipeResponse;
import com.karlgrauers.favorecipe.utils.UIControls;
import com.karlgrauers.favorecipe.view_components.IngredientItem;
import com.karlgrauers.favorecipe.viewmodels.api_viewmodel.RecipeSearchApiViewModel;
import java.util.ArrayList;
import java.util.Objects;



/*
 * Klass med fragment för sökvyn för recept. Vyn består av
 * två huvuddelar. Den första är 'llParentSearchContainer' som innehåller
 * komponenter för själva sökningen såsom edittexts för att skriva
 * in sökord och sökknapp. Om användaren valt att kombinera sina
 * sökord på maträtt med olika ingredienser visas även dessa i
 * denna container. Den andra delen är en recyclerview som visar
 * själva resultaten av sökningen i form av en lista med recept.
 */

public class RecipeSearchFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, OnAdapterClickListener {
    private RecipeSearchApiViewModel recipeSearchViewModel;
    private RecipeSearchApiAdapter recipeSearchAdapter;
    private EditText etDishSearch, etIngredientsSearch;
    private TextView tvNoRecipes;
    private ProgressBar pbProgressBar;
    private RecyclerView recyclerView;
    private ArrayList<IngredientItem> ingredientItems;
    private FlexboxLayout fblIngredientsItemsContainer;
    private LinearLayoutManager layoutManager;
    private LinearLayout llParentSearchContainer, llHideableSearchContainer, llSeparator;
    private Button btnSearch, btnAddIngredient;
    private String recipeNextPageUrl = "", defaultHintColor = "", noResultsMessage = "";
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private static final int ANIMATION_DURATION = 400, MIN_Z_INDEX = -1;
    private final static float SCROLL_MINIMUM = 25, MAX_INGREDIENTS = 5, NO_ALPHA = 0.0f, FULL_ALPHA = 1f;
    private volatile boolean shouldGetNextPage = false;
    private boolean isScrolling = false;
    private GestureDetectorCompat detector;
    private Context context;


    /**
     * Initialiserar fragment.
     * @param savedInstanceState innehåller
     * instansdata om sådan är tillgänlig, annars
     * null.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(recipeSearchAdapter == null) {
            recipeSearchAdapter = new RecipeSearchApiAdapter(context, this);
        }
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

        //Bakåtnavigation inaktiveras eftersom vyn är startvy för appen.
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }


    /**
     * Initialiserar view-model och vyer
     * samt sätter lyssnare.
     * @param inflater innehåller instans
     *      av LayoutInflater.
     * @param container innehåller topp-
     *      vy att placera barnvy i, alltså 'MainActivity'.
     * @return den vy som skapats.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_search, container, false);
        initApiViewModel();
        initViews(view);
        setListeners();
        defaultHintColor = String.format("#%X", etIngredientsSearch.getCurrentHintTextColor());
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
     * Initialiserar fragmentets vyer.
     * @param view innehåller vy som skapades
     *             i metod 'onCreateView'
     */
    private void initViews(View view) {
        llParentSearchContainer = view.findViewById(R.id.fragment_recipesearch_ll_parent_search_container);
        btnAddIngredient = view.findViewById(R.id.fragment_recipesearch_btn_add);
        btnSearch = view.findViewById(R.id.fragment_recipesearch_btn_search);
        pbProgressBar = view.findViewById(R.id.pb_result_progressbar);
        fblIngredientsItemsContainer = view.findViewById(R.id.fragment_recipesearch_fbl_ingredient_items_container);
        llHideableSearchContainer = view.findViewById(R.id.fragment_hideable_search_container);
        tvNoRecipes = view.findViewById(R.id.tv_no_results);
        etDishSearch = view.findViewById(R.id.fragment_recipesearch_et_dish_search);
        etIngredientsSearch = view.findViewById(R.id.fragment_recipesearch_et_ingredients);
        llSeparator = view.findViewById(R.id.ll_bottom_separator);
        recyclerView = view.findViewById(R.id.rw_results);

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recipeSearchAdapter);


        /*Kontrollera om lista ingerdientItems innehåller element.
          Om ja rita ut dessa på nytt när användare navigerar tillbaka från
          annat fragment. (ingrediensvyerna förloras nämligen när fragmentet hamnar på backstack) */

        if(ingredientItems != null && !ingredientItems.isEmpty()) {
            for(int i = 0; i < ingredientItems.size(); i ++) {
                if(ingredientItems.get(i) != null) {
                    ((ViewGroup) ingredientItems.get(i).getParent()).removeView(ingredientItems.get(i));
                }
                fblIngredientsItemsContainer.addView(ingredientItems.get(i));
            }
        }

        /* Om sökresultat finns, visa grön linje mellan recylerView och sökcontainer.
        Om sökresultat saknas, dölj linje och skriv ut meddelande om att sökresultat saknas */
        if(recipeSearchAdapter.getItemCount() > 0) {
            llSeparator.setVisibility(View.VISIBLE);
        } else {
            llSeparator.setVisibility(View.GONE);
            if(!noResultsMessage.equals("")) {
                tvNoRecipes.setText(noResultsMessage);
                tvNoRecipes.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Initialiserar viewModel 'RecipeSearchApiViewModel' och lyssnar
     * på LiveData-objektet 'recipesResponse' i denna.
     * Datan i detta kommer från API-anropen till Edamams recept-databas (som
     * alltså sker när användare söker på recept).
     */
    private void initApiViewModel() {
        if(recipeSearchViewModel == null) {
            recipeSearchViewModel = new ViewModelProvider(this).get(RecipeSearchApiViewModel.class);
        }

        recipeSearchViewModel.init();

        //Lyssna på recipesResponse
        recipeSearchViewModel.getRecipeResponseLiveData().observe(getViewLifecycleOwner(), recipesResponse -> {
            pbProgressBar.setVisibility(View.GONE);

            /*Om recipesResponse är null betyder detta att internet-uppkoppling
              eller API-anrop fallerat. Om recipesResponse.getHits() är tom betyder det
              att användares sökning ej resulterade i några sökträffar. Skriv ut relevant
              meddelande och returnera från metoden. Är recipesResponse.getHits() null har
              oförutsett fel inträffat. Returnera då från metod utan att skriva ut något*/

            if (recipesResponse == null || recipesResponse.getHits() == null || recipesResponse.getHits().isEmpty()) {
                if(recipesResponse == null) {
                    noResultsMessage = getResources().getString(R.string.error_message_connection_failed);
                } else {
                    noResultsMessage = getResources().getString(R.string.no_recipes_found_tv_text);
                }
                tvNoRecipes.setText(noResultsMessage);
                tvNoRecipes.setVisibility(View.VISIBLE);
                return;
            }

            /*Om sökresultat erhållits, skicka resultatet till adapter för utritning
              i recyclerview. Sätt variabel med aktuell pagineringlänk (alltså den länk som
              används för att ropa på nästa sida av sökresultat, om sådan finns)*/
            llSeparator.setVisibility(View.VISIBLE);
            recipeSearchAdapter.setHits(recipesResponse.getHits());
            setPaginationLink(recipesResponse);
        });
    }

    /**
     * Sätt lyssnare. Lyssnare är:
     *  -scroll-lyssnare på recyclerview med sökresultat
     *  -knapplyssnare på sök-knapp samt lägg-till-ingrediens-knapp.
     *  -textförändringslyssnare på ingrediensfältets edittext.
     *  -swipelyssnare på llHideableSearchContainer (som
     *  innehåller samtliga komponenter i sökcontainern utom sökfältets edittext)
     *
     */
    private void setListeners() {
        setScrollListener(recyclerView);
        btnAddIngredient.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        setTextChangedListener();
        setSwipeListener();
    }


    /**
     * Sätt swipe-lyssnare så användare
     * kan dölja merpart av sökcontainer (alla
     * vyer utom edittext med sökord) vid swipe upp
     * och visa hela containern vid swipe ner.
     *
     */
    private void setSwipeListener() {
        detector = new GestureDetectorCompat(context, new SwipeListener() {

            @Override
            public boolean onSwipe(Direction direction) {

                //Animera och dölj ingredienssökning och sökknapp
                if (direction == Direction.up) {
                    UIControls.animateAndHide(llHideableSearchContainer, MIN_Z_INDEX,
                            -llHideableSearchContainer.getHeight(), NO_ALPHA, ANIMATION_DURATION);
                    UIControls.hideKeyboard(etDishSearch, context);
                }

                //Animera och visa ingredienssökning och sökknapp
                if (direction == Direction.down) {
                    UIControls.animateAndShow(llHideableSearchContainer, 0,
                            FULL_ALPHA, ANIMATION_DURATION);
                    UIControls.hideKeyboard(etDishSearch, context);
                }
                return true;
            }
        });

        //Sätt touchlyssnare på de vyer som ska reagera på swipe
        llParentSearchContainer.setOnTouchListener(this);
        etDishSearch.setOnTouchListener(this);
        etIngredientsSearch.setOnTouchListener(this);
    }


    /**
     * Sätt textförändringslyssnare på ingredienssöknings edittext.
     * Lägga till ingerdiens blir endast möjligt om ingerdienssökning
     * innehåller text.
     */
    private void setTextChangedListener() {
        etIngredientsSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            /*Aktivera knapp för att lägga till ingrediens om ingredienssökningens
              edittext innehåller text. Annars avaktivera knapp*/
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!etIngredientsSearch.getText().toString().trim().equals("")) {
                    UIControls.enableButton(btnAddIngredient);
                } else {
                    UIControls.disableButton(btnAddIngredient);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }


    /**
     * Sätt scrollyssnare på recyclerview med sökresultat.
     * @param recyclerView den recyclerview som ska scrollas.
     */
    private void setScrollListener(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            /**
             * Kallas vid scrollning.
             * @param dx innehåller vertikal scrollposition, används ej.
             * @param dy innehåller horisontell scrollposition.
             */
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                /*Om värde för horisontell scroll är högre än startposition
                  lagra värden för element som är synliga på skärm respektive har
                  varit synliga på skärm. Om summan av dessa är lika med
                  eller högre än samtliga element i recyclerview har vi nått
                  slutet av listan. När slutet är nått kontrolleras om pagineringslänk
                  finns i sökresultat. Om ja, kalla på metod i recipeSearchViewModel
                  för att få ytterligare sökresultat. */
                if (dy > 0) {
                    UIControls.hideKeyboard(recyclerView, context);
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        if(shouldGetNextPage) {
                            String[] recipesNextPageUrlParts = recipeNextPageUrl.split("/");
                            recipeSearchViewModel.getNextPage(recipesNextPageUrlParts[recipesNextPageUrlParts.length -1]);
                            shouldGetNextPage = false;
                        }
                    }
                }

                /*Om värde för horisontell scroll är högre än värde i SCROLL_MINIMUM.
                  dölj merpart av sökcontainer.*/
                if (dy > SCROLL_MINIMUM) {
                    isScrolling = true;
                    UIControls.animateAndHide(llHideableSearchContainer, MIN_Z_INDEX, -llHideableSearchContainer.getHeight(), NO_ALPHA, ANIMATION_DURATION);
                }

                /*Om värde för horisontell scroll är 0 och användare slutat scrolla,
                  visa merpart av sökcontainer.*/
                else if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && isScrolling) {
                    isScrolling = false;
                    UIControls.animateAndShow(llHideableSearchContainer, 0, FULL_ALPHA, ANIMATION_DURATION);
                }
            }
        });
    }

    /**
     * Om pagineringslänk finns i sökresultat, sätt
     * boolean 'shouldGetNextPage' till true och lagra
     * länk i sträng 'recipeNextPageUrl.
     * @param recipesResponse innehåller objekt med sökresultat.
     */
    private void setPaginationLink(RecipeResponse recipesResponse) {
        if(recipesResponse.getPaginationLink().getNextPage() != null) {
            shouldGetNextPage = !recipesResponse.getPaginationLink().getNextPage().getHref().equals(recipeNextPageUrl);
            recipeNextPageUrl = recipesResponse.getPaginationLink().getNextPage().getHref();
        }
    }


    /**
     * Sök recept genom att kalla på metod
     * 'searchRecipes' i viewmodel, med sökord
     * angivna av användare.
     */
    private void searchRecipes() {
        pbProgressBar.setVisibility(View.VISIBLE);
        tvNoRecipes.setVisibility(View.GONE);
        noResultsMessage = "";
        String dishName = etDishSearch.getEditableText().toString().trim();

        //Om inga sökord angivits för varken maträtt eller ingredienser, returnera från metod utan åtgärd
        if(dishName.matches("\\s*") && (ingredientItems == null || ingredientItems.isEmpty())) {
            pbProgressBar.setVisibility(View.GONE);
            return;
        }

        //Rensa vy om sökord angivits
        llSeparator.setVisibility(View.GONE);
        layoutManager.scrollToPosition(0);
        recipeSearchAdapter.clearHits();

        //Bygg sträng med sökord
        String keywords;
        StringBuilder ingredients = new StringBuilder();

        //Om användare lagt till ingredienser, inkludera dessa i strängen med sökord.
        if(ingredientItems != null && !ingredientItems.isEmpty()) {
            for(IngredientItem ii: ingredientItems) {
                ingredients.append(ii.getIngredient()).append(" ");
            }
            keywords = String.format("%s %s", dishName, ingredients.toString().trim());
        } else {
            keywords = dishName;
        }
        recipeSearchViewModel.searchRecipes(keywords);
    }


    /**
     * Lägg till ingrediens i sökning.
     */
    private void addIngredient () {
        String ingredient = etIngredientsSearch.getEditableText().toString().trim();

        //Om ingrediens som lagts till inehåller tom sträng, returnera från metod utan åtgärd.
        if(ingredient.matches("\\s*")) {
            return;
        }

        if(ingredientItems == null) {
            ingredientItems = new ArrayList<>();
        }

         /*Skapa vy av tillagd ingrediens, och sätt klicklyssnare
            på denna vy för att möjliggöra borttagning av ingrediens.
            Lägg till ingrediensvyn i lista och flexboxcontainer
            'fblIngredientsItemsContainer'.*/
        IngredientItem ingredientItem = new IngredientItem(context, ingredient);
        ingredientItems.add(ingredientItem);
        ingredientItem.setIndex(ingredientItems.size() -1);
        ingredientItem.getCloseIcon().setOnClickListener(this);

        fblIngredientsItemsContainer.addView(ingredientItem);
        Objects.requireNonNull(etIngredientsSearch.getText()).clear();



        /*Om antal ingredienser är lika med MAX_INGREDIENTS skriv ut meddelande
        om att max antal ingredienser är tillagda och avvaktivera edittext-fält.*/
        if(ingredientItems.size() >= MAX_INGREDIENTS) {
            etIngredientsSearch.setHint(getResources().getString(R.string.hint_max_ingredients));
            etIngredientsSearch.setHintTextColor(ContextCompat.getColor(context, R.color.orange_brown));
            etIngredientsSearch.setEnabled(false);
        }
    }


    /**
     * Ta bort ingrediens från sökning
     * @param index innehåller det index för
     *              vilket ingrediens
     *              ska tas bort.
     */
    private void removeIngredient(int index) {


        /*Ta bort ingrediens ur listan med ingredienser, och
            ta bort motsvarande ingrediensvy från skärm*/
        fblIngredientsItemsContainer.removeViewAt(index);
        ingredientItems.remove(index);


        //Uppdatera index i lista med ingrediensvyer
        for(int i = 0; i < ingredientItems.size(); i ++) {
            ingredientItems.get(i).setIndex(i);
        }

        /*Om edittext är avaktiverad och antal ingredienser är under
          värde i MAX_INGREDIENTS, aktivera edittext*/
        if(!etIngredientsSearch.isEnabled() && ingredientItems.size() < MAX_INGREDIENTS) {
            etIngredientsSearch.setHint(getResources().getString(R.string.hint_ingredients));
            etIngredientsSearch.setEnabled(true);
            etIngredientsSearch.setHintTextColor(Color.parseColor(defaultHintColor));
        }
    }


    /**
     * Hantera klick.
     * @param view den vy som klickas.
     */
    @Override
    public void onClick(View view) {

        //Kalla metod för att lägga till ingrediens.
        if (view.getId() == R.id.fragment_recipesearch_btn_add) {
            addIngredient();

        //Kalla metod för att söka recept och dölj tangentbord.
        } else if (view.getId() == R.id.fragment_recipesearch_btn_search) {
            searchRecipes();
            UIControls.hideKeyboard(view, context);

        //Kalla metod för att ta bort ingrediens.
        } else if(view.getId() == R.id.ingredient_item_iv_close_icon) {
            removeIngredient((Integer) view.getTag());
        }
    }

    /**
     * Hantera touches ('vidrörning' på svenska?).
     * @param view den vy som vidröringen utförs på.
     * @param motionEvent den vidröring som utförs.
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //Visa tangentbord vid vidröring
        UIControls.showKeyboard(view, context);
        detector.onTouchEvent(motionEvent);
        view.performClick();
        return true;
    }


    /**
     * Hantera klick på element i recylerview
     * (som sätts av adaptern, därav namnet på metoden).
     * @param view den vy som klickats.
     * @param object innehåller det objekt som skickas
     *               med vid klick. i detta fall
     *               ett receptobjekt.
     */
    @Override
    public void onAdapterItemLinkClicked(Object object, View view) {
        //Instansiera navController
        final NavController navController = Navigation.findNavController(view);

        /*Lägg receptobjekt i Bundle och navigera till vy 'RecipeDetailsFragment'
          med hjälp av navigationsaktionen 'action_recipe_search_to_recipe_details'
          som är definierad i navigationsgrafen (/navigation/app_navgraph.xml)*/
        final Bundle bundle = new Bundle();
        bundle.putSerializable(context.getString(R.string.bundle_key_recipe_details), (Recipe) object);
        navController.navigate(R.id.action_recipe_search_to_recipe_details, bundle);
    }

    //Stubbe, måste implementeras. Används ej i denna klass.
    @Override
    public void onAdapterItemDeleteClicked(Object object) { }
}

package com.karlgrauers.favorecipe.views;
import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karlgrauers.favorecipe.R;
import com.karlgrauers.favorecipe.adapters.OnAdapterClickListener;
import com.karlgrauers.favorecipe.adapters.VideoApiAdapter;
import com.karlgrauers.favorecipe.models.video.VideoItem;
import com.karlgrauers.favorecipe.utils.UIControls;
import com.karlgrauers.favorecipe.utils.Utils;
import com.karlgrauers.favorecipe.viewmodels.api_viewmodel.VideoApiViewModel;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;


/*
 * Klass med fragment för att slumpa fram tio populära mat-videos
 * från youtube. En lista med länkar på upp till tusen videos
 * hämtas från youtubes api och lagras i sharedprefs. Utifrån
 * denna lista slumpas sedan tio videos fram. Nya videos slumpas
 * när användare navigerar till fragment (ej vid bakåtnavigering
 * från annat fragment) via ikonen i bottenmeny. Användare kan också
 * swipea nedåt och på så sätt själv slumpa fram tio nya videos. Listan
 * med videolänkar i sharedprefs uppdateras en gång per dag.
 */

public class VideoRandomizerFragment extends Fragment implements OnAdapterClickListener {

    private final static int REQUESTED_MAX_NO_OF_VIDEOS = 1000;
    private final static int MAX_NO_OF_RANDOMIZED_VIDEOS = 10;
    private int maxNoOfVideoItems = REQUESTED_MAX_NO_OF_VIDEOS;
    private int maxNoOfUsedIndexes = maxNoOfVideoItems - MAX_NO_OF_RANDOMIZED_VIDEOS;
    private static final String SHARED_PREFS = "sharedPrefs", PACKAGE_YOU_TUBE = "com.google.android.youtube";
    private static String today;
    private static String fromDate;
    private RecyclerView recyclerView;
    private TextView tvNoResults, tvSwipeRefreshInstruction;
    private ProgressBar progressBar;
    private Context context;
    private SwipeRefreshLayout srSwipeRefresh;
    private VideoApiViewModel videoApiViewModel;
    private VideoApiAdapter videoApiAdapter;
    private final ArrayList<VideoItem> VIDEO_ITEMS = new ArrayList<>();
    private final ArrayList<VideoItem> RANDOMIZED_VIDEO_ITEMS = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private boolean hasPostedInitialVideos = false;
    private Handler mainHandler;




    /**
     * Initialiserar fragment.
     * @param savedInstanceState innehåller
     * instansdata om sådan är tillgänlig, annars
     * null.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        //From-date anger från vilket datum videos ska hämtas. Sätts till ett år bakåt i tiden från dagens datum.
        fromDate = Utils.getUTCDateString(-1);

        /*Today jämförs med datum i sharedprefs för att avgöra om nya videos ska hämtas.
        Videos hämtas en gång per dag. */
        today = Utils.getUTCDateString(0).split("T")[0];

        if(videoApiAdapter == null) {
            videoApiAdapter = new VideoApiAdapter(this, getLifecycle());
        }

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /* Detta är enda vyn i appen där rotation tillåts. Om telefon är i landskapsvy,
            avaktiveras manuell slumpfunktion för videos. */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            disableRefresh();
        }
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
     * Initialiserar vyer och sätter lyssnare
     * för uppdatering av videos.
     * @param inflater innehåller instans
     *      av LayoutInflater.
     * @param container innehåller topp-
     *      vy att placera barnvy i, alltså 'MainActivity'.
     * @return den vy som skapats.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_randomizer, container, false);
        mainHandler = new Handler(context.getMainLooper());
        initApiViewModel();
        initViews(view);
        setSwipeRefreshListener();

        /* Om fragment ligger på backstack och listan 'RANDOMIZED_VIDEO_ITEMS'
           därmed redan innehåller lista med videos ska videos ej initialiseras. */
        if(RANDOMIZED_VIDEO_ITEMS.size() < MAX_NO_OF_RANDOMIZED_VIDEOS) {
            initVideos();
        }
        return view;
    }


    /**
     * Initialiserar fragmentets vyer.
     * @param view innehåller förälsravy som
     *             skapades i metod 'onCreateView'
     */
    private void initViews(View view) {
        tvNoResults = view.findViewById(R.id.tv_no_results);
        progressBar = view.findViewById(R.id.pb_result_progressbar);
        srSwipeRefresh = view.findViewById(R.id.sr_swipe_refresh);
        tvSwipeRefreshInstruction = view.findViewById(R.id.tv_swipe_refresh_instruction);
        recyclerView = view.findViewById(R.id.rw_results);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        srSwipeRefresh.setColorSchemeColors(ContextCompat.getColor(context, R.color.green),
                ContextCompat.getColor(context, R.color.green), ContextCompat.getColor(context, R.color.green));
        srSwipeRefresh.setEnabled(true);
        tvNoResults.setText(context.getString(R.string.error_message_video_loading));
        tvSwipeRefreshInstruction.setText(getResources().getString(R.string.instruction_swipe_refresh_videos,
                String.valueOf(MAX_NO_OF_RANDOMIZED_VIDEOS)));
        view.findViewById(R.id.ll_bottom_separator).setVisibility(View.GONE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(videoApiAdapter);
    }


    /**
     * Sätter uppdateringslyssnare på vy
     * 'srSwipeRefresh' och möjliggör därmed
     * för användare att få tio nya slumpade
     * videos genom nedåt-swipe.
     */
    private void setSwipeRefreshListener() {
        srSwipeRefresh.setOnRefreshListener(() -> {
            videoApiAdapter.clearAll();
            recyclerView.setAdapter(videoApiAdapter);
            UIControls.animateAndHide(tvSwipeRefreshInstruction, 0, -tvSwipeRefreshInstruction.getHeight(), 0, 100);

            //En sekunds fördröjning på uppdatering för bättre visuell feedback.
            new Handler().postDelayed(() -> {
                initVideos();
                progressBar.setVisibility(View.GONE);
                srSwipeRefresh.setRefreshing(false);
            }, 1000);
        });
    }

    /**
     * Initialiserar videos.
     */
    private void initVideos() {
        //Avaktivera möjlighet till uppdatering medan videos initialiseras.
        disableRefresh();
        progressBar.setVisibility(View.VISIBLE);


       int numberOfVideosRetrieved = 0;

        //Det antal videos som senaste api-anropet lyckades hämta. Siffran behövs för slumpberäkningarna.
        if(retrieveVideoListFromSharedPrefs() != null) {
            numberOfVideosRetrieved = retrieveVideoListFromSharedPrefs().size();
        }


        //Datum då senaste api-anropet gjordes.
        String storedDate = sharedPreferences.getString(context.getString(R.string.shared_prefs_date_key), null);

        //Om dagens datum är senare är datum då senaste api-anrop gjordes, gör nytt api-anrop och returnera från metod.
        if(storedDate == null || today.compareTo(storedDate) > 0) {
            hasPostedInitialVideos = false;
            videoApiViewModel.retrieveVideos(fromDate);
            return;
        }

        //Sätt max antal videos som hämtats och beräkna hur många slumpnummer som kan användas.
        if(numberOfVideosRetrieved > 0) {
            maxNoOfVideoItems = numberOfVideosRetrieved;
            maxNoOfUsedIndexes = maxNoOfVideoItems - MAX_NO_OF_RANDOMIZED_VIDEOS;
        }

        /* Kalla slumpfunktion på separat tråd. Skicka med array VIDEO_ITEMS
           som argument om denna innehåller videolista. Annars skicka med videolista
           hämtad från sharedprefs */
        Runnable runnable = () -> {
            if(VIDEO_ITEMS.size() >= maxNoOfVideoItems) {
                randomizeVideos(VIDEO_ITEMS);
            } else {
                randomizeVideos(retrieveVideoListFromSharedPrefs());
            }
        };
        mainHandler.post(runnable);
    }


    /**
     * Initialiserar viewModel 'VideoApiViewModel' och lyssnar
     * på LiveData-objektet 'videosResponse' i denna.
     * Datan i detta kommer från api-anropen till youtube som görs
     * en gång per dygn.
     */
    private void initApiViewModel() {
        if(videoApiViewModel == null) {
            videoApiViewModel = new ViewModelProvider(this).get(VideoApiViewModel.class);
        }
        videoApiViewModel.init();

        //Observera 'videosResponse'.
        videoApiViewModel.getVideosResponseLiveData().observe(getViewLifecycleOwner(), videosResponse -> {

            /* Om videosResponse är null har internetuppkoppling fallerat.
               Om videosResponse.getItems() är null har okänt fel inträffat.
               Skriv ut generiskt felmeddelande och returnera från metod */
            if(videosResponse == null || videosResponse.getItems() == null) {
                tvNoResults.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                return;
            }

            ///Om  maximala antalet videoitems nåtts ska inga fler api-anrop göras och vi returnerar från metod.
            if(VIDEO_ITEMS.size() >= maxNoOfVideoItems) {
                return;
            }

            //Lägg till videolist från videosResponse
            VIDEO_ITEMS.addAll(videosResponse.getItems());


            /* När resultat av första api-anrop lagts till i VIDEO_ITEMS, shuffla array och
             rita ut på skärm omedelbart. Detta görs eftersom man max får 50 resultat från varje api-anrop
              till youtube. Därmed behöver flera anrop göras och resultaten läggas till i VIDEO_ITEMS.
              Detta innebär ganska lång laddningtid och för att komma runt detta görs alltså en
              "fejkslumpning" med shuffling av de första 50 resultaten i arrayen, och sedan ritas de tio första
              av dessa ut */
            if(VIDEO_ITEMS.size() >= MAX_NO_OF_RANDOMIZED_VIDEOS && !hasPostedInitialVideos) {
                Collections.shuffle(VIDEO_ITEMS);
                for(int i = 0; i < MAX_NO_OF_RANDOMIZED_VIDEOS; i++) {
                    videoApiAdapter.setVideo(VIDEO_ITEMS.get(i));
                }
                videoApiAdapter.notifyDataSetChanged();
                hasPostedInitialVideos = true;
                progressBar.setVisibility(View.GONE);
            }

            /* Önskemålet är att få totalt 1000 videos (se konstant 'REQUESTED_MAX_NO_OF_VIDEOS')
               från youtubes api men detta är inte alltid möjligt och då är 'nextPageToken'
               null. När detta inträffar sätts variabeln 'maxNoOfVideoItems' till det antal videos som hittills
               har hämtats. */
            if(videosResponse.getNextPageToken() != null) {
                videoApiViewModel.getNextPage(fromDate, videosResponse.getNextPageToken());
            } else {
                maxNoOfVideoItems = VIDEO_ITEMS.size();
                maxNoOfUsedIndexes = maxNoOfVideoItems - MAX_NO_OF_RANDOMIZED_VIDEOS;
            }

            /* När maxantal videos hämtats sparas de redan använda indexen (0-9) från
                de videos som redan ritats ut. Sedan sparas själva video-listan och datum
                för hämtningen. Slutligen aktiveras uppdateringsfunktion för videos.
             */
            if(VIDEO_ITEMS.size() >= maxNoOfVideoItems) {
                final List<Integer> usedIndexes = retrieveUsedIndexesFromSharedPrefs();

                if(usedIndexes != null) {
                    for(int i = 0; i < MAX_NO_OF_RANDOMIZED_VIDEOS; i++) {
                        if(!usedIndexes.contains(i)) {
                            usedIndexes.add(i);
                        }
                    }
                    saveUsedIndexesToSharedPrefs(usedIndexes);
                }
                saveVideoListToSharedPrefs(VIDEO_ITEMS);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(context.getString(R.string.shared_prefs_date_key), today);
                editor.apply();
                enableRefresh();
            }
        });
    }


    /**
     * Slumpfunktion för videolista. Slumpar
     * fram index mellan 0 och storleken på videolistan.
     * Lägger till dessa resultat i array 'RANDOMIZED_VIDEO_ITEMS'
     * och sparar använda index till sharedprefs.
     * @param videoItems innehåller den videolista
     *                   som ska slumpas
     *
     */
    private void randomizeVideos(@Nullable List<VideoItem> videoItems) {
        tvNoResults.setVisibility(View.GONE);

        /* Om videoitems är null eller innehåller färre än 10 videos är något fel.
           Skriv ut felmeddelande och returnera från metod */
        if(videoItems == null || videoItems.size() <= MAX_NO_OF_RANDOMIZED_VIDEOS) {
            progressBar.setVisibility(View.GONE);
            tvNoResults.setVisibility(View.VISIBLE);
            return;
        }

        RANDOMIZED_VIDEO_ITEMS.clear();
        final ArrayList<Integer> usedIndexes = new ArrayList<>();

        /* Så länge max antal slumpindex inte har använts, lägg till använda
            index från sharedprefs i array 'usedIndexes'. Om max antal
            är använda, lägg inte till något (vi börjar alltså om). */
        if(retrieveUsedIndexesFromSharedPrefs() != null
                && retrieveUsedIndexesFromSharedPrefs().size() < maxNoOfUsedIndexes) {
            usedIndexes.addAll(retrieveUsedIndexesFromSharedPrefs());
        }

        //Lägg till upp till tio slumpade videos i array 'RANDOMIZED_VIDEO_ITEMS'
        while(RANDOMIZED_VIDEO_ITEMS.size() < MAX_NO_OF_RANDOMIZED_VIDEOS) {
            final int randomNumber = new Random().nextInt(maxNoOfVideoItems);

            if(!usedIndexes.contains(randomNumber)) {
                usedIndexes.add(randomNumber);
                RANDOMIZED_VIDEO_ITEMS.add(videoItems.get(randomNumber));
            }
        }

        /* Spara använda index till sharedprefs och skicka slumpat
        resultat till adapter för utritning i recyclerview. Aktivera
         funktionalitet för uppdatering av slumpade videos*/
        saveUsedIndexesToSharedPrefs(usedIndexes);
        progressBar.setVisibility(View.GONE);
        videoApiAdapter.setVideos(RANDOMIZED_VIDEO_ITEMS);
        enableRefresh();
    }


    /**
     * Omdirigerar användare till youtube. Om you-tube-appen
     * är installerad och aktiverad på telefonen öppnas denna.
     * Annars öppnas användarens standardwebbläsare.
     * @param url innehåller länk till video som ska spelas upp..
     */
    private void redirectToYouTube(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        if(Utils.isAppActive(context, PACKAGE_YOU_TUBE)) {
            intent.setPackage(PACKAGE_YOU_TUBE);
        }
        context.startActivity(intent);
    }


    /**
     * Aktivera funktionalitet för att uppdatera slumpade
     * videos genom swipe. Animera och visa instruktion
     * för slumpfunktion.
     */
    private void enableRefresh() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            srSwipeRefresh.setEnabled(true);
            UIControls.animateAndShow(tvSwipeRefreshInstruction, 0, 1f, 600);
        }
    }

    /**
     * Avaktivera funktionalitet för att uppdatera slumpade
     * videos genom swipe. Animera och dölj instruktion
     * för slumpfunktion.
     */
    private void disableRefresh() {
        srSwipeRefresh.setEnabled(false);
        UIControls.animateAndHide(tvSwipeRefreshInstruction, 0, -tvSwipeRefreshInstruction.getHeight(), 0, 0);
    }


    /**
     * Omvandla videolista till json-sträng och spara i sharedprefs.
     * @param videoItems innehåller videolista som ska sparas.
     */
    private void saveVideoListToSharedPrefs(List <VideoItem> videoItems) {
        Gson gson = new Gson();
        String json = gson.toJson(videoItems);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.shared_prefs_video_list_key), json);
        editor.apply();
    }

    /**
     * Omvandla lista med använda slumpindex till json-sträng och
     * spara i sharedprefs.
     * @param usedIndexes innehåller indexlista som ska sparas.
     */
    private void saveUsedIndexesToSharedPrefs(List<Integer> usedIndexes) {
        Gson gson = new Gson();
        String json = gson.toJson(usedIndexes);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.shared_prefs_used_indexes_key), json);
        editor.apply();
    }

    /**
     * Hämta videolista som json-sträng från sharedprefs och
     * omvandla denna till ista av typ 'VideoItem'
     * @return lista med videoItems om json-sträng finns i sharedprefs, annars null.
     */
    private List <VideoItem> retrieveVideoListFromSharedPrefs() {
        String serializedList = sharedPreferences.getString(context.getString(R.string.shared_prefs_video_list_key), null);
        if(serializedList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<VideoItem>>(){}.getType();
        return gson.fromJson(serializedList, type);
    }

    /**
     * Hämta använda slumpindex som json-sträng från sharedprefs och
     * omvandla denna till ista av typ integer.
     */
    private List <Integer> retrieveUsedIndexesFromSharedPrefs() {
        String serializedList = sharedPreferences.getString(context.getString(R.string.shared_prefs_used_indexes_key), null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Integer>>(){}.getType();
        return gson.fromJson(serializedList, type);
    }


    /**
     * Kallas om telefon roteras.
     * Om telefon är i porträttläge, aktivera uppdatering av slumpade videos.
     * Om telefon är i landskapsläge avaktivera uppdatering av slumpade videos.
     * @param newConfig innehåller den konfiguration som satts efter telefon roterats.
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            disableRefresh();
        }
        else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            enableRefresh();
        }
    }

    /**
     * Hantera klick på länk på vy i recylerview
     * (som sätts av adaptern, därav namnet på metoden).
     * @param view den vy som klickats.
     * @param object innehåller det objekt som skickas
     *               med vid klick, i detta fall
     *               en länk till youtuube-video som
     *               ska visas.
     */
    @Override
    public void onAdapterItemLinkClicked(Object object, View view) {
        String url = (String) object;
        redirectToYouTube(url);
    }

    //Stubbe, måste implementeras. Används ej i klass.
    @Override
    public void onAdapterItemDeleteClicked(Object object) { }
}

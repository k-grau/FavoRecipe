package com.karlgrauers.favorecipe.views;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karlgrauers.favorecipe.R;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;


/*
 * Matapp som låter användare söka recept via
 * Edamams API: https://developer.edamam.com/edamam-recipe-api.
 * Recept kan sökas via ett eller flera nyckelord, de kan läggas
 * till som favoriter och shoppinglistor kan skapas utifrån deras ingredienser.
 * Både receptfavoriter och shoppinglistor lagras i en SQL-lite databas
 * i telefonen. Recept och shoppinglistor kan också delas
 * via valfri app i enheten (som exempelvis SMS eller Epost).
 * Användare kan även slumpa fram populära mat-videos från youtube.
 * Dessa kan ses internt i appen eller så kan man välja att öppna
 * dem i annan app i telefonen (youtube-app är default, annars omdirigeras
 * till standardwebbläsare).
 * @author Karl Grauers
 */



/*
 * Appen är byggd enligt "Single activity architecture" och utnyttjar
 * Jetpacks navigationskomponenter: https://developer.android.com/guide/navigation
 * Klass MainActivity är ansvarig för att initialisera
 * navigationskomponenterna och lyssna på navigationsförändringar samt
 * rotationsförändringar. Alla vyer är skapade som fragment och "hostas"
 * av denna aktivitet.
 */

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNav;
    private ActionBar actionBar;


    /**
     * Initialiserar huvudaktivitet 'MainActivity'.
     * Sätter Actionbar-vy och navigationsvy.
     * @param savedInstanceState innehåller
     * instansdata om sådan är tillgänlig, annars
     * null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        bottomNav = findViewById(R.id.bottom_navigation);
        setBottomNavigation();
        setDestinationChangedListener();
    }



    /**
     * Initialiserar navigationsfunktionalitet
     * och kopplar navigationskontroll och
     * navigationsgraf till navigationsvy
     * och Action-bar. (Navigationsgraf finns i
     * /navigation/app_navgraph.xml)
     */
    private void setBottomNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = Objects.requireNonNull(navHostFragment).getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(bottomNav, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }


    /**
     * Sätter navigationslyssnare på navigationskontroll.
     * Om användare befinner sig i fragment 'video_randomizer_fragment'
     * tillåts full-sensor-skärm och rotationer - i alla andra fragment
     * är skärm låst till porträtt-läge.
     */
    private void setDestinationChangedListener() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            if(destination.getId() == R.id.video_randomizer_fragment) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
            }
            setRequestedOrientation(requestedOrientation);
        });
    }

    /**
     * Om användare klickar bakåtpil i Actionbar
     * kallas på onBackPressed ocn användare skickas
     * till föregående fragment
     * @param item innehåller den Menykomponent som klickats.
     * @return kall på superklass
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Om skärm roteras till landskapsvy (tillåts endast i 'video_randomizer_fragment')
     * döljs Actionbar och Navigationsmeny. Annars visas de.
     * @param newConfig innehåller objekt med data som satts
     *                  efter konfigurationsförändring (rotation i detta fall).
     */
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomNav.setVisibility(View.GONE);
            actionBar.hide();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            bottomNav.setVisibility(View.VISIBLE);
            actionBar.show();
        }
    }
}
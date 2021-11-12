package com.karlgrauers.favorecipe.repositories.database_repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.karlgrauers.favorecipe.database.AppDatabase;


/*
 * Superklass för databas-klasser vars
 * data kommer från appens interna databas.
 */

public abstract class DatabaseRepository {

    protected long affectedRows = -1;
    protected final MutableLiveData<Long> AFFECTED_ROWS;
    protected final AppDatabase DATABASE;


    /**
     * Konstruktor. Sätter instans av databas
     * och initialiserar LiveData-objekt
     * AFFECTED_ROWS.
     * @param application innehåller instans av applikationsklass
     */
    public DatabaseRepository(Application application) {
        DATABASE = AppDatabase.getDatabase(application);
        AFFECTED_ROWS = new MutableLiveData<>();
    }

    /**
     * Initialisera repository. Implementeras
     * i subklasser.
     */
    public abstract void init();


    /**
     * Radera allt från tabell i databas
     * Implementeras i subklasser.
     */
    public abstract void deleteAll();


    /**
     * Getter för fält AFFECTED_ROWS. Används
     * i klasser 'RecipeFavouritesDbViewModel'
     * och 'ShoppingListDbViewModel'.
     * AFFECTED_ROWS default-värde är -1
     * och ändras vid databasoperationer
     * såsom när rader i tabell tas bort
     * eller läggs till. Då sätts det till
     * antal påverkade rader.
     * @return antal påverkade rader
     */
    public LiveData<Long> getAffectedRows() {
        return AFFECTED_ROWS;
    }


    /**
     * Efter databasoperation nollställs
     * värde på 'AFFECTED_ROWS'.
     */
    public void resetAffectedRows() {
        AFFECTED_ROWS.postValue(null);
    }
}

package com.karlgrauers.favorecipe.viewmodels.database_viewmodel;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/*
 * Superklass för 'RecipeFavouritesViewModel' och 'ShoppingListDbViewModel'.
 *  Ärver av android-interna klassen AndroidViewModel.
 */

public abstract class DatabaseViewModel extends AndroidViewModel {

    protected LiveData<Long> affectedRows;

    /**
     * Konstruktor. Kallar konstruktor i superklass.
     * @param application innehåller instans av applikationsklass.
     */
    public DatabaseViewModel(@NonNull Application application) {
        super(application);
    }

    //Initialisera viewmodel. Implementeras i subklasser
    public abstract void init();

    /**
     * Getter för antal påverkade rader i databas vid databasoperation.
     * Får sitt värde av motsvarande variabel i klass 'DataBaseRepository'
     * @return antal påverkade rader i databasen.
     */
    public LiveData<Long> getAffectedRows() {
        return affectedRows;
    }

    //Nollställ affectedRows. Implementeras i sub-klasser.
    public abstract void resetAffectedRows();

}

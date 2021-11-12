package com.karlgrauers.favorecipe.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.karlgrauers.favorecipe.models.recipe.RecipeFavourite;
import com.karlgrauers.favorecipe.models.recipe.Recipe;
import com.karlgrauers.favorecipe.models.shopping_list.ShoppingList;
import com.karlgrauers.favorecipe.repositories.database_repository.RecipeFavouritesDao;
import com.karlgrauers.favorecipe.repositories.database_repository.ShoppingListDao;
import com.karlgrauers.favorecipe.utils.Converters;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/*
 * Klass för att initalisera och hantera appens SQLLite-databas.
 * Använder databashanteraren Room: https://developer.android.com/training/data-storage/room
 */


//Entiter som ingår, motsvaras av tabeller i databasen.
@Database(entities = {Recipe.class, ShoppingList.class, RecipeFavourite.class}, version = 13)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private final static String DB_NAME = "favorecipe_database";
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService DATABASE_WRITE_EXECUTOR =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    //Referens till abstrakt klass som används av klass ShoppingListDbRepository för att göra databasoperationer.
    public abstract ShoppingListDao shoppingListDao();

    //Referens till abstrakt klass som används av klass RecipeFavouritesDbRepository för att göra databasoperationer.
    public abstract RecipeFavouritesDao recipeFavouritesDao();


    /**
     * Getter för instans av databas. Om instans redan är skapad retuneras
     * denna. Annars skapas ny instans. Därmed kommer endast en instans
     * av databasen att existera under hela appens livscykel.
     * @param context innehåller aktivitetskontexten.
     * @return instans av databasen.
     */
    public static synchronized AppDatabase getDatabase(Context context){
        if(INSTANCE == null){
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    /**
     * Skapa databas om databas ej finns i telfonen. Dvs, första
     * gången appen används.
     */
    private static final RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}

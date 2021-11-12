package com.karlgrauers.favorecipe.view_components;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.karlgrauers.favorecipe.R;
import java.io.Serializable;


/*
 * Klass för att skapa objekt av ingrediensvyer. Ärver
 * av interna klassen LinnearLayout
 */

public class IngredientItem extends LinearLayout implements Serializable {

    private String ingredient;
    private ImageView ivCloseIcon;



    /**
     * Overloaded konstruktor. Kallar konstruktor i superklass och skapar
     * objekt av ingerdiensvy.
     * @param context innehåller aktivitetskontexten.
     * @param ingredient innehåller sträng med texten för ingrediens.
     */
    public IngredientItem(Context context, String ingredient) {
        super(context);
        this.ingredient = ingredient;
        create(context);
    }


    /**
     * Overloaded konstruktor. Kallar konstruktor i superklass.
     * @param context innehåller aktivitetskontexten.
     * @param attrs innehåller attribut från 'layout_ingredient_item.xml'
     */
    public IngredientItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * Skapa ingerdiensobjekt genom att "inflata" (blåsa upp?)
     * layouten som ska användas, 'layout_ingredient_item.xml'
     * och sätt text för ingrediens samt ikon för borttagning (alltså ett 'X')
     * @param context innehåller aktivitetskontexten.
     */
    private void create(Context context) {
        View ingredientItem = inflate(context, R.layout.layout_ingredient_item, this);
        TextView tvIngredientText = ingredientItem.findViewById(R.id.ingredient_item_tv_text);
        tvIngredientText.setText(ingredient);
        ivCloseIcon = ingredientItem.findViewById(R.id.ingredient_item_iv_close_icon);
    }

    //Getter för close-icon. Används av klass 'RecipeSearchFragment' för att sätta klicklyssnare.
    public ImageView getCloseIcon() {
        return ivCloseIcon;
    }

    //Getter för ingredienstext.
    public String getIngredient() {
        return ingredient;
    }

    //Setter för index på X-ikon. Används av klass 'RecipeSearchFragment'
    // för att sätta nya index när ingrediens tas bort.
    public void setIndex(int index) {
        ivCloseIcon.setTag(index);
    }
}

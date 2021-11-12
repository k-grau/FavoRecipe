package com.karlgrauers.favorecipe.utils;
import static android.content.Context.INPUT_METHOD_SERVICE;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.graphics.drawable.DrawableCompat;


/*
 * Abstrakt klass med nyttometoder för manipulering av UI-element.
 * Används av flera olika klasser.
 */

public abstract class UIControls {

    /**
     * Skapar toast.
     * @param cs innehåller den text som
     * ska visas.
     * @param context innehåller aktivitetskontexten.
     */
    public static void displayToast(CharSequence cs, Context context) {
        Toast toast = Toast.makeText(context, cs, Toast.LENGTH_SHORT);
        toast.show();
    }


    /**
     * Gör knapp oklickbar och drar ner dess alpha
     * till hälften.
     * @param button den knapp som ska göras oklickbar.
     */
    public static void disableButton(Button button) {
        button.setClickable(false);
        button.setAlpha(0.5f);
    }

    /**
     * Gör knapp klickbar och sätter full alpha.
     * @param button den knapp som ska göras klickbar.
     */
    public static void enableButton(Button button) {
        button.setClickable(true);
        button.setEnabled(true);
        button.setAlpha(1f);
    }

    /**
     * Ändrar färg på menyikon.
     * @param item den ikon som ska få annan färg.
     * @param color den färg som ikonen ska ha.
     */
    public static void setIconColor(MenuItem item, int color) {
        Drawable drawable = item.getIcon();
        if(drawable == null) {
            return;
        }
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable.mutate(), color);
    }

    /**
     * Gömmer telefonens tangentbord.
     * @param view den vy som för tillfället är i fokus.
     * @param context innehåller aktivitetskontexten.
     */
    public static void hideKeyboard(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
    }


    /**
     * Visar telefonens tangentbord.
     * @param view den vy som för tillfället är i fokus.
     * @param context innehåller aktivitetskontexten.
     */
    public static void showKeyboard(View view, Context context) {
        view.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Animerar och visar vy.
     * @param view den vy som ska animeras och visas.
     * @param translationY den höjd som vyn ska animeras till.
     * @param alpha slutligt alphavärde för när vyn är fullt synlig (vanligtvis 1.0f)
     * @param duration tid i millisekunder för animation.
     */
    public static void animateAndShow(View view, int translationY, float alpha, int duration) {
       view.animate()
                .translationY(translationY)
                .alpha(alpha)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                });

    }

    /**
     * Animerar och döljer vy.
     * @param view den vy som ska animeras och döljas.
     * @param zIndex plats för vyn på/under andra vyer när den är fullt gömd (vanligtvis -1).
     * @param translationY den höjd som vyn ska animeras till.
     * @param alpha slutligt alphavärde för när vyn är fullt gömd (vanligtvis 0f)
     * @param duration tid i millisekunder för animation.
     */
    public static void animateAndHide(View view, int zIndex, int translationY, float alpha, int duration) {
        view.setZ(zIndex);
        view.animate()
                .translationY(translationY)
                .alpha(alpha)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }
}

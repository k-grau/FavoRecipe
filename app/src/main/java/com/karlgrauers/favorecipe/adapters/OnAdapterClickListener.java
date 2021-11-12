package com.karlgrauers.favorecipe.adapters;
import android.view.View;


/*
 * Interface för att hantera klick på objekt i adapter.
 */

public interface OnAdapterClickListener {


    //Delete-klick
    void onAdapterItemDeleteClicked(Object object);

    //Länk-klick
    void onAdapterItemLinkClicked(Object object, View view);
}

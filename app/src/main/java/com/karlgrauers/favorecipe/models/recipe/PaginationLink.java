package com.karlgrauers.favorecipe.models.recipe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;


/*
 * Modellklass för receptobjekt. Används för att skapa
 * objekt från API-anrop till Edamams recept-api.
 * Fält annoterade med '@SerializedName' får
 * sina värden från fält i API-svaret vars namn
 * motsvarar strängen i '@SerializedName'.
 */

public class PaginationLink implements Serializable {
    //Referens till objekt 'NextPage'
    @SerializedName("next")
    @Expose
    private NextPage nextPage = null;


    /**
     * Getter för fält nextPage.
     */
    public NextPage getNextPage() {
        return nextPage;
    }
}

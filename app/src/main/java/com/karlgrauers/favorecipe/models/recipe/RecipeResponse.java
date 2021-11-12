package com.karlgrauers.favorecipe.models.recipe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;


/*
 * Modellklass för receptobjekt. Används för att skapa
 * objekt från API-anrop till Edamams recept-api.
 * Fält annoterade med '@SerializedName' får
 * sina värden från fält i API-svaret vars namn
 * motsvarar strängen i '@SerializedName'.
 *
 * Denna klass motsvarar det svar som skickas vid
 * anrop till Edamams API.
 */


public class RecipeResponse implements Serializable {

    //Lista med resultat efter sökning.
    @SerializedName("hits")
    @Expose
    private List<RecipeHit> hits = null;

    //Pagineringslänkar.
    @SerializedName("_links")
    @Expose
    private PaginationLink paginationLink = null;


    /*Getter för fält hits*/
    public List<RecipeHit> getHits() {
        return hits;
    }

    /*Getter för fält paginationLink*/
    public PaginationLink getPaginationLink() {
        return paginationLink;
    }

}

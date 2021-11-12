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

public class NextPage implements Serializable {

    //Pagineringslänk som kommer från API-anrop.
    @SerializedName("href")
    @Expose
    private String href = null;

    /**
     * Getter för fält href.
     */
    public String getHref() {
        return href;
    }
}

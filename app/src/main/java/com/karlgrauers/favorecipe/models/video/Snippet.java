package com.karlgrauers.favorecipe.models.video;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/*
 * Modellklass för videoobjekt. Används för att skapa
 * objekt från API-anrop till youtubes api.
 * Fält annoterade med '@SerializedName' får
 * sina värden från fält i API-svaret vars namn
 * motsvarar strängen i '@SerializedName'.
 */

public class Snippet implements Serializable {

    //Youtubes-videos titel
    @SerializedName("title")
    @Expose
    private String title = null;

    //Getter för titel
    public String getTitle() {
        return this.title;
    }
}

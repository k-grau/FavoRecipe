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


public class VideoItem implements Serializable {

    //Id för videoitem. OBS! Ej detsamma som själva video-id i klassen 'Id'
    @SerializedName("id")
    @Expose
    private Id id = null;

    //Referens till klass 'Snippet'
    @SerializedName("snippet")
    @Expose
    private Snippet snippet = null;

    //Getter för id
    public Id getId() {
        return this.id;
    }

    //Getter för snippet
    public Snippet getSnippet() {
        return this.snippet;
    }
}

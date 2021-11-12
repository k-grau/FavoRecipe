package com.karlgrauers.favorecipe.models.video;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

/*
 * Modellklass för videoobjekt. Används för att skapa
 * objekt från API-anrop till youtubes api.
 * Fält annoterade med '@SerializedName' får
 * sina värden från fält i API-svaret vars namn
 * motsvarar strängen i '@SerializedName'.
 *
 * Denna klass innehåller det svar som skickas
 * vid anrop till youtubes api.
 */


public class VideoResponse implements Serializable {

    //Lista med resultat, alltså videos som matchade api-anropet.
    @SerializedName("items")
    @Expose
    private List<VideoItem> items = null;

    //Pagineringslänk
    @SerializedName("nextPageToken")
    @Expose
    private String nextPageToken= null;

    //Getter för lista med videos
    public List<VideoItem> getItems() {
        return items;
    }


    //Getter för pagineringslänk
    public String getNextPageToken() {
        return nextPageToken;
    }



}

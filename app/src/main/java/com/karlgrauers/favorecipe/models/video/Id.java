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



public class Id implements Serializable {

    //Youtube-videons unika id
    @SerializedName("videoId")
    @Expose
    private String videoId = null;

    //Getter för id.
    public String getVideoId() {
        return this.videoId;
    }
}

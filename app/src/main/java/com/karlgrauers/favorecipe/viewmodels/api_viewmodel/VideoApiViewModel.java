package com.karlgrauers.favorecipe.viewmodels.api_viewmodel;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.karlgrauers.favorecipe.models.video.VideoResponse;
import com.karlgrauers.favorecipe.repositories.api_repository.VideoApiRepository;

/*
 * ViewModel-klass som agerar "brygga" mellan UI-klasser
 * och klass 'VideoApiRepository'. Ärver av android-interna
 * klassen AndroidViewModel.
 */


public class VideoApiViewModel extends AndroidViewModel {

    private VideoApiRepository repository;
    private LiveData<VideoResponse> videosResponseLiveData;

    /**
     * Konstruktor. Kallar konstruktor i superklass.
     * @param application innehåller instans av applikationsklass
     */
    public VideoApiViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Instansiera 'VideoApiRepository'.
     * Associera LiveData-objekt 'videosResponseLiveData'
     * med motsvarande LiveData-objekt i 'VideoApiRepository'.
     */
    public void init() {
        repository = new VideoApiRepository();
        videosResponseLiveData = repository.getVideosResponseLiveData();
    }

    /**
     * Hämta lista med videos genom att kalla metod 'retrieveVideos' i
     * klass 'VideoApiRepository'.
     * @param fromDate innehåller det datum från vilket videos ska hämtas.
     *                 I detta fall videos från ett år tillbaka i tiden fram till idag.
     */
    public void retrieveVideos(String fromDate) {
        repository.retrieveVideos(fromDate);
    }


    /**
     * Få nästa sida av videolista genom att kalla metod
     * 'getNextPage' i klass 'VideoApiRepository'.
     * @param fromDate innehåller det datum från vilket videos ska hämtas.
     * @param nextPageToken innehåller unik token som identifierar nästa
     *                      sida med resultat.
     */
    public void getNextPage(String fromDate, String nextPageToken) {
        repository.getNextPage(fromDate, nextPageToken);
    }

    //Getter för LiveData-objekt. Observeras i UI-klass.
    public LiveData<VideoResponse> getVideosResponseLiveData() {
        return videosResponseLiveData;
    }
}

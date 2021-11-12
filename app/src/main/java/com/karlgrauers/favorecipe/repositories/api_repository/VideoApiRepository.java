package com.karlgrauers.favorecipe.repositories.api_repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.karlgrauers.favorecipe.models.video.VideoResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;


/*
 * Repository-klass för att hantera anrop till Youtubes
 * video-api. Ärver av superklass ApiRepository.
 *
 */

public class VideoApiRepository extends ApiRepository {
    private VideoApiService videoApiService;
    private final MutableLiveData<VideoResponse> VIDEOS_RESPONSE_LIVEDATA;


    /**
     * Konstruktor. Sätter bas-url och svarskod för
     * begränsning av API-anrop genom att kalla superklassens
     * konstruktor. Intitaliserar det LiveData-objekt
     * vilket innehåller resultaten från api-anropen.
     */
    public VideoApiRepository() {
        super("https://youtube.googleapis.com/youtube/", 403);
        VIDEOS_RESPONSE_LIVEDATA = new MutableLiveData<>();
    }


    /**
     * Intialisera retrofit med bas-rul och
     * interface 'VideoApiService'. Själva metoden
     * kallas i superklassens konstruktor.
     */
    @Override
    protected void initRetroFit() {
        videoApiService = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(VideoApiService.class);
    }


    /**
     * Hämta videos via anrop till Youtubes video-api.
     * Koden i metoden exekveras asynkront och när
     * svar erhållits från api postas detta i
     * LiveData-objektet 'VIDEOS_RESPONSE_LIVEDATA'
     * @param fromDate innehåller det datum från vilket
     *                 videos ska sökas. Sötts till ett
     *                 år tillbaka i tiden i klass
     *                 VideoRandomizerFragment.
     */
    public void retrieveVideos(String fromDate) {
        videoApiService.retrieveVideos(fromDate)
                .enqueue(new Callback<VideoResponse>() {


                    @Override
                    public void onResponse(@NonNull Call<VideoResponse> call, @NonNull Response<VideoResponse> response) {

                        /* Om en vi får kod för begränsning av sökträffar posta null
                         i LiveData-objekt och returnera från metod. Begränsningar av
                         sökträffar kan ske om man gjort för många sökningar på en dag. Mitt
                         obetalda utvecklarkonto på Google ger mig 10 000 'krediter' att använda
                         per dag och ett anrop kostar 100 krediter. */
                        if(response.code() == CALL_LIMIT_RESPONSE) {
                            VIDEOS_RESPONSE_LIVEDATA.postValue(null);
                            return;
                        }

                        //Om svar erhålles från api, posta kroppen i svaret till LiveData-objekt
                        if(response.body() != null) {
                            VIDEOS_RESPONSE_LIVEDATA.postValue(response.body());
                        }
                    }


                    //Om api-anrop fallerar, posta null till LiveData-objekt
                    @Override
                    public void onFailure(@NonNull Call<VideoResponse> call, @NonNull Throwable t) {
                        VIDEOS_RESPONSE_LIVEDATA.postValue(null);
                    }
                });
    }



    /**
     * Hämta nästa sida med sökträffar i svaret från
     * anropet till Youtubes api. Posta i sin tur detta
     * svar i LiveData-objektet 'VIDEOS_RESPONSE_LIVEDATA'.
     * Koden i metoden exekveras asynkront
     * @param fromDate innehåller det datum från vilket
     *                 videos ska sökas. Sötts till ett
     *                 år tillbaka i tiden i klass
     *                 VideoRandomizerFragment.
     * @param nextPageToken innehåller unik token för att
     *                      identifiera nästa sida i sökresultatet.
     */
    public void getNextPage(String fromDate, String nextPageToken) {
        videoApiService.getNextPage(fromDate, nextPageToken)
                .enqueue(new Callback<VideoResponse>() {

                    //Om svar erhålles från api, posta kroppen i svaret till LiveData-objekt
                    @Override
                    public void onResponse(@NonNull Call<VideoResponse> call, @NonNull Response<VideoResponse> response) {
                        if(response.body() == null) {
                            return;
                        }
                        VIDEOS_RESPONSE_LIVEDATA.postValue(response.body());
                    }

                    //Om api-anrop fallerar, posta null till LiveData-objekt
                    @Override
                    public void onFailure(@NonNull Call<VideoResponse> call, @NonNull Throwable t) {
                        VIDEOS_RESPONSE_LIVEDATA.postValue(null);
                    }
                });
    }

    //Getter för LiveData-objekt, används av klass 'VideoApiViewModel'
    public LiveData<VideoResponse> getVideosResponseLiveData() {
        return VIDEOS_RESPONSE_LIVEDATA;
    }
}

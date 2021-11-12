package com.karlgrauers.favorecipe.repositories.api_repository;

import com.karlgrauers.favorecipe.BuildConfig;
import com.karlgrauers.favorecipe.models.video.VideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VideoApiService {

    /*
     * Interface för anrop till Youtubes-video-api.
     * Används av klass 'RecipeSearchApiRepository'
     *
     */


    //Fält för att bygga url för api-anrop. Api-nyckel i separat fil.
    String VERSION = "v3/";
    String TYPE = "search?";
    String API_KEY = "key=" + BuildConfig.VIDEO_API_KEY;
    String FIELDS = "&part=snippet&order=rating&q=cooking|recipe -kids -children -child -cute -india -hindi " +
            "-korea -japan -china -arab -arabic -tamil&relevanceLanguage=en" +
            "&maxResults=50&type=video&videoCaption=closedCaption&videoEmbeddable=true&videoDefinition=high";

    //Sök recept med från specifikt datum. Datum sätts i query-parameter 'publishedAfter'
    @GET(VERSION + TYPE + API_KEY + FIELDS)
    Call<VideoResponse> retrieveVideos(@Query("publishedAfter") String fromDate);


    //Få nästa sida med sökresultat, genom unik token.
    @GET(VERSION + TYPE + API_KEY + FIELDS)
    Call<VideoResponse> getNextPage(@Query("publishedAfter") String fromDate, @Query("pageToken") String nextPageToken);
}


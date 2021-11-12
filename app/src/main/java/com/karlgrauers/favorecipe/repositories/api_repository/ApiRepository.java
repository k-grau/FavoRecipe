package com.karlgrauers.favorecipe.repositories.api_repository;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


/*
 * Superklass för repository-klasser vars
 * data kommer från API-anrop.
 * Använder interface retrofit för
 * att hantera anropen: https://square.github.io/retrofit/
 */

public abstract class ApiRepository {
    protected final String BASE_URL;
    protected HttpLoggingInterceptor interceptor;
    protected OkHttpClient client;
    protected final int CALL_LIMIT_RESPONSE;


    /**
     * Konstruktor. Sätter bas-url och svarskod för
     * begränsning av API-anrop. Kallar sedan på relevanta
     * metoder för att initialisera http-klient
     * och retrofit.
     * @param baseUrl innehåller url som ska användas
     *                vid anrop (värde kommer från subklass)
     * @param callLimitResponse innehåller svarskod som används när
     *                          max antal resultat fås i api-anropet
     *                          (värde kommer från subklass)
     */
    public ApiRepository(String baseUrl, int callLimitResponse) {
        this.BASE_URL = baseUrl;
        CALL_LIMIT_RESPONSE = callLimitResponse;
        setHttpClient();
        initRetroFit();
    }


    /**
     * Initalisera http-klienten och möjliggör
     * automatisk loggning till konsoll av resultat
     * från API-anrop.
     */
    private void setHttpClient() {
        interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }


    /**
     * Initalisera Retrofit. Implementation sker
     * i subklasser.
     */
    protected abstract void initRetroFit();

}

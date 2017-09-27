package com.example.android.popularmovies.themoviedb;

import com.example.android.popularmovies.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mahmoud on 9/13/17.
 */

public class MovieApiClient {

    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static final String API_KEY_PARAM = "api_key";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                HttpUrl newHttpUrl = originalRequest.url().newBuilder().setQueryParameter(API_KEY_PARAM, BuildConfig.API_KEY).build();
                Request newRequest = originalRequest.newBuilder().url(newHttpUrl).build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        OkHttpClient client = builder.build();

        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}

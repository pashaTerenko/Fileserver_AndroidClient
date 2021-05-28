package com.terenko.fileserver.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.terenko.fileserver.DTO.DTO;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {



    APICatalog apiCatalog;
    APIFile apiFile;
    final String URL="http://192.168.1.120:8081/";
    String token;
    final String BEARER="Bearer_";
    public ApiService(String token) {
        this.token=token;
        Retrofit retrofit = createRetrofit();
        apiCatalog = retrofit.create(APICatalog.class);
        apiFile = retrofit.create(APIFile.class);

    }

    public APICatalog getApiCatalog() {
        return apiCatalog;
    }

    public APIFile getApiFile() {
        return apiFile;
    }

    private OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                final Request original = chain.request();
                final HttpUrl originalHttpUrl = original.url();
                final HttpUrl url = originalHttpUrl.newBuilder()
                        .build();
                final Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("Authorization",BEARER+token)
                        .url(url);
                final Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);


        return httpClient.build();
    }
    private static GsonConverterFactory buildGsonConverterDTO() {

        final GsonBuilder builder = new GsonBuilder();

        // Adding custom deserializers
        builder.registerTypeAdapter(DTO.class,
                new ConvertableDeserializer<DTO>());
        builder.registerTypeHierarchyAdapter(byte[].class,
                new ByteArrayTypeAdapter());
        final Gson gson = builder.create();

        return GsonConverterFactory.create(gson);
    }
    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(buildGsonConverterDTO())
                .client(createOkHttpClient())

                .build();
    }
}

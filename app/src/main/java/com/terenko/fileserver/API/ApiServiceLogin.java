package com.terenko.fileserver.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
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

public class ApiServiceLogin {


    APILogin api;
    final String URL = "http://192.168.1.120:8081/";

    public ApiServiceLogin() {
        Retrofit retrofit = createRetrofit();
        api = retrofit.create(APILogin.class);
    }

    public APILogin getApi() {
        return api;
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

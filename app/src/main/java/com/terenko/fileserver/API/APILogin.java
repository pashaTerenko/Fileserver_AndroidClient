package com.terenko.fileserver.API;


import com.terenko.fileserver.DTO.AuthenticationRequestDto;
import com.terenko.fileserver.DTO.AuthenticationToken;
import com.terenko.fileserver.DTO.Responce;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APILogin {

    @POST("/auth/newuser")
    Call<Responce> register(@Body AuthenticationRequestDto authenticationRequestDto);

    @POST("/auth/login")
    Call<Responce> login(@Body AuthenticationRequestDto authenticationRequestDto);
}

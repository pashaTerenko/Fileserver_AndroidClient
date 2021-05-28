package com.terenko.fileserver.API;

import com.terenko.fileserver.DTO.Responce;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APICatalog {
    @POST("/catalog/add")
    public Call<Responce> addCatalog(@Query("name")String name,@Query("access") boolean access);
    @POST("/catalog/del/{catalogId}")
    public Call<Responce> delCatalog(@Path("catalogId")String catalogID);
    @GET("/catalog/get/{catalogId}")
    public Call<Responce> getCatalog(@Path("catalogId")String catalogID);
    @GET("/catalog/user")
    public Call<Responce> getUserCatalogs();
    @POST("/catalog/addAccess/{catalogId}")
    public Call<Responce> addAccess(@Path("catalogId")String catalogID ,@Query("addAccessTo")String addAccessTo);
    @POST("/catalog/removeAccess/{catalogId}")
    public Call<Responce> removeAccess(@Path("catalogId")String catalogID ,@Query("removeAccessFrom")String removeAccessFrom);


}

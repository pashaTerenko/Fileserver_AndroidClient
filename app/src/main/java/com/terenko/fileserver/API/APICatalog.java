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
    @POST("/catalog/del/{CatalogID}")
    public Call<Responce> delCatalog(@Path("catalogID")String catalogID);
    @GET("/catalog/get/{CatalogID}")
    public Call<Responce> getCatalog(@Path("catalogID")String catalogID);
    @GET("/catalog/user")
    public Call<Responce> getUserCatalogs();
    @POST("/catalog/addAccess/{CatalogID}")
    public Call<Responce> addAccess(@Path("catalogID")String catalogID ,@Query("addAccessTo")String addAccessTo);
    @POST("/catalog/removeAccess/{CatalogID}")
    public Call<Responce> removeAccess(@Path("catalogID")String catalogID ,@Query("removeAccessFrom")String removeAccessFrom);


}

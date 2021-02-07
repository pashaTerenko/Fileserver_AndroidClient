package com.terenko.fileserver.API;

import com.terenko.fileserver.DTO.Responce;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIFile {
    @Multipart
    @POST("/file/add/{CatalogID}")
    public Call<Responce> addFile(@Part MultipartBody.Part file,@Path("CatalogID")String catalogID);
    @POST("/file/del/{CatalogID}/{fileID}")
    public Call<Responce> delFile(@Path("CatalogID")String catalogID,@Path("fileID")String fileID);
    @GET("/file/download/{CatalogID}/{fileID}")
    public Call<Responce> download(@Path("CatalogID")String catalogID,@Path("fileID")String fileID);
    @GET("/file/getFiles/{CatalogID}")
    public Call<Responce> getFilesFromCatalog(@Path("CatalogID")String catalogID);




}

package com.terenko.fileserver.API;

import com.terenko.fileserver.DTO.Responce;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface APIFile {
    @Multipart
    @POST("/file/add/{catalogId}")
    public Call<Responce> addFile(@Part MultipartBody.Part file,@Path("catalogId")String catalogID);
    @POST("/file/del/{catalogId}/{fileId}")
    public Call<Responce> delFile(@Path("catalogId")String catalogID,@Path("fileId")String fileID);
    @Streaming
    @GET("/file/download/{catalogId}/{fileId}")
    public Call<ResponseBody> download(@Path("catalogId")String catalogID, @Path("fileId")String fileID);
    @GET("/file/getFiles/{catalogId}")
    public Call<Responce> getFilesFromCatalog(@Path("catalogId")String catalogID);




}

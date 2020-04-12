package com.planjut.basiccrudmahasiswa.controller;

import com.planjut.basiccrudmahasiswa.model.FotoMahasiswa;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileService {
    @Multipart
    @POST("api/apiFotoMahasiswa.php")
    Call<FotoMahasiswa> upload(@Part MultipartBody.Part file, @Part("file") RequestBody name);
}

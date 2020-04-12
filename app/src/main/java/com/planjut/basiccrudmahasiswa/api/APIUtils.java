package com.planjut.basiccrudmahasiswa.api;

import com.planjut.basiccrudmahasiswa.controller.FileService;
import com.planjut.basiccrudmahasiswa.controller.RetrofitClient;

public class APIUtils {
    private APIUtils(){

    }

    public static final String API_URL = "http://192.168.43.147/clientserver/";

    public static FileService getFileService(){
        return RetrofitClient.getClient(API_URL).create(FileService.class);
    }
}

package com.planjut.basiccrudmahasiswa.model;

import com.google.gson.annotations.SerializedName;

public class FotoMahasiswa {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}

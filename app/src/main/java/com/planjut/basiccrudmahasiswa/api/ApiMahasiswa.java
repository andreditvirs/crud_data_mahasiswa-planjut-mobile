package com.planjut.basiccrudmahasiswa.api;

public class ApiMahasiswa {
    private static final String BASE_URL = "http://192.168.43.147/clientserver/api/apiMahasiswa.php?apicall=";

    public static final String URL_CREATE = BASE_URL + "create_mahasiswa";
    public static final String URL_READ = BASE_URL + "get_mahasiswa";
    public static final String URL_UPDATE = BASE_URL + "update_mahasiswa";
    public static final String URL_DELETE = BASE_URL + "delete_mahasiswa&id=";
}

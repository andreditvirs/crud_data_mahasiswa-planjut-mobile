package com.planjut.basiccrudmahasiswa.model;

import androidx.annotation.NonNull;

public class Mahasiswa {
    private int id;
    private String nrp;
    private String nama;
    private String alamat;
    private String tmpLahir;
    private String tglLahir;
    private String imageName;

    public Mahasiswa(int id, String nrp, String nama, String alamat, String tmpLahir, String tglLahir, String imageName) {
        this.id = id;
        this.nrp = nrp;
        this.nama = nama;
        this.alamat = alamat;
        this.tmpLahir = tmpLahir;
        this.tglLahir = tglLahir;
        this.imageName = imageName;
    }

    public int getId() {
        return id;
    }

    public String getNrp() { return nrp; }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getTmpLahir() {
        return tmpLahir;
    }

    public String getTglLahir() {
        return tglLahir;
    }

    public String getImageName() {
        return imageName;
    }

    @NonNull
    @Override
    public String toString() {
        return nama;
    }
}

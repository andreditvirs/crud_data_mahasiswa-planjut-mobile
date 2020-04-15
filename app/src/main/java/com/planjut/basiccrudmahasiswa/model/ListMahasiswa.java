package com.planjut.basiccrudmahasiswa.model;

public class ListMahasiswa {
    private String id, nama, nrp, img;

    public ListMahasiswa(){};

    public ListMahasiswa(String id, String nama, String keterangan, String img){
        this.id = id;
        this.nama = nama;
        this.nrp = keterangan;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNrp() {
        return nrp;
    }

    public void setNrp(String nrp) {
        this.nrp = nrp;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}

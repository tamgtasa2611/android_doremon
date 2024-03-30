package com.bkacad.doremon;

//tao class Chapter gom id, ten, url
public class Chapter {
    //thuoc tinh
    private int id;
    private String name;
    private String url;
    //nhap gia tri khi khoi tao
    public Chapter(int id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    //lay thong tin cua chapter
    public int getId() { return id; }
    public String getName() {
        return name;
    }
    public String getUrl() {
        return url;
    }
}

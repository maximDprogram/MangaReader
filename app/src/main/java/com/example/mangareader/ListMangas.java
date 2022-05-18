package com.example.mangareader;

public class ListMangas {

    int id;
    String title, titleOrig, chapter1;

    public ListMangas(int id, String title, String titleOrig, String chapter1) {
        this.id = id;
        this.title = title;
        this.titleOrig = titleOrig;
        this.chapter1 = chapter1;
    }

    public String getChapter1() {
        return chapter1;
    }

    public String getTitleOrig() {
        return titleOrig;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
}

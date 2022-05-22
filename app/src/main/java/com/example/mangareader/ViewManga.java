package com.example.mangareader;

public class ViewManga {

    int id;
    String img, title, titleOrig, description, chapter1;

    public ViewManga(int id, String img, String title, String titleOrig, String description, String chapter1) {
        this.id = id;
        this.img = img;
        this.title = title;
        this.titleOrig = titleOrig;
        this.description = description;
        this.chapter1 = chapter1;
    }

    public String getChapter1() {
        return chapter1;
    }

    public String getTitleOrig() {
        return titleOrig;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public String getTitle() {
        return title;
    }
}

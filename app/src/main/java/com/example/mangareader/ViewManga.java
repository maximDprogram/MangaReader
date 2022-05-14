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

    public void setChapter1(String chapter1) {
        this.chapter1 = chapter1;
    }

    public String getTitleOrig() {
        return titleOrig;
    }

    public void setTitleOrig(String titleOrig) {
        this.titleOrig = titleOrig;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

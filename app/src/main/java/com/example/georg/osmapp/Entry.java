package com.example.georg.osmapp;

/**
 * Created by Georg on 26.05.2017.
 */

public class Entry {

    private int image;
    private String text;

    public Entry(int image, String text){
        setImage(image);
        setText(text);

    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}

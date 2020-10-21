package com.example.asdinvoice;

public class ImagesList {
    private String imageUrl;
    public ImagesList(String imageUrl)
    {
        this.imageUrl=imageUrl;
    }
    public ImagesList()
    {

    }
    public String getImageUrl()
    {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl=imageUrl;
    }
}

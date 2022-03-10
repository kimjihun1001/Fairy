package com.jihoon.fairy.Model;

import android.graphics.drawable.Drawable;

public class Advertisement {

    //logo (광고 이미지)
    private String logo;
    public String getLogo() {
        return this.logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }

    //title (이름)
    private String title;
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    //description (설명)
    private String description;
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}

package com.shhb.supermoon.panda.model;

/**
 * Created by superMoon on 2017/7/31.
 */
public class BannerInfo {
    private String name;
    private String avatar;

    public BannerInfo() {
    }

    public BannerInfo(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

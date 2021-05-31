package com.example.kickboardguard.Setting;

public class ListViewItem {
    private String name ;
    private String email ;
    private String phone ;
    private float Myload;

    public void setname(String name) {
        this.name = name ;
    }
    public void setemail(String email) {
        this.email = email ;
    }
    public void setphone(String phone) {
        this.phone = phone ;
    }

    public void setMyload(float myload) {
        Myload = myload;
    }

    public String getname() {
        return this.name ;
    }
    public String getemail() {
        return this.email ;
    }
    public String getphone() {
        return this.phone ;
    }

    public float getMyload() {
        return Myload;
    }
}

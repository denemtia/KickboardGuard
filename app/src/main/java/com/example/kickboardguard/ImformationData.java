package com.example.kickboardguard;

// 값들을 다른 엑티비티에 이동할수 있게 하는 통로
public class ImformationData {
    private float distance;
    private String name;
    private String phone;
    private String email;
    private onGpsServiceUpdate onGpsServiceUpdate;

    public ImformationData() {
        distance = 0;
        name = null;
        phone = null;
        email = null;
    }
    public interface onGpsServiceUpdate{
        public void update();
    }

    public void setOnGpsServiceUpdate(onGpsServiceUpdate onGpsServiceUpdate){
        this.onGpsServiceUpdate = onGpsServiceUpdate;
    }

    public void update(){
        onGpsServiceUpdate.update();
    }

    public ImformationData(onGpsServiceUpdate onGpsServiceUpdate){
        this();
        setOnGpsServiceUpdate(onGpsServiceUpdate);
    }

    public float returnDistance(){
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public float getDistance() {
        return distance;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}

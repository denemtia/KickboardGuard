package com.example.kickboardguard;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

// 값들을 다른 엑티비티에 이동할수 있게 하는 통로
public class ImformationData {
    private float distance;
    private String name;
    private String email;
    private int id;

    public ImformationData(){
        distance = 0;
        name = null;
        email = null;
        id = 0;
    }

    public ImformationData(int id, String name, String email, float distance) {
      this.id = id;
      this.name = name;
      this.email = email;
      this.distance = distance;
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

    public float getDistance() {
        return distance;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("id", id);
        result.put("name", name);
        result.put("email",email);
        result.put("distance",distance);
        return result;
    }
}

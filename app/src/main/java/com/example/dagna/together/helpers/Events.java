package com.example.dagna.together.helpers;

/**
 * Created by Dagna on 18/04/2016.
 */
public class Events {

    private String name, description, city,id;

    public Events(String id,String name, String description){
       this.setId(id);
        this.setName(name);
        this.setDescription(description);
        //this.setCity(city);
    }

    public Events(String id,String name, String description, String city){
        this.setId(id);
        this.setName(name);
        this.setDescription(description);
        this.setCity(city);
    }

    public Events(String name, String description){
        this.setName(name);
        this.setDescription(description);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

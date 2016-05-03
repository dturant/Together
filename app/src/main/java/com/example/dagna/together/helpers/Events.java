package com.example.dagna.together.helpers;

/**
 * Created by Dagna on 18/04/2016.
 */
public class Events {

    private String name, description, city,id;
    private Integer imageId;

    public Events(String id,String name, String description, Integer imageId){
       this.setId(id);
        this.setName(name);
        this.setDescription(description);
        this.setImageId(imageId);
        //this.setCity(city);
    }

    public Events(String id,String name, String description, String city, Integer imageId){
        this.setId(id);
        this.setName(name);
        this.setDescription(description);
        this.setCity(city);
        this.setImageId(imageId);
    }

    public Events(String name, String description, Integer imageId){
        this.setName(name);
        this.setDescription(description);
        this.setImageId(imageId);
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

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
}

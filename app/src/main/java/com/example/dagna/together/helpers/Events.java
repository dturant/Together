package com.example.dagna.together.helpers;

/**
 * Created by Dagna on 18/04/2016.
 */
public class Events {

    private String name, description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

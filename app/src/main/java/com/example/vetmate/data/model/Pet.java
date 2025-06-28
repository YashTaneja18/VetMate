package com.example.vetmate.data.model;

import com.example.vetmate.R;

import java.io.Serializable;

public class Pet implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    private String id;
    private String name;
    private String species;
    private String breed;
    private String gender;
    private int age;
    private float height;
    private float weight;
    private String photoUri;

    public int getLocalImageResId() {
        return localImageResId;
    }

    public void setLocalImageResId(int localImageResId) {
        this.localImageResId = localImageResId;
    }

    private int localImageResId;

    // Required empty constructor for Firestore
    public Pet() {}

    public Pet(String id, String name, String species, String breed, String gender,
               int age, float height, float weight, String photoUri) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.photoUri = photoUri;
        this.localImageResId = R.drawable.placeholder_dog;
    }
}

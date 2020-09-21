package com.example.sadaksevaapp;

public class Const_Pothole_Detail {

    public Const_Pothole_Detail(String location, String tolocation, String severity, String description, String namee, String urlimage, double latitude, double longitude,String email) {
        this.location = location;
        this.urlimage=urlimage;
        this.tolocation = tolocation;
        this.severity = severity;
        this.description = description;
        this.namee = namee;
        this.latitude=latitude;
        this.longitude=longitude;
        this.email=email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTolocation() {
        return tolocation;
    }

    public void setTolocation(String tolocation) {
        this.tolocation = tolocation;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNamee() {
        return namee;
    }

    public void setNamee(String namee) {
        this.namee = namee;
    }

    public String location;
    public String tolocation;
    public String severity;
    public String description;
    public String namee;
    public double latitude;
    public double longitude;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String email;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUrlimage() {
        return urlimage;
    }

    public void setUrlimage(String urlimage) {
        this.urlimage = urlimage;
    }

    public String urlimage;

    public Const_Pothole_Detail() {

    }

}

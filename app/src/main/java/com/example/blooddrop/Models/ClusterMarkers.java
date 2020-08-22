package com.example.blooddrop.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarkers implements ClusterItem {
    private LatLng position;
    private String title;
    private String snippet;
    private int iconId;
    private VehicleLocations user;

    public ClusterMarkers(LatLng position, String title, String snippet, int iconId, VehicleLocations user) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconId = iconId;
        this.user = user;
    }
    public ClusterMarkers(){

    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public VehicleLocations getUser() {
        return user;
    }

    public void setUser(VehicleLocations user) {
        this.user = user;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
}

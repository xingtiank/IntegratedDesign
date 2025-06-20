package com.work.integratedDesign.pojo;

public enum Depot {
    DEPOT_A(104.092048, 30.688573),
    DEPOT_B(103.931658, 30.688573),
    DEPOT_C(104.100221, 30.688573),
    DEPOT_D(104.100544, 30.688573),
    DEPOT_E(104.100131, 30.688573);

    private final double longitude; // 经度
    private final double latitude;  // 纬度

    Depot(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

}

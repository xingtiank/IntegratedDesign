package com.work.integratedDesign.pojo;

public class VehicleModel {
    private String type;
    private double capacity;
    private double area;

    public VehicleModel(String type, double capacity, double area) {
        this.type = type;
        this.capacity = capacity;
        this.area = area;
    }

    public String getType() {
        return type;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getArea() {
        return area;
    }
}
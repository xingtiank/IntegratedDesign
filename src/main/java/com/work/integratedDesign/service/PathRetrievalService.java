package com.work.integratedDesign.service;

public interface PathRetrievalService {
    double[][] getPolyline(double originLatitude, double originLongitude, double destinationLatitude, double destinationLongitude);
    double[][] getPolyline(double originLatitude, double originLongitude, double destinationLatitude, double destinationLongitude,  String waypoints);
}

package com.work.integratedDesign.Service;

public interface RestTemplate {
  double[][] getPolyline(double originLatitude,double originLongitude,double destinationLatitude,double destinationLongitude);

  double[][] decodePolyline(String polyline);
}

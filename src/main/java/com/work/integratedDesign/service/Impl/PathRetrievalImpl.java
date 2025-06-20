package com.work.integratedDesign.service.Impl;


import com.work.integratedDesign.pojo.RouteResponse;
import com.work.integratedDesign.service.PathRetrievalService;
import com.work.integratedDesign.service.WebClientService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//获取路径Polyline
@Service
public class PathRetrievalImpl implements PathRetrievalService {
    @Resource
    WebClientService webClientService;
    // 获取路径,没有途径点
    public  double[][] getPolyline(double originLongitude, double originLatitude, double destinationLongitude, double destinationLatitude) {
        try {
            Mono<RouteResponse> routeMono = webClientService.getDistanceData(
                    originLongitude + "," + originLatitude,
                    destinationLongitude + "," + destinationLatitude
            );

            RouteResponse routeResponse = routeMono.block(); // 使用 block() 获取同步结果
            if (routeResponse != null && routeResponse.getRoute() != null) {
                List<double[]> allCoordinates = new ArrayList<>();
                for (RouteResponse.RouteData.Path path : routeResponse.getRoute().getPaths()) {
                    for (RouteResponse.RouteData.Path.Step step : path.getSteps()) {
                        String polyline = step.getPolyline();
                        double[][] coordinates = decodePolyline(polyline);
                        allCoordinates.addAll(Arrays.asList(coordinates));
                    }
                }
                return allCoordinates.toArray(new double[allCoordinates.size()][]);
            } else {
                System.out.println("RouteResponse is null or does not contain valid data.");
            }
        } catch ( Exception e) {
            System.out.println("Error occurred while processing the request: " + e.getMessage());
        }
        return null;
    }
    public  double[][] getPolyline(double originLongitude, double originLatitude, double destinationLongitude, double destinationLatitude,  String waypoints) {
        try {
            Mono<RouteResponse> routeMono = webClientService.getDistanceData(
                    originLongitude + "," + originLatitude,
                    destinationLongitude + "," + destinationLatitude,
                    waypoints
            );

            RouteResponse routeResponse = routeMono.block(); // 使用 block() 获取同步结果
            if (routeResponse != null && routeResponse.getRoute() != null) {
                List<double[]> allCoordinates = new ArrayList<>();
                for (RouteResponse.RouteData.Path path : routeResponse.getRoute().getPaths()) {
                    for (RouteResponse.RouteData.Path.Step step : path.getSteps()) {
                        String polyline = step.getPolyline();
                        double[][] coordinates = decodePolyline(polyline);
                        allCoordinates.addAll(Arrays.asList(coordinates));
                    }
                }
                return allCoordinates.toArray(new double[allCoordinates.size()][]);
            } else {
                System.out.println("RouteResponse is null or does not contain valid data.");
            }
        } catch ( Exception e) {
            System.out.println("Error occurred while processing the request: " + e.getMessage());
        }
        return null;
    }

    public static double[][] decodePolyline(String polyline) {
        String[] points = polyline.split(";");
        List<double[]> coordinates = new ArrayList<>();

        for (String point : points) {
            String[] latLng = point.split(",");
            if (latLng.length == 2) {
                double latitude = Double.parseDouble(latLng[0]);
                double longitude = Double.parseDouble(latLng[1]);
                coordinates.add(new double[]{latitude, longitude});
            }
        }

        return coordinates.toArray(new double[coordinates.size()][]);
    }

}

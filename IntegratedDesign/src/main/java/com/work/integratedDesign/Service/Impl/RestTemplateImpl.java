package com.work.integratedDesign.Service.Impl;


import com.work.integratedDesign.Service.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//获取路径
public class RestTemplateImpl implements RestTemplate {
    public double[][] getPolyline(double originLatitude,double originLongitude,double destinationLatitude,double destinationLongitude) {
        try {
            String key="b37ebc0a0eb576a1cc6f432b2a5439f7";
            URL url = new URL("https://restapi.amap.com/v3/direction/driving?origin=" + originLatitude + "," + originLongitude + "&destination=" + destinationLatitude + "," + destinationLongitude + "&key=" + key);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            List<double[]> allCoordinates = new ArrayList<>();
            // 解析JSON
            JSONObject jsonObject = new JSONObject(content.toString());
            JSONObject routeObject = jsonObject.getJSONObject("route");
            JSONArray pathsObject = routeObject.getJSONArray("paths");
            for (int i = 0; i < pathsObject.length(); i++) {
                JSONObject path = pathsObject.getJSONObject(i);
                JSONArray stepsObject = path.getJSONArray("steps");
                for(int j = 0; j < stepsObject.length(); j++){
                    JSONObject step = stepsObject.getJSONObject(j);
                    String polyline = step.getString("polyline");
                    double[][] coordinates = decodePolyline(polyline);
                    allCoordinates.addAll(Arrays.asList(coordinates));
                }
            }
            return allCoordinates.toArray(new double[allCoordinates.size()][]);
        } catch (Exception e) {
            System.out.println("Error occurred while processing the request:"+e.getMessage());
        }
        return null;
    }

    public double[][] decodePolyline(String polyline) {
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

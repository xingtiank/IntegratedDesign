package com.work.integratedDesign.pojo;

public class TransportTask {
    private String source;
    private String destination;
    private double totalDistance;
    private double totalTime; // 总时间（分钟）
    private double elapsedTime; // 已执行时间（分钟）

    public TransportTask(String source, String destination, double totalDistance) {
        this.source = source;
        this.destination = destination;
        this.totalDistance = totalDistance;
        this.totalTime = totalDistance / 60; // 假设速度为60km/h
        this.elapsedTime = 0;
    }

    public boolean isCompleted() {
        return elapsedTime >= totalTime;
    }

    public void updateElapsedTime(double timeStep) {
        elapsedTime += timeStep;
    }

    // Getters and setters
    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }
}

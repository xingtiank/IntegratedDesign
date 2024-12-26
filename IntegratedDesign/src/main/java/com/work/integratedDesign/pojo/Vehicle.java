package com.work.integratedDesign.pojo;



public class Vehicle {
    private String id;
    private String currentState;
    private TransportTask currentTask;
    private StateTransitionTable transitionTable;
    private String vehicleType;
    private double capacity;
    private double area;

    public Vehicle(String id, StateTransitionTable transitionTable, String vehicleType, double capacity, double area) {
        this.id = id;
        this.currentState = "idle";
        this.currentTask = null;
        this.transitionTable = transitionTable;
        this.vehicleType = vehicleType;
        this.capacity = capacity;
        this.area = area;
    }

    public void updateState(double timeStep) {
        if (currentTask != null) {
            currentTask.updateElapsedTime(timeStep);
            if (currentTask.isCompleted()) {
                currentState = transitionTable.getNextState(currentState);
                currentTask = null;
            }
        } else {
            currentState = transitionTable.getNextState(currentState);
        }
    }

    public void assignTask(TransportTask task) {
        this.currentTask = task;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getCurrentState() {
        return currentState;
    }

    public TransportTask getCurrentTask() {
        return currentTask;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getArea() {
        return area;
    }
}
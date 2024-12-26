package com.work.integratedDesign.Service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.work.integratedDesign.pojo.*;
import com.work.integratedDesign.pojo.Goods.Goods;


public class Simulation {
    private List<Vehicle> vehicles;
    private StateTransitionTable transitionTable;
    private List<TransportTask> tasks;
    private Random random;
    // 预设值
    private static final VehicleModel[] VEHICLE_MODELS = {
        new VehicleModel("2.5吨 货车（厢式/板车）", 3000.0, 12.0), // 单位：kg, m²
        new VehicleModel("35吨 货车（厢式/板车）", 35000.0, 110.0),
        new VehicleModel("3.5吨 货车（厢式/板车）", 5000.0, 30.0),
        new VehicleModel("25吨 货车（厢式/板车）", 25000.0, 60.0),
        new VehicleModel("28吨 货车（厢式/板车）", 28000.0, 80.0),
        new VehicleModel("35吨 货车（板车）", 35000.0, 80.0),
        new VehicleModel("8吨 货车（冷藏车）", 10000.0, 45.0),
        new VehicleModel("40吨 货车（板车）", 40000.0, 96.0),
        new VehicleModel("8吨 货车（行李托运） 集装箱", 25000.0, 85.0),
    };

    public Simulation() {
        transitionTable = new StateTransitionTable();
        vehicles = new ArrayList<>();
        tasks = new ArrayList<>();
        random = new Random();

         // 初始化100台车辆
         for (int i = 0; i < 100; i++) {
            VehicleModel model = VEHICLE_MODELS[random.nextInt(VEHICLE_MODELS.length)];
            vehicles.add(new Vehicle("Vehicle" + i, transitionTable, model.getType(), model.getCapacity(), model.getArea()));
        }
    }

    public void runSimulation() {
        while (true) {
            // 生成新的运输任务
            generateTasks();
    
            // 更新每辆车的状态
            for (Vehicle vehicle : vehicles) {
                vehicle.updateState(5); // 每次循环代表5分钟
    
                // 如果车辆空闲且有任务，分配任务
                if (vehicle.getCurrentState().equals("idle") && !tasks.isEmpty()) {
                    TransportTask task = tasks.get(0);
                    Goods goods = task.getGoods();
                    if (vehicle.getCapacity() >= goods.getWeight() && vehicle.getArea() >= goods.getVolume()) {
                        tasks.remove(0);
                        vehicle.assignTask(task);
                    }
                }
            }
    
            // 检查是否有任务未分配
            if (!tasks.isEmpty()) {
                TransportTask task = tasks.get(0);
                Goods goods = task.getGoods();
                boolean vehicleFound = false;
    
                // 查找是否有合适的车辆
                for (Vehicle vehicle : vehicles) {
                    if (vehicle.getCurrentState().equals("idle") && vehicle.getCapacity() >= goods.getWeight() && vehicle.getArea() >= goods.getVolume()) {
                        tasks.remove(0);
                        vehicle.assignTask(task);
                        vehicleFound = true;
                        break;
                    }
                }
    
                // 如果没有找到合适的车辆，生成一辆新的车辆
                if (!vehicleFound) {
                    VehicleModel model = findSuitableVehicleModel(goods);
                    vehicles.add(new Vehicle("Vehicle" + vehicles.size(), transitionTable, model.getType(), model.getCapacity(), model.getArea()));
                }
            }
    
            // 在地图上显示车辆状态
            displayVehiclesOnMap();
    
            // 暂停5秒（模拟5分钟）
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private VehicleModel findSuitableVehicleModel(Goods goods) {
        for (VehicleModel model : VEHICLE_MODELS) {
            if (model.getCapacity() >= goods.getWeight() && model.getArea() >= goods.getVolume()) {
                return model;
            }
        }
        // 如果没有找到合适的型号，返回默认的第一个型号
        return VEHICLE_MODELS[0];
    }
    
    private void generateTasks() {
        // 生成新的运输任务
        GenerateImpl generate = new GenerateImpl();
        Goods goods = generate.generateGoods();
        //随机生成一个3-40吨，10-80平方米的货物，货物还包含起点和终点的经纬度
        // 计算距离
        double totalDistance = PositionUtil.getDistance4(
            goods.getOrigin().getLongitude(), goods.getOrigin().getLatitude(),
            goods.getDestination().getLongitude(), goods.getDestination().getLatitude()
        );
    
        // 新建运输任务
        tasks.add(new TransportTask(
            "Source: (" + goods.getOrigin().getLongitude() + ", " + goods.getOrigin().getLatitude() + ")",
            "Destination: (" + goods.getDestination().getLongitude() + ", " + goods.getDestination().getLatitude() + ")",
            totalDistance
        ));
    }

    private void displayVehiclesOnMap() {
        /*在地图上显示车辆状态(假设)
       for (Vehicle vehicle : vehicles) {
            System.out.println("Vehicle " + vehicle.getId() + " - State: " + vehicle.getCurrentState());
            System.out.println("  Type: " + vehicle.getVehicleType());
            System.out.println("  Capacity: " + vehicle.getCapacity() + " kg");
            System.out.println("  Area: " + vehicle.getArea() + " m²");
            if (vehicle.getCurrentTask() != null) {
                System.out.println("  Task: " + vehicle.getCurrentTask().getSource() + " to " + vehicle.getCurrentTask().getDestination());
                System.out.println("  Elapsed Time: " + vehicle.getCurrentTask().getElapsedTime() + " / " + vehicle.getCurrentTask().getTotalTime());
            }
        }*/
    }

//    public static void main(String[] args) {
//        Simulation simulation = new Simulation();
//        simulation.runSimulation();
//    }
}
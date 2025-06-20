package com.work.integratedDesign.service.Impl;


import com.graphhopper.jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.work.integratedDesign.config.VehicleTypeConfig;
import com.work.integratedDesign.pojo.OnePath;
import com.work.integratedDesign.pojo.Task;
import com.work.integratedDesign.pojo.DetailedVehicle;
import com.work.integratedDesign.service.PathRetrievalService;
import com.work.integratedDesign.service.VRPService;
import jakarta.annotation.Resource;
import com.graphhopper.jsprit.core.problem.job.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@org.springframework.stereotype.Service
public class VRPServiceImpl implements VRPService {
    @Resource
    private VehicleTypeConfig  vehicleTypeConfig;
    @Resource
    private PathRetrievalService pathRetrievalService;

//    public List<OnePath> VRPWithOneVehicleType() {
//        List<OnePath> paths=new ArrayList<>();
//        VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
//        vehicleBuilder.setStartLocation(Location.newInstance(104.092048, 30.688573));
//        vehicleBuilder.setType(vehicleTypeConfig.VehicleType_1());
//        VehicleImpl vehicle = vehicleBuilder.build();
//
//        //创建服务点，包括id，容量，坐标
//        Service service1 = com.graphhopper.jsprit.core.problem.job.Service.Builder.newInstance("1").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(103.931658, 30.749167)).build();
//        Service service2 = com.graphhopper.jsprit.core.problem.job.Service.Builder.newInstance("2").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(104.100221, 30.675709)).build();
//
//        Service service3 = com.graphhopper.jsprit.core.problem.job.Service.Builder.newInstance("3").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(104.083766, 30.630647)).build();
//        Service service4 = com.graphhopper.jsprit.core.problem.job.Service.Builder.newInstance("4").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(104.067565, 30.640916)).build();
//
//        //创建问题，添加车辆和服务点
//        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
//        vrpBuilder.addVehicle(vehicle);
//        vrpBuilder.addJob(service1).addJob(service2).addJob(service3).addJob(service4);
//
//        VehicleRoutingProblem problem = vrpBuilder.build();
//
//        //创建算法
//        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
//
//        //运行算法
//        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
//        //绘制优化曲线
//        algorithm.addListener(new AlgorithmSearchProgressChartListener("output/progress.png"));
//        //输出结果
//        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
//        //高德API会有同一路径途径点仅能有16个的限制
//        bestSolution.getRoutes().forEach(route -> {
//            OnePath onePath = new OnePath();
//            StringBuilder waypoints = new StringBuilder();
//            route.getTourActivities().getActivities().forEach(activity -> {
//                waypoints.append(activity.getLocation().getCoordinate().getX()).append(",").append(activity.getLocation().getCoordinate().getY()).append(";");
//            });
//            onePath.setVehicleTypeId(route.getVehicle().getType().getTypeId());
//            onePath.setPolyline(pathRetrievalService.getPolyline(route.getStart().getLocation().getCoordinate().getX(), route.getStart().getLocation().getCoordinate().getY()
//                    , route.getEnd().getLocation().getCoordinate().getX(), route.getEnd().getLocation().getCoordinate().getY()
//                    , waypoints.toString()));
//            paths.add(onePath);
//        });
//        return paths;
//    }
    public List<OnePath> VRP(List<Task> tasks, List<DetailedVehicle> detailedVehicles) {
        List<OnePath> paths=new ArrayList<>();
        int count=1;
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        //创建车辆
        for (DetailedVehicle detailedVehicle : detailedVehicles) {
            //添加车辆
            int i=detailedVehicle.getNumberOfVehicle();
            if(i<=0){
                vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.INFINITE);
            }else {
                vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
            }
            do{
                VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle_"+count++);
                vehicleBuilder.setStartLocation(Location.newInstance(detailedVehicle.getDepotLongitude(), detailedVehicle.getDepotLatitude()))
                        .setType(vehicleTypeConfig.vehicleTypeMap().get(detailedVehicle.getVehicleTypeId()));
                if(!detailedVehicle.getIsReturnToDepot()){
                    vehicleBuilder.setReturnToDepot(false);
                }
                VehicleImpl vehicle = vehicleBuilder.build();
                vrpBuilder.addVehicle(vehicle);
                i--;
            }while (i >0);
        }
        count=1;
        //创建任务
        for (Task task : tasks) {
            if (task.getTaskType().equals("service")){//服务任务,不需要目的地
                Service.Builder service = Service.Builder.newInstance("task_"+count++)
                        .addSizeDimension(WEIGHT_INDEX, task.getWeight())
                        .addSizeDimension(VOLUME_INDEX, task.getVolume())
                        .setLocation(Location.newInstance(task.getPickUpLongitude(), task.getPickUpLatitude()));
                if(task.getWithTW()){
                    service.addTimeWindow(task.getPickUpTimeWindowEnd(), task.getPickUpTimeWindowStart());
                }
                if (!(task.getPriority()==0)){
                    service.setPriority(task.getPriority());
                }
               vrpBuilder.addJob(service.build());
            }else if (task.getTaskType().equals("shipment")){//配送任务，需要目的地
                Shipment.Builder  shipment = Shipment.Builder.newInstance("task_"+count++)
                        .addSizeDimension(WEIGHT_INDEX, task.getWeight())
                        .addSizeDimension(VOLUME_INDEX, task.getVolume())
                        .setPickupLocation(Location.newInstance(task.getPickUpLongitude(), task.getPickUpLatitude()))
                        .setDeliveryLocation(Location.newInstance(task.getDeliveryLongitude(), task.getDeliveryLatitude()));
                if (task.getWithTW()){
                    shipment.addPickupTimeWindow(task.getPickUpTimeWindowStart(), task.getPickUpTimeWindowEnd());
                    shipment.addDeliveryTimeWindow(task.getDeliveryTimeWindowStart(), task.getDeliveryTimeWindowEnd());
                }
                if (!(task.getPriority()==0)){
                    shipment.setPriority(task.getPriority());
                }
                vrpBuilder.addJob(shipment.build());
            }
        }
        VehicleRoutingProblem problem = vrpBuilder.build();
        //创建算法
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);

        //运行算法
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        //绘制优化曲线
        algorithm.addListener(new AlgorithmSearchProgressChartListener("output/progress.png"));
        //输出结果
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
        SolutionPrinter.print(problem, bestSolution,  SolutionPrinter.Print.VERBOSE);
        //绘制路线
        new Plotter(problem,bestSolution).plot("output/plot.png","方案");
        //高德API会有同一路径途径点仅能有16个的限制
        bestSolution.getRoutes().forEach(route -> {
            OnePath onePath = new OnePath();
            StringBuilder waypoints = new StringBuilder();
            route.getTourActivities().getActivities().forEach(activity -> {
                waypoints.append(activity.getLocation().getCoordinate().getX()).append(",").append(activity.getLocation().getCoordinate().getY()).append(";");
            });
            onePath.setVehicleTypeId(route.getVehicle().getType().getTypeId());
            onePath.setPolyline(pathRetrievalService.getPolyline(route.getStart().getLocation().getCoordinate().getX(), route.getStart().getLocation().getCoordinate().getY()
                    , route.getEnd().getLocation().getCoordinate().getX(), route.getEnd().getLocation().getCoordinate().getY()
                    , waypoints.toString()));
            paths.add(onePath);
        });
        return paths;
    }
}

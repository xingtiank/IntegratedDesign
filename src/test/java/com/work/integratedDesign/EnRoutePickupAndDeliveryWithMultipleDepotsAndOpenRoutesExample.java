package com.work.integratedDesign;


import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer.Label;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.io.problem.VrpXMLWriter;
import com.graphhopper.jsprit.util.Examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * 带多仓库、开放路径和沿途取送货（En-Route Pickup and Delivery） 的车辆路径问题
 * 多个仓库/出发点（Multiple Depots）
 * 开放式路径（Open Routes）：即车辆无需返回起点
 * 沿途取货与送货（Pickup and Delivery）
 */
public class EnRoutePickupAndDeliveryWithMultipleDepotsAndOpenRoutesExample {

    public static void main(String[] args) {
        /**
         * 创建输出目录
         * 创建用于保存结果文件（如图像、XML 等）的输出目录。
         */
        Examples.createOutputFolder();


        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(0, 2);
        vehicleTypeBuilder.setCostPerDistance(1.0);
        VehicleType vehicleType = vehicleTypeBuilder.build();

        /*
         * define two vehicles and their start-locations
         *
         * the first two do need to return to depot
         */
        VehicleImpl.Builder vehicleBuilder1 = VehicleImpl.Builder.newInstance("vehicles@[10,10]");
        vehicleBuilder1.setStartLocation(loc(Coordinate.newInstance(10, 10))).setReturnToDepot(false);
        vehicleBuilder1.setType(vehicleType);
        VehicleImpl vehicle1 = vehicleBuilder1.build();

        VehicleImpl.Builder vehicleBuilder2 = VehicleImpl.Builder.newInstance("vehicles@[30,30]");
        vehicleBuilder2.setStartLocation(loc(Coordinate.newInstance(30, 30))).setReturnToDepot(false);
        vehicleBuilder2.setType(vehicleType);
        VehicleImpl vehicle2 = vehicleBuilder2.build();

        VehicleImpl.Builder vehicleBuilder3 = VehicleImpl.Builder.newInstance("vehicles@[10,30]");
        vehicleBuilder3.setStartLocation(loc(Coordinate.newInstance(10, 30)));
        vehicleBuilder3.setType(vehicleType);
        VehicleImpl vehicle3 = vehicleBuilder3.build();

        VehicleImpl. Builder vehicleBuilder4 = VehicleImpl.Builder.newInstance("vehicles@[30,10]");
        vehicleBuilder4.setStartLocation(loc(Coordinate.newInstance(30, 10)));
        vehicleBuilder4.setType(vehicleType);
        VehicleImpl vehicle4 = vehicleBuilder4.build();

        /**
         * 每个 Shipment 对象代表一个“取货→送货”的任务。
         * 设置了 16 个任务，每个任务都需要从一个坐标点取货，送到另一个坐标点。
         * 每个任务占用容量维度 0 上的 1 单位资源。
         */
        Shipment shipment1 = Shipment.Builder.newInstance("1").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(5, 7))).setDeliveryLocation(loc(Coordinate.newInstance(6, 9))).build();
        Shipment shipment2 = Shipment.Builder.newInstance("2").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(5, 13))).setDeliveryLocation(loc(Coordinate.newInstance(6, 11))).build();

        Shipment shipment3 = Shipment.Builder.newInstance("3").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(15, 7))).setDeliveryLocation(loc(Coordinate.newInstance(14, 9))).build();
        Shipment shipment4 = Shipment.Builder.newInstance("4").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(15, 13))).setDeliveryLocation(loc(Coordinate.newInstance(14, 11))).build();

        Shipment shipment5 = Shipment.Builder.newInstance("5").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(25, 27))).setDeliveryLocation(loc(Coordinate.newInstance(26, 29))).build();
        Shipment shipment6 = Shipment.Builder.newInstance("6").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(25, 33))).setDeliveryLocation(loc(Coordinate.newInstance(26, 31))).build();

        Shipment shipment7 = Shipment.Builder.newInstance("7").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(35, 27))).setDeliveryLocation(loc(Coordinate.newInstance(34, 29))).build();
        Shipment shipment8 = Shipment.Builder.newInstance("8").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(35, 33))).setDeliveryLocation(loc(Coordinate.newInstance(34, 31))).build();

        Shipment shipment9 = Shipment.Builder.newInstance("9").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(5, 27))).setDeliveryLocation(loc(Coordinate.newInstance(6, 29))).build();
        Shipment shipment10 = Shipment.Builder.newInstance("10").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(5, 33))).setDeliveryLocation(loc(Coordinate.newInstance(6, 31))).build();

        Shipment shipment11 = Shipment.Builder.newInstance("11").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(15, 27))).setDeliveryLocation(loc(Coordinate.newInstance(14, 29))).build();
        Shipment shipment12 = Shipment.Builder.newInstance("12").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(15, 33))).setDeliveryLocation(loc(Coordinate.newInstance(14, 31))).build();

        Shipment shipment13 = Shipment.Builder.newInstance("13").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(25, 7))).setDeliveryLocation(loc(Coordinate.newInstance(26, 9))).build();
        Shipment shipment14 = Shipment.Builder.newInstance("14").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(25, 13))).setDeliveryLocation(loc(Coordinate.newInstance(26, 11))).build();

        Shipment shipment15 = Shipment.Builder.newInstance("15").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(35, 7))).setDeliveryLocation(loc(Coordinate.newInstance(34, 9))).build();
        Shipment shipment16 = Shipment.Builder.newInstance("16").addSizeDimension(0, 1).setPickupLocation(loc(Coordinate.newInstance(35, 13))).setDeliveryLocation(loc(Coordinate.newInstance(34, 11))).build();


        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle1).addVehicle(vehicle2).addVehicle(vehicle3).addVehicle(vehicle4);
        vrpBuilder.addJob(shipment1).addJob(shipment2).addJob(shipment3).addJob(shipment4);
        vrpBuilder.addJob(shipment5).addJob(shipment6).addJob(shipment7).addJob(shipment8);
        vrpBuilder.addJob(shipment9).addJob(shipment10).addJob(shipment11).addJob(shipment12);
        vrpBuilder.addJob(shipment13).addJob(shipment14).addJob(shipment15).addJob(shipment16);

        //设置车队规模为有限（FleetSize.FINITE），意味着只能使用现有车辆，不能新增。
        vrpBuilder.setFleetSize(FleetSize.FINITE);
        VehicleRoutingProblem problem = vrpBuilder.build();


        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
//		algorithm.setMaxIterations(30000);

        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();


        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        new VrpXMLWriter(problem, solutions).write("output/shipment-problem-with-solution.xml");


        SolutionPrinter.print(bestSolution);

        /*
         * 绘制原始问题地图（无路径）
         */
        Plotter problemPlotter = new Plotter(problem);
        problemPlotter.plotShipments(true);
        problemPlotter.plot("output/enRoutePickupAndDeliveryWithMultipleLocationsExample_problem.png", "en-route pickup and delivery");

        /*
         * 绘制包含最优路径的地图
         */
//        Plotter solutionPlotter = new Plotter(problem, Arrays.asList(Solutions.bestOf(solutions).getRoutes().iterator().next()));
//        solutionPlotter.plotShipments(true);
//        solutionPlotter.plot("output/enRoutePickupAndDeliveryWithMultipleLocationsExample_solution.png", "en-route pickup and delivery");
        // 获取所有路径
        List<VehicleRoute> allRoutes = new ArrayList<>();
        allRoutes.addAll(bestSolution.getRoutes());

        // 创建 Plotter 并传入所有路径
        Plotter solutionPlotter = new Plotter(problem, allRoutes);
        solutionPlotter.plotShipments(true);
        solutionPlotter.plot("output/enRoutePickupAndDeliveryWithMultipleLocationsExample_solution_all_routes.png", "All Routes - en-route pickup and delivery");


        new GraphStreamViewer(problem, Solutions.bestOf(solutions)).labelWith(Label.ACTIVITY).setRenderDelay(100).setRenderShipments(true).display();

    }

    private static Location loc(Coordinate coordinate) {
        return Location.Builder.newInstance().setCoordinate(coordinate).build();
    }

}

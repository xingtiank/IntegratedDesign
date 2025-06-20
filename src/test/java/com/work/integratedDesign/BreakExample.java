package com.work.integratedDesign;

import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Break;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
//import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;

import java.util.Collection;

/**
 * 这段代码是一个使用 jsprit（一个用于解决车辆路径问题的 Java 库）编写的示例程序
 * 演示了如何为车辆设置休息时间（Break），并求解包含休息约束的车辆路径问题。
 */

public class BreakExample {


    public static void main(String[] args) {

        //获取车辆类型构建器，并构建一个类型，其类型ID为“vehicleType”，索引为 0（通常表示货物重量），最大容量为 2，设置单位等待成本为 1.0。
        final int WEIGHT_INDEX = 0;
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType")
                .addCapacityDimension(WEIGHT_INDEX, 2).setCostPerWaitingTime(1.0);
        VehicleType vehicleType = vehicleTypeBuilder.build();


        VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("v1");
        vehicleBuilder.setStartLocation(Location.newInstance(10, 10));
        //创建一个名为 "myFirstBreak" 的休息任务，时间窗口为 [10,15]，即只能在这个时间段内开始休息，休息时长为 100 单位时间。
        Break myFirstBreak = Break.Builder.newInstance("myFirstBreak")
                .setTimeWindow(TimeWindow.newInstance(10, 15)).setServiceTime(100).build();
        vehicleBuilder.setBreak(myFirstBreak);
        vehicleBuilder.setType(vehicleType);
        VehicleImpl vehicle = vehicleBuilder.build();

        //同样，创建一个名为 "mySecondBreak" 的休息任务
        VehicleImpl v2 = VehicleImpl.Builder.newInstance("v2").setStartLocation(Location.newInstance(0, 10)).setType(vehicleType)
                .setBreak(Break.Builder.newInstance("mySecondBreak").setTimeWindow(TimeWindow.newInstance(5, 10)).setServiceTime(10).build()).build();

        //创建四个服务任务，并设置其位置和容量。
        Service service1 = Service.Builder.newInstance("1").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(5, 7)).build();
        Service service2 = Service.Builder.newInstance("2").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(5, 13)).build();

        Service service3 = Service.Builder.newInstance("3").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(15, 7)).build();
        Service service4 = Service.Builder.newInstance("4").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(15, 13)).build();

        //创建车辆路径问题，并添加车辆和任务
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle);
        vrpBuilder.addJob(service1).addJob(service2).addJob(service3).addJob(service4).addVehicle(v2);
        //设置车队规模为有限（FINITE）
        vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
        VehicleRoutingProblem problem = vrpBuilder.build();


        VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem)
                //CLUSTER_REGRET 策略用于衡量插入新任务到现有路径时的成本损失（regret-based insertion），这里将其权重设为 0，表示不启用此策略。
                .setProperty(Jsprit.Strategy.CLUSTER_REGRET, "0.")
                //CLUSTER_BEST 策略用于寻找当前最优插入位置，同样设置为 0 表示禁用该策略。
                .setProperty(Jsprit.Strategy.CLUSTER_BEST, "0.").buildAlgorithm();

        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();


        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);

        new Plotter(problem, bestSolution).plot("output/plot", "breaks");

    }

}

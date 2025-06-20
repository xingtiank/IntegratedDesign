package com.work.integratedDesign;


import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer.Label;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.listener.VehicleRoutingAlgorithmListener;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Activity;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.io.problem.VrpXMLWriter;
import com.graphhopper.jsprit.util.Examples;
import com.work.integratedDesign.pojo.OnePath;
import com.work.integratedDesign.service.PathRetrievalService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SpringBootTest
public class SimpleExample{
    @Resource
    private PathRetrievalService pathRetrievalService;

    /**
     * 使用Jsprit解决简单问题一般步骤
     * 1.创建车辆类型VehicleType
     * 2.创建车辆VehicleImpl，指定车辆类型，设置起始位置
     * 3.创建服务点Service，指定id，容量，坐标
     * 4.创建问题VehicleRoutingProblem，添加车辆和服务点
     * 5.创建算法VehicleRoutingAlgorithm，运行算法Collection<VehicleRoutingProblemSolution>，获取结果VehicleRoutingProblemSolution
     * 6.输出结果，可通过多种方式输出
     *
     *注：Jsprit多采用Builder模式，创建对象时，创建对象时不需要new
     */
    public  static void main(String[] args) {
        //创建输出目录
        Examples.createOutputFolder();
        //创建车辆类型，id为"vehicleType"，容量为2
        final int WEIGHT_INDEX = 0;
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(WEIGHT_INDEX, 2);
        VehicleType vehicleType = vehicleTypeBuilder.build();

        //创建车辆
        VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
        vehicleBuilder.setStartLocation(Location.newInstance(104.092048, 30.688573));
        vehicleBuilder.setType(vehicleType).setReturnToDepot(false);
        VehicleImpl vehicle = vehicleBuilder.build();

        //创建服务点，包括id，容量，坐标
        Service service1 = Service.Builder.newInstance("1").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(103.931658, 30.749167)).build();
        Service service2 = Service.Builder.newInstance("2").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(104.100221, 30.675709)).build();

        Service service3 = Service.Builder.newInstance("3").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(104.083766, 30.630647)).build();
        Service service4 = Service.Builder.newInstance("4").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(104.067565, 30.640916)).build();

        //创建问题，添加车辆和服务点
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle);
        vrpBuilder.addJob(service1).addJob(service2).addJob(service3).addJob(service4);

        VehicleRoutingProblem problem = vrpBuilder.build();
        //创建算法
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);
        // 使用 Jsprit.Builder 来配置算法
        VehicleRoutingAlgorithm initialAlgorithm = Jsprit.Builder.newInstance(problem)
                .setProperty(Jsprit.Parameter.ITERATIONS, "1") // 核心：只迭代一次！
                .buildAlgorithm();
        VehicleRoutingProblemSolution initialSolution = Solutions.bestOf(initialAlgorithm.searchSolutions());
        //运行算法
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

        //输出结果
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        //绘制路线
        new Plotter(problem, initialSolution).plot("output/plot_initial_from_jsprit.png", "Initial Solution");
        new Plotter(problem, bestSolution).plot("output/plot_optimized.png", "Optimized Solution");
//        new GraphStreamViewer(problem, bestSolution).labelWith(Label.ACTIVITY).setRenderDelay(200).display();
        SolutionPrinter.print(problem,bestSolution,  SolutionPrinter.Print.VERBOSE);
    }

}
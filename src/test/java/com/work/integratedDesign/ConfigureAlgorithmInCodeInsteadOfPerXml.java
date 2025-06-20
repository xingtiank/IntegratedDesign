package com.work.integratedDesign;

import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.io.algorithm.AlgorithmConfig;
import com.graphhopper.jsprit.io.algorithm.VehicleRoutingAlgorithms;
import com.graphhopper.jsprit.io.problem.VrpXMLWriter;
import com.graphhopper.jsprit.util.Examples;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.Collection;

/**
 * 如何在 Java 代码中直接配置算法参数和策略，而不是依赖传统的 XML 配置文件。
 */
public class ConfigureAlgorithmInCodeInsteadOfPerXml {

    public static void main(String[] args) {
        //一些准备-创建输出文件夹
        Examples.createOutputFolder();


        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(0, 2);
        VehicleType vehicleType = vehicleTypeBuilder.build();


        VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
        vehicleBuilder.setStartLocation(Location.newInstance(10, 10));
        vehicleBuilder.setType(vehicleType);
        VehicleImpl vehicle = vehicleBuilder.build();


        Service service1 = Service.Builder.newInstance("1").addSizeDimension(0, 1).setLocation(Location.newInstance(5, 7)).build();
        Service service2 = Service.Builder.newInstance("2").addSizeDimension(0, 1).setLocation(Location.newInstance(5, 13)).build();

        Service service3 = Service.Builder.newInstance("3").addSizeDimension(0, 1).setLocation(Location.newInstance(15, 7)).build();
        Service service4 = Service.Builder.newInstance("4").addSizeDimension(0, 1).setLocation(Location.newInstance(15, 13)).build();


        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle);
        vrpBuilder.addJob(service1).addJob(service2).addJob(service3).addJob(service4);

        VehicleRoutingProblem problem = vrpBuilder.build();

        //不使用 XML 文件，而是通过 Java 代码配置算法行为。
        AlgorithmConfig algorithmConfig = getAlgorithmConfig();
        VehicleRoutingAlgorithm algorithm = VehicleRoutingAlgorithms.createAlgorithm(problem, algorithmConfig);


        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();


        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        new VrpXMLWriter(problem, solutions).write("output/problem-with-solution.xml");

        SolutionPrinter.print(bestSolution);


        new Plotter(problem, bestSolution).plot("output/solution.png", "solution");
    }
    //手动配置算法策略、选择器、接受器等
    private static AlgorithmConfig getAlgorithmConfig() {
        AlgorithmConfig config = new AlgorithmConfig();
        XMLConfiguration xmlConfig = config.getXMLConfiguration();
        //设置迭代次数为 2000 次
        xmlConfig.setProperty("iterations", "2000");
        //设置插入算法为 bestInsertion
        xmlConfig.setProperty("construction.insertion[@name]", "bestInsertion");
        //设置内存策略为 1
        xmlConfig.setProperty("strategy.memory", 1);
        //设置搜索策略
        String searchStrategy = "strategy.searchStrategies.searchStrategy";
        //策略一：随机破坏 + 最佳插入
        xmlConfig.setProperty(searchStrategy + "(0)[@name]", "random_best");
        xmlConfig.setProperty(searchStrategy + "(0).selector[@name]", "selectBest");
        xmlConfig.setProperty(searchStrategy + "(0).acceptor[@name]", "acceptNewRemoveWorst");
        xmlConfig.setProperty(searchStrategy + "(0).modules.module(0)[@name]", "ruin_and_recreate");
        xmlConfig.setProperty(searchStrategy + "(0).modules.module(0).ruin[@name]", "randomRuin");
        xmlConfig.setProperty(searchStrategy + "(0).modules.module(0).ruin.share", "0.3");
        xmlConfig.setProperty(searchStrategy + "(0).modules.module(0).insertion[@name]", "bestInsertion");
        xmlConfig.setProperty(searchStrategy + "(0).probability", "0.5");
        //略二：径向破坏 + 最佳插入
        xmlConfig.setProperty(searchStrategy + "(1)[@name]", "radial_best");
        xmlConfig.setProperty(searchStrategy + "(1).selector[@name]", "selectBest");
        xmlConfig.setProperty(searchStrategy + "(1).acceptor[@name]", "acceptNewRemoveWorst");
        xmlConfig.setProperty(searchStrategy + "(1).modules.module(0)[@name]", "ruin_and_recreate");
        xmlConfig.setProperty(searchStrategy + "(1).modules.module(0).ruin[@name]", "radialRuin");
        xmlConfig.setProperty(searchStrategy + "(1).modules.module(0).ruin.share", "0.15");
        xmlConfig.setProperty(searchStrategy + "(1).modules.module(0).insertion[@name]", "bestInsertion");
        xmlConfig.setProperty(searchStrategy + "(1).probability", "0.5");

        return config;
    }

}

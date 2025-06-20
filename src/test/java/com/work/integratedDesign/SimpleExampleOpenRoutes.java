package com.work.integratedDesign;

import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
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
import com.graphhopper.jsprit.io.problem.VrpXMLWriter;
import com.graphhopper.jsprit.util.Examples;

import java.util.Collection;

/**
 * 开放式路径（Open Route）的简单车辆路径规划问题（Vehicle Routing Problem, VRP）
 * 即车辆从指定起点出发后，无需返回起点。
 */
public class SimpleExampleOpenRoutes {

    public static void main(String[] args) {

        /*
         * get a vehicle type-builder and build a type with the typeId "vehicleType" and a capacity of 2
         */
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(0, 2);
        vehicleTypeBuilder.setFixedCost(100);
        VehicleType vehicleType = vehicleTypeBuilder.build();

        /*
         * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
         */
        Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
        vehicleBuilder.setStartLocation(Location.newInstance(10, 10));
        vehicleBuilder.setType(vehicleType);
        vehicleBuilder.setReturnToDepot(false);

        VehicleImpl vehicle = vehicleBuilder.build();

        /*
         * build services at the required locations, each with a capacity-demand of 1.
         */
        Service service1 = Service.Builder.newInstance("1").addSizeDimension(0, 1).setLocation(Location.newInstance(5, 7)).build();
        Service service2 = Service.Builder.newInstance("2").addSizeDimension(0, 1).setLocation(Location.newInstance(5, 13)).build();

        Service service3 = Service.Builder.newInstance("3").addSizeDimension(0, 1).setLocation(Location.newInstance(15, 7)).build();
        Service service4 = Service.Builder.newInstance("4").addSizeDimension(0, 1).setLocation(Location.newInstance(15, 13)).build();


        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle);
        vrpBuilder.addJob(service1).addJob(service2).addJob(service3).addJob(service4);

        VehicleRoutingProblem problem = vrpBuilder.build();

        /*
         * get the algorithm out-of-the-box.
         */
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);

        /*
         * and search a solution
         */
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

        /*
         * get the best
         */
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        new VrpXMLWriter(problem, solutions).write("output/problem-with-solution.xml");

        SolutionPrinter.print(bestSolution);

        /*
         * plot
         */

        new Plotter(problem, bestSolution).plot("output/solution.png", "solution");

    }

}

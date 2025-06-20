package com.work.integratedDesign;

import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.analysis.toolbox.Plotter.Label;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.constraint.ServiceDeliveriesFirstConstraint;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Pickup;
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
 * 单车辆、带回程任务（Backhaul）的路径优化问题，适用于如下场景：
 * 物流配送中心需要先给客户送货，再从客户处回收货物
 * 快递员需先派送包裹，再收取客户的寄件
 * 通过添加约束机制确保送货任务优先执行，从而实现更合理的调度逻辑。
 */
public class SimpleVRPWithBackhaulsExample {

    public static void main(String[] args) {
        /*
         * some preparation - create output folder
         */
        Examples.createOutputFolder();

        /*
         * get a vehicle type-builder and build a type with the typeId "vehicleType" and a capacity of 2
         */
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(0, 2);
        VehicleType vehicleType = vehicleTypeBuilder.build();

        /*
         * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
         */
        Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
        vehicleBuilder.setStartLocation(Location.newInstance(10, 10));
        vehicleBuilder.setType(vehicleType);
        VehicleImpl vehicle = vehicleBuilder.build();

        /*
         * build pickups and deliveries at the required locations, each with a capacity-demand of 1.
         */
        Pickup pickup1 = Pickup.Builder.newInstance("1").addSizeDimension(0, 1).setLocation(Location.newInstance(5, 7)).build();
        Delivery delivery1 = Delivery.Builder.newInstance("2").addSizeDimension(0, 1).setLocation(Location.newInstance(5, 13)).build();

        Pickup pickup2 = Pickup.Builder.newInstance("3").addSizeDimension(0, 1).setLocation(Location.newInstance(15, 7)).build();
        Delivery delivery2 = Delivery.Builder.newInstance("4").addSizeDimension(0, 1).setLocation(Location.newInstance(15, 13)).build();


        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle);

        vrpBuilder.addJob(pickup1).addJob(pickup2).addJob(delivery1).addJob(delivery2);

        VehicleRoutingProblem problem = vrpBuilder.build();


        StateManager stateManager = new StateManager(problem);
        ConstraintManager constraintManager = new ConstraintManager(problem, stateManager);
        constraintManager.addConstraint(new ServiceDeliveriesFirstConstraint(), ConstraintManager.Priority.CRITICAL);

        VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem).setStateAndConstraintManager(stateManager,constraintManager)
                .buildAlgorithm();

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
        Plotter plotter = new Plotter(problem, bestSolution);
        plotter.setLabel(Label.SIZE);
        plotter.plot("output/solution.png", "solution");

    }

}

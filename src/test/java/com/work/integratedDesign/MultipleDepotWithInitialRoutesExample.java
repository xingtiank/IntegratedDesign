package com.work.integratedDesign;

import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.analysis.toolbox.Plotter.Label;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.Builder;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.io.problem.VrpXMLReader;
import com.graphhopper.jsprit.util.Examples;

import java.util.Collection;

/**
 * 多仓库车辆路径问题（MDVRP） 中引入 初始固定路径（Initial Route），并在此基础上进行路径优化求解。
 */
public class MultipleDepotWithInitialRoutesExample {


    public static void main(String[] args) {
        /*
         * some preparation - create output folder
         */
        Examples.createOutputFolder();

        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        /*
         * Read cordeau-instance p01
         */
        new VrpXMLReader(vrpBuilder).read("input/cordeau01.xml");

        /*
         * Add initial route with 1_4_vehicle and services 44, 26
         */
        VehicleRoute initialRoute = VehicleRoute.Builder.newInstance(getVehicle("1_4_vehicle", vrpBuilder)).addService(getService("44", vrpBuilder))
                .addService(getService("26", vrpBuilder)).build();
        vrpBuilder.addInitialVehicleRoute(initialRoute);

        /*
         * build the problem
         */
        VehicleRoutingProblem vrp = vrpBuilder.build();
        /*
         * since job (service) 26 and 44 are already planned in initial route and thus static AND sequence is fixed they
         * should not be in jobMap anymore (only variable jobs are in jobMap)
         */
        assert !vrp.getJobs().containsKey("26") : "strange. service 26 should not be part of the problem";
        assert !vrp.getJobs().containsKey("44") : "strange. service 44 should not be part of the problem";

        /*
         * plot to see how the problem looks like
         */
        new Plotter(vrp).setLabel(Label.ID).plot("output/cordeau01_problem_withInitialRoute.png", "c");

        /*
         * solve the problem
         */
//		VehicleRoutingAlgorithm vra = Jsprit.Builder.newInstance(vrp)
//				.setProperty(Jsprit.Parameter.ITERATIONS,"10000").buildAlgorithm();

        VehicleRoutingAlgorithm vra = Jsprit.createAlgorithm(vrp);
        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();

        SolutionPrinter.print(Solutions.bestOf(solutions));

        new Plotter(vrp, Solutions.bestOf(solutions)).setLabel(Label.ID).plot("output/cordeau01_solution_withInitialRoute.png", "p01");


    }

    private static Service getService(String serviceId, Builder vrpBuilder) {
        for (Job j : vrpBuilder.getAddedJobs()) {
            if (j.getId().equals(serviceId)) {
                return (Service) j;
            }
        }
        return null;
    }

    private static Vehicle getVehicle(String vehicleId, Builder vrpBuilder) {
        for (Vehicle v : vrpBuilder.getAddedVehicles()) {
            if (v.getId().equals(vehicleId)) return v;
        }
        return null;
    }

}

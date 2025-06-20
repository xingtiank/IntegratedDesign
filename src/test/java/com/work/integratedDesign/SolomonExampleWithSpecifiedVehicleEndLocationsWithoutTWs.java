package com.work.integratedDesign;

import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer.Label;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.selector.SelectBest;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.io.problem.VrpXMLReader;
import com.graphhopper.jsprit.util.Examples;

import java.io.File;
import java.util.Collection;

/**
 *  带指定车辆终点、无时间窗约束的 Solomon 车辆路径问题（Vehicle Routing Problem, VRP）
 */
public class SolomonExampleWithSpecifiedVehicleEndLocationsWithoutTWs {

    public static void main(String[] args) {
        Examples.createOutputFolder();
        /*
         * Build the problem.
         *
         * But define a problem-builder first.
         */
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

        /*
         * A solomonReader reads solomon-instance files, and stores the required information in the builder.
         */
        new VrpXMLReader(vrpBuilder).read("input/pickups_and_deliveries_solomon_c101_withoutTWs_and_specifiedVehicleEndLocations.xml");

        /*
         * Finally, the problem can be built. By default, transportCosts are crowFlyDistances (as usually used for vrp-instances).
         */
//		vrpBuilder.addProblemConstraint(Constraint.DELIVERIES_FIRST);
        VehicleRoutingProblem vrp = vrpBuilder.build();

        Plotter pblmPlotter = new Plotter(vrp);
        pblmPlotter.plot("output/solomon_C101_specifiedVehicleEndLocations_withoutTWs.png", "C101");

        /*
         * Define the required vehicle-routing algorithms to solve the above problem.
         *
         * The algorithm can be defined and configured in an xml-file.
         */
//		VehicleRoutingAlgorithm vra = new SchrimpfFactory().createAlgorithm(vrp);
        VehicleRoutingAlgorithm vra = Jsprit.createAlgorithm(vrp);
        vra.setMaxIterations(20000);
//		vra.setPrematureBreak(100);
//		vra.getAlgorithmListeners().addListener(new AlgorithmSearchProgressChartListener("output/sol_progress.png"));
        /*
         * Solve the problem.
         *
         *
         */
        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();

        /*
         * Retrieve best solution.
         */
        VehicleRoutingProblemSolution solution = new SelectBest().selectSolution(solutions);

        /*
         * print solution
         */
        SolutionPrinter.print(solution);

        /*
         * Plot solution.
         */
//		SolutionPlotter.plotSolutionAsPNG(vrp, solution, "output/solomon_C101_specifiedVehicleEndLocations_solution.png","C101");
        Plotter solPlotter = new Plotter(vrp, solution);
        solPlotter.plot("output/solomon_C101_specifiedVehicleEndLocations_withoutTWs_solution.png", "C101");


        new GraphStreamViewer(vrp, solution).setRenderDelay(50).labelWith(Label.ID).display();


    }

}
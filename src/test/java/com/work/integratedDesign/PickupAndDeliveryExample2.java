package com.work.integratedDesign;

import com.graphhopper.jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.analysis.toolbox.Plotter.Label;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.selector.SelectBest;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.io.problem.VrpXMLReader;
import com.graphhopper.jsprit.util.Examples;

import java.util.Collection;

/**
 * 带提货和送货（Pickup and Delivery）约束的车辆路径问题（PDVRP）
 */
public class PickupAndDeliveryExample2 {

    public static void main(String[] args) {

        /*
         * some preparation - create output folder
         */
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
        new VrpXMLReader(vrpBuilder).read("input/pd_christophides_vrpnc1_vcap50.xml");

        /*
         * Finally, the problem can be built. By default, transportCosts are crowFlyDistances (as usually used for vrp-instances).
         */

        VehicleRoutingProblem vrp = vrpBuilder.build();

        new Plotter(vrp).plot("output/pd_christophides_vrpnc1.png", "pd_vrpnc1");


        /*
         * Define the required vehicle-routing algorithms to solve the above problem.
         *
         * The algorithm can be defined and configured in an xml-file.
         */

        VehicleRoutingAlgorithm vra = Jsprit.createAlgorithm(vrp);
        vra.getAlgorithmListeners().addListener(new AlgorithmSearchProgressChartListener("output/sol_progress.png"));
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
//		SolutionPlotter.plotSolutionAsPNG(vrp, solution, "output/pd_solomon_r101_solution.png","pd_r101");
        Plotter plotter = new Plotter(vrp, solution);
        plotter.setLabel(Label.SIZE);
        plotter.plot("output/pd_christophides_vrpnc1_solution.png", "pd_vrpnc1");


        new GraphStreamViewer(vrp, solution).setRenderDelay(200).display();

    }

}
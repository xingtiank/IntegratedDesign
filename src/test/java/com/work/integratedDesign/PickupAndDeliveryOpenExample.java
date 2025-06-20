package com.work.integratedDesign;

import com.graphhopper.jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
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
 * 带提货和送货任务的车辆路径优化问题
 * 车辆从起点出发，完成任务后不需要返回起点。
 */
public class PickupAndDeliveryOpenExample {

    public static void main(String[] args) {

        /*
         * Build the problem.
         *
         * But define a problem-builder first.
         */
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

        /*
         * A solomonReader reads solomon-instance files, and stores the required information in the builder.
         */
        new VrpXMLReader(vrpBuilder).read("input/pickups_and_deliveries_solomon_r101_withoutTWs_open.xml");

        /*
         * Finally, the problem can be built. By default, transportCosts are crowFlyDistances (as usually used for vrp-instances).
         */

        VehicleRoutingProblem vrp = vrpBuilder.build();

//		SolutionPlotter.plotVrpAsPNG(vrp, "output/pd_solomon_r101_o.png", "pd_r101");

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
        plotter.plot("output/pd_solomon_r101_solution_open.png", "pd_r101");


    }

}

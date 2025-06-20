package com.work.integratedDesign;

import com.graphhopper.jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
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
 * Solomon 标准数据集的车辆路径规划问题（如 R101 实例）
 * R101 是一个客户位置随机、时间窗紧凑的经典 VRPTW 实例
 */
public class SolomonR101Example {

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
//		new SolomonReader(vrpBuilder).read("/Users/schroeder/IdeaProjects/jsprit/jsprit-instances/instances/solomon/R211.txt");
        new VrpXMLReader(vrpBuilder).read("output/R211.xml");
        /*
         * Finally, the problem can be built. By default, transportCosts are crowFlyDistances (as usually used for vrp-instances).
         */
        VehicleRoutingProblem vrp = vrpBuilder.build();

//        new VrpXMLWriter(vrp).write("output/R211.xml");
//		new Plotter(vrp).plot("output/solomon_R101.png", "R101");

        /*
         * Define the required vehicle-routing algorithms to solve the above problem.
         *
         * The algorithm can be defined and configured in an xml-file.
         */
//		VehicleRoutingAlgorithm vra = new SchrimpfFactory().createAlgorithm(vrp);
        VehicleRoutingAlgorithm vra = Jsprit.createAlgorithm(vrp);
        vra.setMaxIterations(20000);
//		vra.setPrematureBreak(100);
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
        SolutionPrinter.print(vrp, solution, SolutionPrinter.Print.VERBOSE);

        new GraphStreamViewer(vrp, solution).display();
        /*
         * Plot solution.
         */
//		new Plotter(vrp,solution).plot( "output/solomon_R101_solution.png","R101");

    }

}

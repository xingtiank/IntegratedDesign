package com.work.integratedDesign;

import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer.Label;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.SchrimpfFactory;
import com.graphhopper.jsprit.core.algorithm.selector.SelectBest;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.instance.reader.SolomonReader;
import com.graphhopper.jsprit.util.Examples;

import java.util.Collection;

/**
 * Solomon 基准 VRP 实例（C101），适用于测试和验证车辆路径优化算法在带时间窗约束下的调度效果。整个流程包括：
 * 读取标准数据
 * 构建问题模型
 * 启发式算法求解
 * 输出与可视化分析
 * 适用于物流配送、快递调度等场景下的路径优化研究与实践。
 */
public class SolomonExample {

    public static void main(String[] args) {

        Examples.createOutputFolder();


        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

        /*
         * A solomonReader reads solomon-instance files, and stores the required information in the builder.
         */
        new SolomonReader(vrpBuilder).read("input/C101_solomon.txt");


        /*
         * Finally, the problem can be built. By default, transportCosts are crowFlyDistances (as usually used for vrp-instances).
         */
        VehicleRoutingProblem vrp = vrpBuilder.build();

        new Plotter(vrp).plot("output/solomon_C101.png", "C101");

        /*
         * Define the required vehicle-routing algorithms to solve the above problem.
         *
         * The algorithm can be defined and configured in an xml-file.
         */
        VehicleRoutingAlgorithm vra = new SchrimpfFactory().createAlgorithm(vrp);

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

        /*
         * Plot solution.
         */
        Plotter plotter = new Plotter(vrp, solution);
//		plotter.setBoundingBox(30, 0, 50, 20);
        plotter.plot("output/solomon_C101_solution.png", "C101");

        new GraphStreamViewer(vrp, solution).setCameraView(30, 30, 0.25).labelWith(Label.ID).setRenderDelay(100).display();

    }

}

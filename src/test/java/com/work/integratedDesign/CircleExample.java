package com.work.integratedDesign;


import com.graphhopper.jsprit.analysis.toolbox.AlgorithmEventsRecorder;
import com.graphhopper.jsprit.analysis.toolbox.AlgorithmEventsViewer;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.GreedySchrimpfFactory;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 构建了一个圆形分布的服务点布局，并运行算法求解最优路径。
 */
public class CircleExample {

    /**
     * 创建一个圆形分布的服务点布局
     *
     * @param center_x 圆心x坐标
     * @param center_y 圆心y坐标
     * @param radius   圆的半径
     * @param step     圆上点的间隔
     * @return 包含多个 Coordinate 的集合，表示圆周上的一系列点。
     */
    public static Collection<Coordinate> createCoordinates(double center_x, double center_y, double radius, double step) {
        Collection<Coordinate> coords = new ArrayList<Coordinate>();
        for (double theta = 0; theta < 2 * Math.PI; theta += step) {
            double x = center_x + radius * Math.cos(theta);
            double y = center_y - radius * Math.sin(theta);
            coords.add(Coordinate.newInstance(x, y));
        }
        return coords;
    }

    public static void main(String[] args) {
        File dir = new File("output");
        // if the directory does not exist, create it
        if (!dir.exists()) {
            System.out.println("creating directory ./output");
            boolean result = dir.mkdir();
            if (result) System.out.println("./output created");
        }

        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        VehicleImpl v = VehicleImpl.Builder.newInstance("v")
                .setStartLocation(Location.Builder.newInstance().setCoordinate(Coordinate.newInstance(0, 0)).build()).build();
        vrpBuilder.addVehicle(v);
        /**
         * 创建约 50 个服务点，均匀分布在半径为 20 的圆周上；
         * 每个服务点都有唯一 ID 和对应坐标；
         * 最终构建完整的 VehicleRoutingProblem 对象。
         */
        double step = 2 * Math.PI / 50.;
        Collection<Coordinate> circle = createCoordinates(0, 0, 20, step);
        int id = 1;
        for (Coordinate c : circle) {
            Service s = Service.Builder.newInstance(Integer.toString(id)).setLocation(Location.Builder.newInstance().setCoordinate(c).build()).build();
            vrpBuilder.addJob(s);
            id++;
        }
        VehicleRoutingProblem vrp = vrpBuilder.build();

        //使用 GreedySchrimpfFactory 创建算法（一种基于贪心策略的插入算法）；
        //设置最大迭代次数为 50 次。
        VehicleRoutingAlgorithm vra = new GreedySchrimpfFactory().createAlgorithm(vrp);
        vra.setMaxIterations(50);
        //记录算法执行过程
        AlgorithmEventsRecorder eventsRecorder = new AlgorithmEventsRecorder(vrp, "output/events.dgs.gz");
        eventsRecorder.setRecordingRange(0, 50);
        vra.addListener(eventsRecorder);

        VehicleRoutingProblemSolution solution = Solutions.bestOf(vra.searchSolutions());

        new Plotter(vrp, solution).plot("output/circle.png", "circleProblem");
        new GraphStreamViewer(vrp, solution).display();

        /**
         * 回放之前记录的迭代过程；
         * 设置破坏（ruin）和重建（recreate）动画延迟时间；
         * 显示整个优化过程。
         */
        AlgorithmEventsViewer viewer = new AlgorithmEventsViewer();
        viewer.setRuinDelay(16);
        viewer.setRecreationDelay(8);
        viewer.display("output/events.dgs.gz");

    }

}

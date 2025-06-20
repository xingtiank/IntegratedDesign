package com.work.integratedDesign;

import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.state.StateId;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.algorithm.state.StateUpdater;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.constraint.HardActivityConstraint;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.ActivityVisitor;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.EuclideanDistanceCalculator;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import com.graphhopper.jsprit.io.problem.VrpXMLReader;

import java.util.Collection;


/**
 * 带有最大行驶距离约束的车辆路径问题（Vehicle Routing Problem, VRP）
 */
public class AdditionalDistanceConstraintExample {
    //状态更新器，每次路线计算时，动态更新路线行驶距离
    static class DistanceUpdater implements StateUpdater, ActivityVisitor {
        //状态管理器
        private final StateManager stateManager;
        //交通成本矩阵
        private final VehicleRoutingTransportCostsMatrix costMatrix;
        //行驶距离状态
        private final StateId distanceStateId;
        //路线
        private VehicleRoute vehicleRoute;
        //距离
        private double distance = 0.;
        //上一个活动
        private TourActivity prevAct;


        public DistanceUpdater(StateId distanceStateId, StateManager stateManager, VehicleRoutingTransportCostsMatrix transportCosts) {
            this.costMatrix = transportCosts;
            this.stateManager = stateManager;
            this.distanceStateId = distanceStateId;
        }
        //初始化当前路线的距离为0，并记录起始点。
        @Override
        public void begin(VehicleRoute vehicleRoute) {
            distance = 0.;
            prevAct = vehicleRoute.getStart();
            this.vehicleRoute = vehicleRoute;
        }
        //访问每个任务点，累加从上一个点到当前点的距离。
        @Override
        public void visit(TourActivity tourActivity) {
            distance += getDistance(prevAct, tourActivity);
            prevAct = tourActivity;
        }
        //处理终点前最后一步的距离并保存总距离值到 StateManager 中。
        @Override
        public void finish() {
            distance += getDistance(prevAct, vehicleRoute.getEnd());
            stateManager.putRouteState(vehicleRoute, distanceStateId, distance);
        }
        //根据预设的成本矩阵获取两点之间的距离。
        double getDistance(TourActivity from, TourActivity to) {
            return costMatrix.getDistance(from.getLocation().getId(), to.getLocation().getId());
        }
    }
    //自定义硬性约束，距离约束，用于确保插入新任务后，路线的总行驶距离不超过设定的最大值
    static class DistanceConstraint implements HardActivityConstraint {

        private final StateManager stateManager;

        private final VehicleRoutingTransportCostsMatrix costsMatrix;
        //最大距离
        private final double maxDistance;

        //距离状态
        private final StateId distanceStateId;


        DistanceConstraint(double maxDistance, StateId distanceStateId, StateManager stateManager, VehicleRoutingTransportCostsMatrix transportCosts) { //head of development - upcoming release (v1.4)
            this.costsMatrix = transportCosts;
            this.maxDistance = maxDistance;
            this.stateManager = stateManager;
            this.distanceStateId = distanceStateId;
        }

        /**
         * 计算插入新任务对路线距离的影响；
         * 获取当前路线已有的距离；
         * 判断加入新任务后是否超过最大允许距离；
         * 若超过则返回 NOT_FULFILLED，阻止插入该任务。
         */
        @Override
        public ConstraintsStatus fulfilled(JobInsertionContext context, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double v) {
            //计算新增距离
            double additionalDistance = getDistance(prevAct, newAct) + getDistance(newAct, nextAct) - getDistance(prevAct, nextAct);
            //获取当前路线总距离
            Double routeDistance = stateManager.getRouteState(context.getRoute(), distanceStateId, Double.class);
            if (routeDistance == null) routeDistance = 0.;
            double newRouteDistance = routeDistance + additionalDistance;
            //判断是否超限
            if (newRouteDistance > maxDistance) {
                return ConstraintsStatus.NOT_FULFILLED;
            } else return ConstraintsStatus.FULFILLED;
        }

        double getDistance(TourActivity from, TourActivity to) {
            return costsMatrix.getDistance(from.getLocation().getId(), to.getLocation().getId());
        }

    }

    public static void main(String[] args) {

        //读取问题数据
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrpXMLReader(vrpBuilder).read("input/pickups_and_deliveries_solomon_r101_withoutTWs.xml");
        //基于欧几里得距离生成一个运输成本矩阵，用于计算各点之间的距离和时间。
        VehicleRoutingTransportCostsMatrix costMatrix = createMatrix(vrpBuilder);
        vrpBuilder.setRoutingCost(costMatrix);
        VehicleRoutingProblem vrp = vrpBuilder.build();

        //设置状态管理器与自定义状态更新器
        StateManager stateManager = new StateManager(vrp);

        StateId distanceStateId = stateManager.createStateId("distance");
        stateManager.addStateUpdater(new DistanceUpdater(distanceStateId, stateManager, costMatrix));
        //添加自定义硬性约束，设置最大行驶距离为 120；
        //将 DistanceConstraint 加入约束管理器，并设置为关键优先级。
        ConstraintManager constraintManager = new ConstraintManager(vrp, stateManager);
        constraintManager.addConstraint(new DistanceConstraint(120., distanceStateId, stateManager, costMatrix), ConstraintManager.Priority.CRITICAL);
        //构建算法并求解，设置最大迭代次数为 250，开始搜索最优解。
        VehicleRoutingAlgorithm vra = Jsprit.Builder.newInstance(vrp).setStateAndConstraintManager(stateManager,constraintManager)
                .buildAlgorithm();

        vra.setMaxIterations(250);

        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();

        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
        //输出结果
        SolutionPrinter.print(vrp, bestSolution, SolutionPrinter.Print.VERBOSE);

        new Plotter(vrp, Solutions.bestOf(solutions)).plot("output/plot", "plot");

        new GraphStreamViewer(vrp, bestSolution).labelWith(GraphStreamViewer.Label.ID).setRenderDelay(200).display();
    }

    //创建成本矩阵，包括距离成本和时间成本
    private static VehicleRoutingTransportCostsMatrix createMatrix(VehicleRoutingProblem.Builder vrpBuilder) {
        //创建成本矩阵，对称为true
        VehicleRoutingTransportCostsMatrix.Builder matrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);
        //遍历地点
        for (String from : vrpBuilder.getLocationMap().keySet()) {
            for (String to : vrpBuilder.getLocationMap().keySet()) {
                Coordinate fromCoord = vrpBuilder.getLocationMap().get(from);
                Coordinate toCoord = vrpBuilder.getLocationMap().get(to);
                double distance = EuclideanDistanceCalculator.calculateDistance(fromCoord, toCoord);
                matrixBuilder.addTransportDistance(from, to, distance);
                matrixBuilder.addTransportTime(from, to, (distance / 2.));
            }
        }
        return matrixBuilder.build();
    }

}
package com.work.integratedDesign;


import com.graphhopper.jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer.Label;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.state.StateId;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.algorithm.state.StateUpdater;
import com.graphhopper.jsprit.core.algorithm.termination.VariationCoefficientTermination;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.Builder;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.constraint.HardActivityConstraint;
import com.graphhopper.jsprit.core.problem.constraint.HardRouteConstraint;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.driver.DriverImpl;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.ReverseActivityVisitor;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity.JobActivity;
import com.graphhopper.jsprit.core.problem.solution.route.state.RouteAndActivityStateGetter;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.CrowFlyCosts;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.util.Examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * This class provides the/a solution to the following problem:
 * <p>
 * Statement of the problem (see Stackoverflow: http://stackoverflow.com/questions/19080537/bicycle-messenger-tsppd-with-optaplanner/20412598#20412598):
 * <p>
 * Optimize the routes for a bicycle messenger service!
 * Assume 5 messengers that have to pick up 30 envelopes distributed through the city. These 5 messengers are distributed through the city as well. Thus
 * there is no single depot and they do not need to go back to their original starting location.
 * <p>
 * Additional hard constraints:
 * 1) Every messenger can carry up to fifteen envelopes
 * 2) The way an evelopes travels should be less than three times the direct route (so delivery does not take too long)
 * <p>
 * Thus this problem is basically a Capacitated VRP with Pickups and Deliveries, Multiple Depots, Open Routes and Time Windows/Restrictions.
 *
 * @author stefan schroeder
 */

/**
 * 这个问题基本上是一个有容量的VRP，有提货和送货、多个仓库、开放路线和时间窗口/限制。
 */
public class BicycleMessenger {

    //硬约束：信封的递送时间不得超过3*bestDirect（即直接递送中最快的信使）
    static class ThreeTimesLessThanBestDirectRouteConstraint implements HardActivityConstraint {
        //路程成本
        private final VehicleRoutingTransportCosts routingCosts;
        //状态管理器
        private final RouteAndActivityStateGetter stateManager;

        //jobId map direct-distance by nearestMessenger
        //key:jobId,value:bestDirect
        private final Map<String, Double> bestMessengers;

        private final StateId latest_act_arrival_time_stateId;

        public ThreeTimesLessThanBestDirectRouteConstraint(StateId latest_act_arrival_time, Map<String, Double> nearestMessengers, VehicleRoutingTransportCosts routingCosts, RouteAndActivityStateGetter stateManager) {
            this.bestMessengers = nearestMessengers;
            this.routingCosts = routingCosts;
            this.stateManager = stateManager;
            this.latest_act_arrival_time_stateId = latest_act_arrival_time;
        }

        /**
         *
         * @param iFacts 插入上下文信息，包含新车辆、司机等信息。
         * @param prevAct 当前路径中新活动插入位置的前一个活动。
         * @param newAct 要插入的新活动（可能是取件或送货）。
         * @param nextAct 当前路径中新活动插入位置的后一个活动。
         * @param prevActDepTime 前一个活动的出发时间。
         * @return  ConstraintsStatus.FULFILLED: 约束满足，可以插入。
         *          ConstraintsStatus.NOT_FULFILLED: 插入会导致后续活动不满足条件，但可能还有机会调整。
         *          ConstraintsStatus.NOT_FULFILLED_BREAK: 无论如何都无法满足条件，直接放弃插入。
         */
        @Override
        public ConstraintsStatus fulfilled(JobInsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
            //计算从 prevAct 出发，直接前往 nextAct 所需的时间。
            double arrTime_at_nextAct_onDirectRoute = prevActDepTime + routingCosts.getTransportTime(prevAct.getLocation(), nextAct.getLocation(), prevActDepTime, iFacts.getNewDriver(), iFacts.getNewVehicle());
            // nextAct 的最晚到达时间
            Double latest_arrTime_at_nextAct = stateManager.getActivityState(nextAct, latest_act_arrival_time_stateId, Double.class);
            if (latest_arrTime_at_nextAct == null)
                latest_arrTime_at_nextAct = nextAct.getTheoreticalLatestOperationStartTime();
            //如果这个时间超过了 nextAct 的最晚到达时间，则无法插入任何新活动，直接返回 NOT_FULFILLED_BREAK。
            if (arrTime_at_nextAct_onDirectRoute > latest_arrTime_at_nextAct) {
                return ConstraintsStatus.NOT_FULFILLED_BREAK;
            }
            // 计算新活动 newAct 的到达时间。
            double arrTime_at_newAct = prevActDepTime + routingCosts.getTransportTime(prevAct.getLocation(), newAct.getLocation(), prevActDepTime, iFacts.getNewDriver(), iFacts.getNewVehicle());
            //局部影响
            //计算新活动 newAct 的到达时间。
            double directTimeOfNearestMessenger = bestMessengers.get(((JobActivity) newAct).getJob().getId());
            //无论是提货还是送货。两个到达时间都必须小于3*best，如果到达时间超过该任务由“最近信使”直达所需时间的三倍，则直接拒绝插入。
            if (arrTime_at_newAct > 3 * directTimeOfNearestMessenger) {
                return ConstraintsStatus.NOT_FULFILLED_BREAK;
            }

            //对整个路线的影响，因为插入新法案会及时推进所有后续活动
            //计算新活动完成后离开的时间（departureTime_at_newAct）。
            double departureTime_at_newAct = arrTime_at_newAct + newAct.getOperationTime();
            //推算新活动的最新允许到达时间（基于下一个活动的最晚到达时间减去运输时间）。
            double latest_arrTime_at_newAct = latest_arrTime_at_nextAct - routingCosts.getTransportTime(newAct.getLocation(), nextAct.getLocation(), departureTime_at_newAct, iFacts.getNewDriver(), iFacts.getNewVehicle());
            //如果实际到达时间超过最新允许时间，说明插入会影响后续活动，返回 NOT_FULFILLED。
            if (arrTime_at_newAct > latest_arrTime_at_newAct) {
                return ConstraintsStatus.NOT_FULFILLED;
            }
            //插入新活动后，nextAct 的到达时间会往后推移。
            double arrTime_at_nextAct = departureTime_at_newAct + routingCosts.getTransportTime(newAct.getLocation(), nextAct.getLocation(), departureTime_at_newAct, iFacts.getNewDriver(), iFacts.getNewVehicle());
            //如果推移后超过其最晚到达时间，说明插入导致了不可接受的延迟，返回 NOT_FULFILLED。
            if (arrTime_at_nextAct > latest_arrTime_at_nextAct) {
                return ConstraintsStatus.NOT_FULFILLED;
            }
            //返回 FULFILLED 表示插入成功
            return ConstraintsStatus.FULFILLED;
        }

    }

    /**
     * When inserting the activities of an envelope which are pickup and deliver envelope, this constraint makes insertion procedure to ignore messengers that are too far away to meet the 3*directTime-Constraint.
     * <p>
     * <p>one does not need this constraint. but it is faster. the earlier the solution-space can be constraint the better/faster.
     *
     * @author schroeder
     */
    //硬路线约束：当插入信封的取送活动时，此约束使插入过程忽略距离太远而无法满足3*directTime约束的信使。
    static class IgnoreMessengerThatCanNeverMeetTimeRequirements implements HardRouteConstraint {

        private final Map<String, Double> bestMessengers;

        private final VehicleRoutingTransportCosts routingCosts;

        public IgnoreMessengerThatCanNeverMeetTimeRequirements(Map<String, Double> bestMessengers, VehicleRoutingTransportCosts routingCosts) {
            super();
            this.bestMessengers = bestMessengers;
            this.routingCosts = routingCosts;
        }

        @Override
        public boolean fulfilled(JobInsertionContext insertionContext) {
            double timeOfDirectRoute = getTimeOfDirectRoute(insertionContext.getJob(), insertionContext.getNewVehicle(), routingCosts);
            double timeOfNearestMessenger = bestMessengers.get(insertionContext.getJob().getId());
            return !(timeOfDirectRoute > 3 * timeOfNearestMessenger);
        }

    }

    /**
     * updates the state "latest-activity-start-time" (required above) once route/activity states changed, i.e. when removing or inserting an envelope-activity
     * <p>
     * <p>thus once either the insertion-procedure starts or an envelope has been inserted, this visitor runs through the route in reverse order (i.e. starting with the end of the route) and
     * calculates the latest-activity-start-time (or latest-activity-arrival-time) which is the time to just meet the constraints of subsequent activities.
     *
     * @author schroeder
     */
    //状态更新器：反向遍历路径以更新最新到达时间
    static class UpdateLatestActivityStartTimes implements StateUpdater, ReverseActivityVisitor {
        //存储和更新活动的状态（如最晚到达时间）。
        private final StateManager stateManager;
        //计算两点之间的运输时间。
        private final VehicleRoutingTransportCosts routingCosts;
        //每个任务（信封）对应的最近信使直达时间。
        private final Map<String, Double> bestMessengers;
        //当前正在处理的车辆路径。
        private VehicleRoute route;
        //上一个处理过的活动节点
        private TourActivity prevAct;
        //上一个活动的最晚到达时间。
        private double latest_arrTime_at_prevAct;
        //状态 ID，用于标识“最晚到达时间”这个状态值。
        private final StateId latest_act_arrival_time_stateId;

        public UpdateLatestActivityStartTimes(StateId latest_act_arrival_time, StateManager stateManager, VehicleRoutingTransportCosts routingCosts, Map<String, Double> bestMessengers) {
            super();
            this.stateManager = stateManager;
            this.routingCosts = routingCosts;
            this.bestMessengers = bestMessengers;
            this.latest_act_arrival_time_stateId = latest_act_arrival_time;
        }
        //初始化遍历过程，设置当前路径，并将“上一个活动”设为路径的终点（end）。
        @Override
        public void begin(VehicleRoute route) {
            this.route = route;
            latest_arrTime_at_prevAct = route.getEnd().getTheoreticalLatestOperationStartTime();
            prevAct = route.getEnd();
        }
        //对当前活动节点进行处理，计算其最晚到达时间，并更新状态。
        @Override
        public void visit(TourActivity currAct) {
            //获取当前任务的最佳直达时间
            double timeOfNearestMessenger = bestMessengers.get(((JobActivity) currAct).getJob().getId());
            //基于后续活动的最晚到达时间
            double potential_latest_arrTime_at_currAct =
                    latest_arrTime_at_prevAct - routingCosts.getBackwardTransportTime(currAct.getLocation(), prevAct.getLocation(), latest_arrTime_at_prevAct, route.getDriver(), route.getVehicle()) - currAct.getOperationTime();
            //真正的最晚到达时间为两者较小者，不得超过三倍于最佳直达时间，同时也不能导致后续活动超时
            double latest_arrTime_at_currAct = Math.min(3 * timeOfNearestMessenger, potential_latest_arrTime_at_currAct);
            //将计算出的最晚到达时间保存到状态中，如果当前活动的实际到达时间大于最晚允许时间，则抛出异常（仅在启用断言时触发）
            stateManager.putActivityState(currAct, latest_act_arrival_time_stateId, latest_arrTime_at_currAct);
            assert currAct.getArrTime() <= latest_arrTime_at_currAct : "this must not be since it breaks condition; actArrTime: " + currAct.getArrTime() + " latestArrTime: " + latest_arrTime_at_currAct + " vehicle: " + route.getVehicle().getId();
            //更新“上一个活动”和对应时间
            latest_arrTime_at_prevAct = latest_arrTime_at_currAct;
            prevAct = currAct;
        }
        //表示反向遍历结束后的清理工作
        @Override
        public void finish() {
        }

    }

    /**
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Examples.createOutputFolder();

		/*
        build the problem
		 */
        VehicleRoutingProblem.Builder problemBuilder = VehicleRoutingProblem.Builder.newInstance();
        problemBuilder.setFleetSize(FleetSize.FINITE);
        readEnvelopes(problemBuilder);
        readMessengers(problemBuilder);
        //add constraints to problem
        VehicleRoutingTransportCosts routingCosts = new CrowFlyCosts(problemBuilder.getLocations()); //which is the default VehicleRoutingTransportCosts in builder above
        problemBuilder.setRoutingCost(routingCosts);
        //finally build the problem
//        problemBuilder.addPenaltyVehicles(20.0,50000);
        VehicleRoutingProblem bicycleMessengerProblem = problemBuilder.build();

        /*
        define states and constraints
         */
        //map mapping nearest messengers, i.e. for each envelope the direct-delivery-time with the fastest messenger is stored here
        Map<String, Double> nearestMessengers = getNearestMessengers(routingCosts, problemBuilder.getAddedJobs(), problemBuilder.getAddedVehicles());

        //define stateManager to update the required activity-state: "latest-activity-start-time"
        StateManager stateManager = new StateManager(bicycleMessengerProblem);
        //create state
        StateId latest_act_arrival_time_stateId = stateManager.createStateId("latest-act-arrival-time");
        //and make sure you update the activity-state "latest-activity-start-time" the way it is defined above
        stateManager.addStateUpdater(new UpdateLatestActivityStartTimes(latest_act_arrival_time_stateId, stateManager, routingCosts, nearestMessengers));
        stateManager.updateLoadStates();

        ConstraintManager constraintManager = new ConstraintManager(bicycleMessengerProblem, stateManager);
        constraintManager.addLoadConstraint();
        constraintManager.addConstraint(new ThreeTimesLessThanBestDirectRouteConstraint(latest_act_arrival_time_stateId, nearestMessengers, routingCosts, stateManager), ConstraintManager.Priority.CRITICAL);
        constraintManager.addConstraint(new IgnoreMessengerThatCanNeverMeetTimeRequirements(nearestMessengers, routingCosts));

        VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(bicycleMessengerProblem)
                .setStateAndConstraintManager(stateManager,constraintManager).buildAlgorithm();

        algorithm.setMaxIterations(2000);

//		VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(bicycleMessengerProblem)
//				.setStateAndConstraintManager(stateManager, constraintManager)
//				.setProperty(Jsprit.Parameter.THREADS.toString(), "6")
////				.setProperty(Jsprit.Strategy.RADIAL_BEST.toString(), "0.25")
////				.setProperty(Jsprit.Strategy.WORST_BEST.toString(), "0.25")
////				.setProperty(Jsprit.Strategy.CLUSTER_BEST.toString(), "0.25")
////				.setProperty(Jsprit.Strategy.RANDOM_BEST.toString(), "0.")
////				.setProperty(Jsprit.Strategy.RANDOM_REGRET.toString(), "1.")
//				.setProperty(Jsprit.Parameter.INSERTION_NOISE_LEVEL.toString(),"0.01")
//				.setProperty(Jsprit.Parameter.INSERTION_NOISE_PROB.toString(), "0.2")
////				.setProperty(Jsprit.Parameter.THRESHOLD_ALPHA.toString(),"0.1")
//				.buildAlgorithm();
//		algorithm.setMaxIterations(5000);

//        VariationCoefficientTermination prematureAlgorithmTermination = new VariationCoefficientTermination(200, 0.001);
//        algorithm.setPrematureAlgorithmTermination(prematureAlgorithmTermination);
//        algorithm.addListener(prematureAlgorithmTermination);
        algorithm.addListener(new AlgorithmSearchProgressChartListener("output/progress.png"));

        //search
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

        //this is just to ensure that solution meet the above constraints
        validateSolution(Solutions.bestOf(solutions), bicycleMessengerProblem, nearestMessengers);

        SolutionPrinter.print(bicycleMessengerProblem, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);

        //you may want to plot the problem
        Plotter plotter = new Plotter(bicycleMessengerProblem);
//		plotter.setBoundingBox(10000, 47500, 20000, 67500);
        plotter.plotShipments(true);
        plotter.plot("output/bicycleMessengerProblem.png", "bicycleMessenger");

        //and the problem as well as the solution
        Plotter plotter1 = new Plotter(bicycleMessengerProblem, Solutions.bestOf(solutions));
        plotter1.setLabel(Plotter.Label.ID);
        plotter1.plotShipments(false);
//		plotter1.setBoundingBox(5000, 45500, 25000, 66500);
        plotter1.plot("output/bicycleMessengerSolution.png", "bicycleMessenger");

        //and write out your solution in xml
//		new VrpXMLWriter(bicycleMessengerProblem, solutions).write("output/bicycleMessenger.xml");


        new GraphStreamViewer(bicycleMessengerProblem).labelWith(Label.ID).setRenderShipments(true).setRenderDelay(150).display();
//
        new GraphStreamViewer(bicycleMessengerProblem, Solutions.bestOf(solutions)).setGraphStreamFrameScalingFactor(1.5).setCameraView(12500, 55000, 0.25).labelWith(Label.ACTIVITY).setRenderShipments(true).setRenderDelay(150).display();

    }

    //验证最终路径是否满足硬约束条件。
    //if you wanne run this enable assertion by putting an '-ea' in your vmargument list - Run As --> Run Configurations --> (x)=Arguments --> VM arguments: -ea
    private static void validateSolution(VehicleRoutingProblemSolution bestOf, VehicleRoutingProblem bicycleMessengerProblem, Map<String, Double> nearestMessengers) {
        for (VehicleRoute route : bestOf.getRoutes()) {
            for (TourActivity act : route.getActivities()) {
                if (act.getArrTime() > 3 * nearestMessengers.get(((JobActivity) act).getJob().getId())) {
                    SolutionPrinter.print(bicycleMessengerProblem, bestOf, SolutionPrinter.Print.VERBOSE);
                    throw new IllegalStateException("three times less than ... constraint broken. this must not be. act.getArrTime(): " + act.getArrTime() + " allowed: " + 3 * nearestMessengers.get(((JobActivity) act).getJob().getId()));
                }
            }
        }
    }
    //计算每个信封由最近信使送达的时间。
    static Map<String, Double> getNearestMessengers(VehicleRoutingTransportCosts routingCosts, Collection<Job> envelopes, Collection<Vehicle> messengers) {
        Map<String, Double> nearestMessengers = new HashMap<String, Double>();
        for (Job envelope : envelopes) {
            double minDirect = Double.MAX_VALUE;
            for (Vehicle m : messengers) {
                double direct = getTimeOfDirectRoute(envelope, m, routingCosts);
                if (direct < minDirect) {
                    minDirect = direct;
                }
            }
            nearestMessengers.put(envelope.getId(), minDirect);
        }
        return nearestMessengers;
    }

    //计算信使到信封起点再到终点的最短时间。
    static double getTimeOfDirectRoute(Job job, Vehicle v, VehicleRoutingTransportCosts routingCosts) {
        Shipment envelope = (Shipment) job;
        return routingCosts.getTransportTime(v.getStartLocation(), envelope.getPickupLocation(), 0.0, DriverImpl.noDriver(), v) +
                routingCosts.getTransportTime(envelope.getPickupLocation(), envelope.getDeliveryLocation(), 0.0, DriverImpl.noDriver(), v);
    }
    //从文件中读取信封数据并构建为 Shipment 对象。
    private static void readEnvelopes(Builder problemBuilder) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File("input/bicycle_messenger_demand.txt")));
        String line;
        boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if (firstLine) {
                firstLine = false;
                continue;
            }
            String[] tokens = line.split("\\s+");
            //define your envelope which is basically a shipment from A to B
            Shipment envelope = Shipment.Builder.newInstance(tokens[1]).addSizeDimension(0, 1)
                    .setPickupLocation(Location.Builder.newInstance().setCoordinate(Coordinate.newInstance(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]))).build())
                    .setDeliveryLocation(Location.Builder.newInstance().setCoordinate(Coordinate.newInstance(Double.parseDouble(tokens[4]), Double.parseDouble(tokens[5]))).build()).build();
            problemBuilder.addJob(envelope);
        }
        reader.close();
    }
    //从文件中读取信使信息并构建为 Vehicle 对象。
    private static void readMessengers(Builder problemBuilder) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File("input/bicycle_messenger_supply.txt")));
        String line;
        boolean firstLine = true;
        VehicleType messengerType = VehicleTypeImpl.Builder.newInstance("messengerType").addCapacityDimension(0, 15).setCostPerDistance(1).build();
        /*
         * the algo requires some time and space to search for a valid solution. if you ommit a penalty-type, it probably throws an Exception once it cannot insert an envelope anymore
         * thus, give it space by defining a penalty/shadow vehicle with higher variable and fixed costs to up the pressure to find solutions without penalty type
         *
         * it is important to give it the same typeId as the type you want to shadow
         */
        while ((line = reader.readLine()) != null) {
            if (firstLine) {
                firstLine = false;
                continue;
            }
            String[] tokens = line.split("\\s+");
//            build your vehicle
            VehicleImpl vehicle = VehicleImpl.Builder.newInstance(tokens[1])
                    .setStartLocation(Location.Builder.newInstance().setCoordinate(Coordinate.newInstance(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]))).build())
                    .setReturnToDepot(false).setType(messengerType).build();
            problemBuilder.addVehicle(vehicle);
        }
        reader.close();
    }

}

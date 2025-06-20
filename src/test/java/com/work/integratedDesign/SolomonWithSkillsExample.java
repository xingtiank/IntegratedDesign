package com.work.integratedDesign;

import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.instance.reader.SolomonReader;
import com.graphhopper.jsprit.io.problem.VrpXMLWriter;

import java.util.Collection;
/**
 * 带技能约束的 Solomon 车辆路径问题（Vehicle Routing Problem with Skills）
 * 如何为不同车辆和任务配置“技能（Skills）”，以实现对某些特定任务只能由具备相应技能的车辆来完成。
 */
public class SolomonWithSkillsExample {

    public static void main(String[] args) {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new SolomonReader(vrpBuilder).read("input/C101_solomon.txt");
        VehicleRoutingProblem vrp = vrpBuilder.build();

        //y >= 50 skill1 otherwise skill2
        //two vehicles: v1 - skill1 #5; v2 - skill2 #6
        Vehicle solomonVehicle = vrp.getVehicles().iterator().next();
        VehicleType newType = solomonVehicle.getType();
        VehicleRoutingProblem.Builder skillProblemBuilder = VehicleRoutingProblem.Builder.newInstance();
        for (int i = 0; i < 5; i++) {
            VehicleImpl skill1Vehicle = VehicleImpl.Builder.newInstance("skill1_vehicle_" + i).addSkill("skill1")
                    .setStartLocation(Location.Builder.newInstance().setId(solomonVehicle.getStartLocation().getId()).setCoordinate(solomonVehicle.getStartLocation().getCoordinate()).build())
                    .setEarliestStart(solomonVehicle.getEarliestDeparture())
                    .setType(newType).build();
            VehicleImpl skill2Vehicle = VehicleImpl.Builder.newInstance("skill2_vehicle_" + i).addSkill("skill2")
                    .setStartLocation(Location.Builder.newInstance().setId(solomonVehicle.getStartLocation().getId())
                            .setCoordinate(solomonVehicle.getStartLocation().getCoordinate()).build())
                    .setEarliestStart(solomonVehicle.getEarliestDeparture())
                    .setType(newType).build();
            skillProblemBuilder.addVehicle(skill1Vehicle).addVehicle(skill2Vehicle);
        }
        for (Job job : vrp.getJobs().values()) {
            Service service = (Service) job;
            Service.Builder skillServiceBuilder;
            if (service.getLocation().getCoordinate().getY() < 50.) {
                skillServiceBuilder = Service.Builder.newInstance(service.getId() + "_skill2").setServiceTime(service.getServiceDuration())
                        .setLocation(Location.Builder.newInstance().setId(service.getLocation().getId())
                                .setCoordinate(service.getLocation().getCoordinate()).build()).setTimeWindow(service.getTimeWindow())
                        .addSizeDimension(0, service.getSize().get(0));
                skillServiceBuilder.addRequiredSkill("skill2");
            } else {
                skillServiceBuilder = Service.Builder.newInstance(service.getId() + "_skill1").setServiceTime(service.getServiceDuration())
                        .setLocation(
                                Location.Builder.newInstance().setId(service.getLocation().getId())
                                        .setCoordinate(service.getLocation().getCoordinate()).build()
                        ).setTimeWindow(service.getTimeWindow())
                        .addSizeDimension(0, service.getSize().get(0));
                skillServiceBuilder.addRequiredSkill("skill1");
            }
            skillProblemBuilder.addJob(skillServiceBuilder.build());
        }
        skillProblemBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
        VehicleRoutingProblem skillProblem = skillProblemBuilder.build();

        VehicleRoutingAlgorithm vra = Jsprit.Builder.newInstance(skillProblem).buildAlgorithm();

        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
        VehicleRoutingProblemSolution solution = Solutions.bestOf(solutions);

        SolutionPrinter.print(skillProblem, solution, SolutionPrinter.Print.VERBOSE);

        new Plotter(skillProblem, solution).plot("output/skill_solution", "solomon_with_skills");

        new VrpXMLWriter(skillProblem, solutions).write("output/solomon_with_skills");
    }
}

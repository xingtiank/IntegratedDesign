package com.work.integratedDesign.controller;


import com.work.integratedDesign.pojo.*;
import com.work.integratedDesign.service.VRPService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.List;

@RestController
public class VRP {
    @Resource
    private VRPService vrpService;

    @PostMapping("/VRP")
    public JsonResult<List<OnePath>> getVRP(@RequestBody VrpRequest request) {
        ArrayList<Task> tasks = request.getTasks();
//        tasks.forEach(System.out::println);
        ArrayList<DetailedVehicle> detailedVehicles = request.getDetailedVehicles();
//        detailedVehicles.forEach(System.out::println);
        return new JsonResult<>(vrpService.VRP(tasks, detailedVehicles));
    }
}

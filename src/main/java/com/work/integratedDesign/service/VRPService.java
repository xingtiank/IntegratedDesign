package com.work.integratedDesign.service;


import com.work.integratedDesign.pojo.DetailedVehicle;
import com.work.integratedDesign.pojo.OnePath;
import com.work.integratedDesign.pojo.Task;

import java.util.List;

public interface VRPService {
     int WEIGHT_INDEX = 0;
     int VOLUME_INDEX = 1;
     int SHIPMENT_COUNT_INDEX = 2;
//    List<OnePath> VRPWithOneVehicleType();
    List<OnePath> VRP(List<Task> tasks, List<DetailedVehicle> detailedVehicles);

}

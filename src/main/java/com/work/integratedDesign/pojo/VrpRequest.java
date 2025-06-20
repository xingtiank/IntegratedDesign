package com.work.integratedDesign.pojo;


import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;

@Getter
@Setter
public class VrpRequest {


    private ArrayList<Task> tasks;


    private ArrayList<DetailedVehicle> detailedVehicles;
}
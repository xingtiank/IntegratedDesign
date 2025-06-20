package com.work.integratedDesign.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class DetailedVehicle {
    private String vehicleTypeId;
    private Integer numberOfVehicle;
    private Double depotLongitude;
    private Double depotLatitude;
    private Boolean isReturnToDepot;
}

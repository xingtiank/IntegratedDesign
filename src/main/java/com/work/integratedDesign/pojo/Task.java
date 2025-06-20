package com.work.integratedDesign.pojo;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Task {
    private String taskType;//任务类型,service/shipment
    private Integer weight;
    private Integer volume;

    private Double pickUpLongitude;
    private Double pickUpLatitude;
    private Double deliveryLongitude;
    private Double deliveryLatitude;//shipment

    private Boolean withTW;
    private Double pickUpTimeWindowStart;
    private Double pickUpTimeWindowEnd;
    private Double deliveryTimeWindowStart;
    private Double deliveryTimeWindowEnd;

    private Integer priority;//优先级,默认为0
}

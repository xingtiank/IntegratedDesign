package com.work.integratedDesign.config;

import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 车辆类型配置
 */
@Configuration
public class VehicleTypeConfig {

    final int WEIGHT_INDEX = 0;
    final int VOLUME_INDEX = 1;
    final int SHIPMENT_COUNT_INDEX = 2;
    @Bean
    public VehicleType VehicleType_1() {
        return VehicleTypeImpl.Builder.newInstance("small_van")
                .addCapacityDimension(WEIGHT_INDEX, 2)   // 重量上限为2
                .addCapacityDimension(VOLUME_INDEX, 6)// 体积上限为6
                .addCapacityDimension(SHIPMENT_COUNT_INDEX, 14)// 任务数量上限为14
                .setCostPerDistance(10000)
                .setFixedCost(10000)
                .setMaxVelocity(1)
                .build();
    }

    @Bean
    public VehicleType VehicleType_2() {
        return VehicleTypeImpl.Builder.newInstance("medium_truck")
                .addCapacityDimension(WEIGHT_INDEX, 10)
                .addCapacityDimension(VOLUME_INDEX, 15)
                .addCapacityDimension(SHIPMENT_COUNT_INDEX, 14)
                .setCostPerDistance(15000)
                .setFixedCost(20000)
                .setMaxVelocity(1.2)
                .build();
    }

    @Bean
    public VehicleType VehicleType_3() {
        return VehicleTypeImpl.Builder.newInstance("large_truck")
                .addCapacityDimension(WEIGHT_INDEX, 10)
                .addCapacityDimension(VOLUME_INDEX, 30)
                .addCapacityDimension(SHIPMENT_COUNT_INDEX, 14)
                .setCostPerDistance(20000)
                .setFixedCost(30000)
                .setMaxVelocity(1.4)
                .build();
    }
    @Bean
    public VehicleType VehicleType_4() {
        return VehicleTypeImpl.Builder.newInstance("compact_van")
                .addCapacityDimension(WEIGHT_INDEX, 10)
                .addCapacityDimension(VOLUME_INDEX, 20)
                .addCapacityDimension(SHIPMENT_COUNT_INDEX, 14)
                .setCostPerDistance(15000)
                .setFixedCost(25000)
                .setMaxVelocity(1)
                .build();
    }

    @Bean
    public VehicleType VehicleType_5() {
        return VehicleTypeImpl.Builder.newInstance("heavy_duty_truck")
                .addCapacityDimension(WEIGHT_INDEX, 30)
                .addCapacityDimension(VOLUME_INDEX, 80)
                .addCapacityDimension(SHIPMENT_COUNT_INDEX, 14)
                .setCostPerDistance(30000)
                .setFixedCost(50000)
                .setMaxVelocity(1.6)
                .build();
    }
    @Bean
    public VehicleType VehicleType_6() {
        return VehicleTypeImpl.Builder.newInstance("pickup_truck")
                .addCapacityDimension(WEIGHT_INDEX, 30)
                .addCapacityDimension(VOLUME_INDEX, 50)
                .addCapacityDimension(SHIPMENT_COUNT_INDEX, 14)
                .setCostPerDistance(25000)
                .setFixedCost(40000)
                .setMaxVelocity(1.1)
                .build();
    }

    @Bean
    public Map<String, VehicleType> vehicleTypeMap() {
        return Map.of(
                "small_van", VehicleType_1(),
                "medium_truck", VehicleType_2(),
                "large_truck", VehicleType_3(),
                "compact_van", VehicleType_4(),
                "heavy_duty_truck", VehicleType_5(),
                "pickup_truck", VehicleType_6()
        );
    }
}


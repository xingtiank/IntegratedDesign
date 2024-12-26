package com.work.integratedDesign.pojo.Goods;

import com.work.integratedDesign.Utility.RandomUtils;
import com.work.integratedDesign.pojo.Place;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
public class Gasoline extends Goods {
    private String fuelGrade; // 燃料等级


    public Gasoline(double volume,Place origin, Place destination) {
        super(volume,  origin, destination);
        this.fuelGrade = RandomUtils.getRandomEnum(FuelGrade.class).toString();
    }

    private enum FuelGrade {
        E89, E92, E95, E98
    }
}
package com.work.integratedDesign.pojo.Goods;

import com.work.integratedDesign.Utility.RandomUtils;
import com.work.integratedDesign.pojo.Place;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
public class Machinery extends Goods {
    private String purpose;
    private String model;

    // 构造函数
    public Machinery(double volume,Place origin, Place destination){
        super(volume, origin, destination);
        this.purpose = RandomUtils.getRandomEnum(Purpose.class).toString();
        this.model = RandomUtils.getRandomEnum(Model.class).toString();
    }


    public enum Purpose {
        Construction,
        Manufacturing,
        Transportation,
        IndustrialProcessing,
        Agricultural,
        Others
    }

    public enum Model {
        Small,
        Medium,
        Large,
        Huge,
        Others
    }
}

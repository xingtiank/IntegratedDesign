package com.work.integratedDesign.pojo.Goods;

import com.work.integratedDesign.pojo.Place;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.work.integratedDesign.Utility.RandomUtils;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
public class Express extends Goods{
    private Integer quantity;         // 数量
    private String companyName;

    public Express(double volume,  Place origin, Place destination, int quantity) {
        super(volume,  origin, destination);
        this.quantity = quantity;
        this.companyName = String.valueOf(RandomUtils.getRandomEnum(CompanyType.class));
    }


    private enum CompanyType{
        DHL,
        FedEx,
        UPS,
        USPS,
        DPD,
        FastWay,
        Aramex,
        TNT
    }
}


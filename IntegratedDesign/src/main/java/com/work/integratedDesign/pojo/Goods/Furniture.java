package com.work.integratedDesign.pojo.Goods;

import com.work.integratedDesign.Utility.RandomUtils;
import com.work.integratedDesign.pojo.Place;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
public class Furniture extends Goods {
    private String material; // 材质
    private String dimensions; // 尺寸

    public Furniture(double volume, Place origin, Place destination) {
        super(volume,  origin, destination);
        this.material = RandomUtils.getRandomEnum(Materials.class).toString();
        this.dimensions = RandomUtils.getRandomEnum(Dimensions.class).toString();
    }


    private enum Materials {
        Wood,
        Metal,
        Plastic,
        Glass,
        Ceramic,
        Stone,
        Leather,
        Fiber,
        Bamboo,
        Paper,
        Cardboard,
        Textile,
        Fabric,
        Silk,
        Wool,
        Cotton,
        Linen,
        Synthetic,
        Nylon,
        Polyester,
        Acrylic,
        Vinyl,
        PVC,
        Teflon
    }
    private enum Dimensions {
        Small,
        Medium,
        Large,
        ExtraLarge
    }
}


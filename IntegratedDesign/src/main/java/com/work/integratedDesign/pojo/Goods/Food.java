package com.work.integratedDesign.pojo.Goods;

import com.work.integratedDesign.Utility.RandomUtils;
import com.work.integratedDesign.pojo.Place;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
public class Food extends Goods {
    private String type;
    private String storageCondition;

    // 构造函数
    public Food(double volume, Place origin, Place destination) {
        super(volume, origin, destination);
        this.type = RandomUtils.getRandomEnum(Type.class).toString();
        this.storageCondition = RandomUtils.getRandomEnum(StorageCondition.class).toString();
    }

    private enum Type {
        Meat,
        Fish,
        Vegetables,
        Fruits,
        Dairy,
        Bread,
        Snacks,
        CannedGoods,
        Beverages,
        Others
    }
    private enum StorageCondition {
        Refrigeration,
        Freezing,
        Ambient,
        Others
    }
}

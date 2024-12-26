package com.work.integratedDesign.pojo.Goods;

import com.work.integratedDesign.Utility.RandomUtils;
import com.work.integratedDesign.pojo.Place;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class BuildingMaterial extends Goods {
    private String characteristics; // 特性
    private String material;        // 材质
    private String purpose;         // 用途

    public BuildingMaterial(double volume, Place origin, Place destination) {
        super(volume,  origin, destination);
        this.characteristics = RandomUtils.getRandomEnum(CharacteristicsType.class).toString();
        this.material = RandomUtils.getRandomEnum(MaterialType.class).toString();
        this.purpose = RandomUtils.getRandomEnum(PurposeType.class).toString();
    }

    private enum MaterialType {
        Concrete,
        Steel,
        Wood,
        Plastic,
        Glass,
        Ceramic,
        Metal,
        Stone,
        Brick,
        Plywood,
        Fiberglass,
        Tiles,
        Cement,
        Sand,
        Gravel,
        Sandstone,
        Limestone,
        Marble,
        Basalt,
        Quartz,
        Clay,
        Soil,
        Silt
    }
    private enum PurposeType {
        Construction,
        Decoration,
        Architecture,
        Interior,
        Exterior,
        Furniture,
        Building,
        Machinery,
        Electrical,
        Plumbing,
        Sanitary,
        Hydraulic,
        Mechanical,
        Thermal,
        Aerospace,
        Naval,
        Agricultural,
        Forestry,
        Mining,
        Oil,
        Gas,
        Water
    }
    private enum CharacteristicsType {
        Strong,
        Weak,
        Flexible,
        Resilient,
        Durable,
        Fragile,
        Smooth,
        Rough,
        Shiny,
        Matte,
        Glossy,
        Translucent,
        Transparent,
        Opaque,
        Colored,
        Uncolored,
        Powdery,
        Solid,
        Liquid,
        Gas,
        Vapor
    }
}

package com.work.integratedDesign.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("e_poi")
public class Place {
    @TableId
    private Integer id;
    @TableField(value = "poi_name")
    private String name;
    @TableField(value = "poi_lat")
    private double latitude;
    @TableField(value = "poi_lon")
    private double longitude;
    @TableField(value = "poi_type")
    private String type;
    @TableField(value = "poi_status")
    private Integer status;
    private Date createTime;
}

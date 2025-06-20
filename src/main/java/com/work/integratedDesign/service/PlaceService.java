package com.work.integratedDesign.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.work.integratedDesign.pojo.Place;

import java.util.List;


public interface PlaceService extends IService<Place> {
//    Integer POI_NUMBER = 534;
    Integer POI_NUMBER = 5;  // 测试数据
    Place getRandomPlace();
    List<Place> listPlace();
}


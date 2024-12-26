package com.work.integratedDesign.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.work.integratedDesign.pojo.Place;




public interface PlaceService extends IService<Place> {

    Place getRandomPlace();
}


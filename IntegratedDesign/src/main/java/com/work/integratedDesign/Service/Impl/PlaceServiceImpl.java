package com.work.integratedDesign.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.integratedDesign.Mapper.PlaceMapper;
import com.work.integratedDesign.Service.PlaceService;
import com.work.integratedDesign.Utility.RandomUtils;
import com.work.integratedDesign.pojo.Place;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class PlaceServiceImpl extends ServiceImpl<PlaceMapper,Place> implements PlaceService  {

    @Override
    public Place getRandomPlace() {
        List<Place> places = list();
        if (!places.isEmpty()) {
            return places.get(RandomUtils.getRandomNumber(0, places.size() - 1));
        }
        return null;
    }
}

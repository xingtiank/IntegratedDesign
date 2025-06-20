package com.work.integratedDesign.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.integratedDesign.mapper.PlaceMapper;
import com.work.integratedDesign.service.PlaceService;
import com.work.integratedDesign.utility.RandomUtils;
import com.work.integratedDesign.pojo.Place;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PlaceServiceImpl extends ServiceImpl<PlaceMapper,Place> implements PlaceService  {

    @Override
    public Place getRandomPlace() {
        return getById(RandomUtils.getRandomNumber(1, POI_NUMBER));
    }

    @Override
    public List<Place> listPlace() {
        return this.list();
    }
}

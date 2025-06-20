package com.work.integratedDesign.service.Impl;

import com.work.integratedDesign.pojo.Goods;
import com.work.integratedDesign.service.GenerateGoodsService;
import com.work.integratedDesign.service.PlaceService;
import com.work.integratedDesign.service.PathRetrievalService;
import com.work.integratedDesign.utility.RandomUtils;
import com.work.integratedDesign.pojo.Place;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;



@Service
public class GenerateGoodsServiceImpl implements GenerateGoodsService {
    @Resource
    private PlaceService placeService;
    @Resource
    private PathRetrievalService pathRetrievalService;

    public Goods generateGoods() {
        Place origin, destination;
        //保证两点不同
        do {
            origin = placeService.getRandomPlace();
            destination = placeService.getRandomPlace();
        } while (origin.equals(destination));

        Goods goods = new Goods(RandomUtils.getRandomNumber(3, 40), origin, destination);
        return f_setPolyline(goods);
    }
    //设置polyline
    private  Goods f_setPolyline(Goods goods) {
        goods.setPolyline(pathRetrievalService.getPolyline(goods.getOrigin().getLatitude(), goods.getOrigin().getLongitude(),
                goods.getDestination().getLatitude(), goods.getDestination().getLongitude()));
        return goods;
    }


}



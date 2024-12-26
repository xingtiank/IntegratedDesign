package com.work.integratedDesign.Service.Impl;

import com.work.integratedDesign.Service.Generate;
import com.work.integratedDesign.Service.PlaceService;
import com.work.integratedDesign.Utility.RandomUtils;
import com.work.integratedDesign.pojo.Goods.*;
import com.work.integratedDesign.pojo.Place;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;



@Service
public class GenerateImpl implements Generate {
    @Resource
    private PlaceService placeService;

    RestTemplateImpl restTemplateImpl = new RestTemplateImpl();

    public Goods generateGoods() {
        int random = (int) (Math.random() * 6);
        Place origin, destination;
        //保证两点不同
        do {
            origin = placeService.getRandomPlace();
            destination = placeService.getRandomPlace();
        } while (origin.equals(destination));

        switch (random) {
            case 0: {
                Goods food = new Food(RandomUtils.getRandomNumber(3, 40), origin, destination);
                food.setPolyline(restTemplateImpl.getPolyline(food.getOrigin().getLatitude(), food.getOrigin().getLongitude(), food.getDestination().getLatitude(), food.getDestination().getLongitude()));
                return food;
            }
            case 1: {
                Goods machinery = new Machinery(RandomUtils.getRandomNumber(3, 40), origin, destination);
                machinery.setPolyline(restTemplateImpl.getPolyline(machinery.getOrigin().getLatitude(), machinery.getOrigin().getLongitude(), machinery.getDestination().getLatitude(), machinery.getDestination().getLongitude()));
                return machinery;
            }
            case 2: {
                Goods buildingMaterial = new BuildingMaterial(RandomUtils.getRandomNumber(10, 40), origin, destination);
                buildingMaterial.setPolyline(restTemplateImpl.getPolyline(buildingMaterial.getOrigin().getLatitude(), buildingMaterial.getOrigin().getLongitude(), buildingMaterial.getDestination().getLatitude(), buildingMaterial.getDestination().getLongitude()));
                return buildingMaterial;
            }
            case 3: {
                Goods furniture = new Furniture(RandomUtils.getRandomNumber(3, 40), origin, destination);
                furniture.setPolyline(restTemplateImpl.getPolyline(furniture.getOrigin().getLatitude(), furniture.getOrigin().getLongitude(), furniture.getDestination().getLatitude(), furniture.getDestination().getLongitude()));
                return furniture;
            }
            case 4: {
                Goods gasoline = new Gasoline(RandomUtils.getRandomNumber(3, 40), origin, destination);
                gasoline.setPolyline(restTemplateImpl.getPolyline(gasoline.getOrigin().getLatitude(), gasoline.getOrigin().getLongitude(), gasoline.getDestination().getLatitude(), gasoline.getDestination().getLongitude()));
                return gasoline;
            }
            case 5: {
                Goods express = new Express(RandomUtils.getRandomNumber(3, 40), origin, destination, RandomUtils.getRandomNumber(100, 300));
                express.setPolyline(restTemplateImpl.getPolyline(express.getOrigin().getLatitude(), express.getOrigin().getLongitude(), express.getDestination().getLatitude(), express.getDestination().getLongitude()));
                return express;
            }
            default:
                return null;
        }
    }



}



package com.work.integratedDesign.controller;


import com.work.integratedDesign.pojo.JsonResult;
import com.work.integratedDesign.pojo.Place;
import com.work.integratedDesign.service.PlaceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


//查询所有地点
@RestController
public class POIController {
    @Resource
    private PlaceService placeService;

    @GetMapping("/poi")
    public JsonResult<List<Place>> selectLatitudeLongitudeType() {
        List<Place> result = placeService.listPlace();
        if (result.isEmpty()) {
            return new JsonResult<>("未找到相关记录", null);
        }
        return new JsonResult<>("查询成功", result);
    }
}

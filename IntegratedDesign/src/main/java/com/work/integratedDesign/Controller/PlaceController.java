package com.work.integratedDesign.Controller;


import com.work.integratedDesign.Service.Generate;
import com.work.integratedDesign.Service.PlaceService;
import com.work.integratedDesign.pojo.Goods.Goods;
import com.work.integratedDesign.pojo.JsonResult;
import com.work.integratedDesign.pojo.Place;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class PlaceController {

    @Resource
    private PlaceService placeService;
    @Resource
    private Generate generate;


    @RequestMapping("/places")
    @ResponseBody
    public JsonResult<List<Place>> getPlaces() {
        List<Place> places = placeService.list();
        if (places == null) {
            return new JsonResult<>(404, "Not Found");
        }
        return new JsonResult<>(places);
    }

    @RequestMapping(value = "/place")
    @ResponseBody
    public JsonResult<Place> getPlace() {
        Place place = placeService.getRandomPlace();
        if (place == null) {
            return new JsonResult<>(404, "Not Found");
        }
        return new JsonResult<>(place);
    }

    @RequestMapping("/goods")
    @ResponseBody
    public JsonResult<Goods> generateGoods() {
        Goods goods = generate.generateGoods();
        return new JsonResult<>(goods);
    }

    @RequestMapping("/place/{id}")
    @ResponseBody
    public JsonResult<Place> getPlaceById(@PathVariable("id") Integer id) {
        Place place = placeService.getById(id);
        if (place == null) {
            return new JsonResult<>(404, "Not Found");
        }
        return new JsonResult<>(place);
    }





}


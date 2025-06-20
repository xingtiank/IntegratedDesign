package com.work.integratedDesign;


import com.work.integratedDesign.service.Impl.WebClientServiceImpl;
import com.work.integratedDesign.service.PathRetrievalService;
import com.work.integratedDesign.service.WebClientService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class WebClientTest {
    @Resource
    WebClientService webClientService;
    @Resource
    PathRetrievalService pathRetrievalService;
    @Test
    public void test() throws InterruptedException {
        webClientService.getDistanceData("116.481028,39.989643", "116.475579,39.952631").subscribe(data -> System.out.println("收到响应：" + data),
                error -> System.err.println("请求失败：" + error),
                () -> System.out.println("请求完成"));
        // 保持测试线程存活
        Thread.sleep(3000);
    }
    //途经点测试
    @Test
    public void test2() throws InterruptedException {
        webClientService.getDistanceData("116.481028,39.989643", "116.475579,39.952631",  "116.475670,39.952666;116.475500,39.952600;").subscribe(data -> System.out.println("收到响应：" + data),
                error -> System.err.println("请求失败：" + error),
                () -> System.out.println("请求完成"));
        // 保持测试线程存活
        Thread.sleep(3000);
    }
    @Test
    public void testBatchRequest() throws InterruptedException {
        List<WebClientServiceImpl.LocationPair> pairs = List.of(
                new WebClientServiceImpl.LocationPair("116.481028,39.989643", "116.475579,39.952631"),
                new WebClientServiceImpl.LocationPair("116.481028,39.989643", "116.475571,39.952631"),
                new WebClientServiceImpl.LocationPair("116.481028,39.989643", "116.475572,39.952631"),
                new WebClientServiceImpl.LocationPair("116.481028,39.989643", "116.475573,39.952631"),
                new WebClientServiceImpl.LocationPair("116.481028,39.989643", "116.475574,39.952631"),
                new WebClientServiceImpl.LocationPair("116.481028,39.989643", "116.475597,39.952631"),
                new WebClientServiceImpl.LocationPair("116.481028,39.989643", "116.475534,39.952631"),
                new WebClientServiceImpl.LocationPair("116.481028,39.989643", "116.475537,39.952631"),
                new WebClientServiceImpl.LocationPair("116.481028,39.989643", "116.475565,39.952631"),
                new WebClientServiceImpl.LocationPair("116.481028,39.989643", "116.475568,39.952631")
        );

        webClientService.getBatchDistances(pairs)
                .subscribe(
                        res -> System.out.println("收到响应：" + res.getRoute()),
                        err -> System.err.println("全局错误：" + err),
                        () -> System.out.println("所有请求处理完成")
                );

        Thread.sleep(10000); // 根据实际情况调整等待时间
    }
    @Test
    public void testRestTemplate() {
        double[][] polyline = pathRetrievalService.getPolyline(39.989643, 116.481028, 39.952631, 116.475579);
        for (double[] doubles : polyline) {
            System.out.println(Arrays.toString(doubles));
        }
    }
}

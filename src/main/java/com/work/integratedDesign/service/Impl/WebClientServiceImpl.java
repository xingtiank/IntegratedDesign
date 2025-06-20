package com.work.integratedDesign.service.Impl;

import com.work.integratedDesign.pojo.RouteResponse;
import com.work.integratedDesign.service.WebClientService;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
public class WebClientServiceImpl implements WebClientService {

    private final WebClient webClient;

    public WebClientServiceImpl() {
        this.webClient = WebClient.builder()
                .baseUrl("https://restapi.amap.com/v5/direction/driving?key=b37ebc0a0eb576a1cc6f432b2a5439f7")
                .build();
    }

    public Mono<RouteResponse> getDistanceData(String origin, String destination,String waypoints) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", "b37ebc0a0eb576a1cc6f432b2a5439f7")
                        .queryParam("origin", origin)
                        .queryParam("destination", destination)
                        .queryParam("strategy", "2")
                        .queryParam("show_fields", "cost,polyline")
                        .queryParam("waypoints", waypoints)
                        .build())
                .retrieve()
                .bodyToMono(RouteResponse.class);
    }

    public Mono<RouteResponse> getDistanceData(String origin, String destination) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", "b37ebc0a0eb576a1cc6f432b2a5439f7")
                        .queryParam("origin", origin)
                        .queryParam("destination", destination)
                        .queryParam("strategy", "2")
                        .queryParam("show_fields", "cost,polyline")
                        .build())
                .retrieve()
                .bodyToMono(RouteResponse.class);
    }


    public Flux<RouteResponse> getBatchDistances(List<LocationPair> locationPairs) {
        return Flux.fromIterable(locationPairs)
                .flatMap(pair ->
                        getDistanceData(pair.getOrigin(), pair.getDestination())
                                .onErrorResume(e -> {
                                    System.err.println("请求失败：" + pair);
                                    return Mono.empty(); // 忽略错误继续执行
                                })
                );
    }

    // 参数封装类
    @Data // Lombok
    @AllArgsConstructor
    public static class LocationPair {
        private String origin;
        private String destination;
    }

}


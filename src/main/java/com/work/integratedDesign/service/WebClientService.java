package com.work.integratedDesign.service;


import com.work.integratedDesign.pojo.RouteResponse;
import com.work.integratedDesign.service.Impl.WebClientServiceImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface WebClientService {
    Mono<RouteResponse> getDistanceData(String origin, String destination);
    Mono<RouteResponse> getDistanceData(String origin, String destination,  String waypoints);
    Flux<RouteResponse> getBatchDistances(List<WebClientServiceImpl.LocationPair> locationPairs);

}

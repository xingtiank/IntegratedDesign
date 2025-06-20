package com.work.integratedDesign.pojo;


import lombok.Data;

import java.util.List;


@Data
public class RouteResponse {
    private String status;
    private RouteData route;

    @Data
    public static class RouteData {
        private List<Path> paths;

        @Data
        public static class Path {
            private String distance;
            private Cost cost;
            private List<Step> steps;

            @Data
            public static class Step {
                private String polyline;
            }
            @Data
            public static class Cost {
                private String duration;
                private String tolls;
                private String traffic_lights;
            }
        }
    }
}

基于高德API的路径规划

采用自适应大领域搜索算法ALNS
使用了jsprit算法库

100个服务点，1辆车仅指定起终点，迭代20000次，时间就需要两分钟
使用高德地图获取polyline时，每秒只允许3个并发
LIFO约束不具备普适性，在大多数情况下，可以通过其他方法避免
另外，具备LIFO约束的情况下，一般都是在仓库通过分拣解决
所以，几乎不存在即具有带取送货，又具备LIFO约束的场景

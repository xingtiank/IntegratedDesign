package com.work.integratedDesign.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//public class StateTransitionTable {
//
//    private static final Map<String, Map<String, Double>> transitionTable;
//    private static final Random random = new Random();
//
//    // 初始化状态转换表
//    static {
//        transitionTable = new HashMap<>();
//        Map<String, Double> pickingUpTransition = new HashMap<>();
//        pickingUpTransition.put("pickingUp", 0.05);
//        pickingUpTransition.put("loading", 0.9);
//        pickingUpTransition.put("waiting", 0.05);
//        transitionTable.put("pickingUp", pickingUpTransition);
//
//        Map<String, Double> loadingTransition = new HashMap<>();
//        loadingTransition.put("loading", 0.95);
//        loadingTransition.put("unloading", 0.02);
//        loadingTransition.put("waiting", 0.02);
//        loadingTransition.put("pickingUp", 0.01);
//        transitionTable.put("loading", loadingTransition);
//
//        Map<String, Double> transportingTransition = new HashMap<>();
//        transportingTransition.put("transporting", 0.95);
//        transportingTransition.put("unloading", 0.01);
//        transportingTransition.put("waiting", 0.04);
//        transportingTransition.put("pickingUp", 0.0);
//        transitionTable.put("transporting", transportingTransition);
//
//        Map<String, Double> unloadingTransition = new HashMap<>();
//        unloadingTransition.put("unloading", 0.95);
//        unloadingTransition.put("transporting", 0.03);
//        unloadingTransition.put("waiting", 0.02);
//        unloadingTransition.put("pickingUp", 0.0);
//        transitionTable.put("unloading", unloadingTransition);
//
//        Map<String, Double> waitingTransition = new HashMap<>();
//        waitingTransition.put("waiting", 0.15);
//        waitingTransition.put("pickingUp", 0.05);
//        waitingTransition.put("loading", 0.01);
//        waitingTransition.put("transporting", 0.10);
//        waitingTransition.put("idle", 0.05);
//        transitionTable.put("waiting", waitingTransition);
//
//        Map<String, Double> idleTransition = new HashMap<>();
//        idleTransition.put("idle", 0.85);
//        idleTransition.put("pickingUp", 0.0);
//        idleTransition.put("loading", 0.0);
//        idleTransition.put("transporting", 0.05);
//        transitionTable.put("idle", idleTransition);
//    }
//
//    // 私有构造函数，防止实例化
//    private StateTransitionTable() {
//        throw new UnsupportedOperationException("Utility class should not be instantiated");
//    }
//
//    // 根据当前状态和随机数，返回下一个状态
//    public static String getNextState(String currentState) {
//        Map<String, Double> transitions = transitionTable.get(currentState);
//        if (transitions == null || transitions.isEmpty()) {
//            // 如果当前状态不在转换表中，返回默认状态（如 "idle"）
//            System.err.println("Current state not found in transition table: " + currentState);
//            return "idle"; // 或者抛出异常
//        }
//
//        double randomValue = random.nextDouble();
//
//        double cumulativeProbability = 0.0;
//        for (Map.Entry<String, Double> entry : transitions.entrySet()) {
//            cumulativeProbability += entry.getValue();
//            if (randomValue < cumulativeProbability) {
//                return entry.getKey();
//            }
//        }
//        // 如果累积概率和小于 1，返回默认状态（如 "idle"）
//        System.err.println("Cumulative probability less than 1 for state: " + currentState);
//        return "idle"; // 或者抛出异常
//    }
//}
package com.work.integratedDesign.pojo;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StateTransitionTable {
    private Map<String, Map<String, Double>> transitionTable;

    public StateTransitionTable() {
        transitionTable = new HashMap<>();

        // 初始化状态转换表
        Map<String, Double> pickingUpTransition = new HashMap<>();
        pickingUpTransition.put("pickingUp", 0.05);
        pickingUpTransition.put("loading", 0.9);
        pickingUpTransition.put("waiting", 0.05);
        transitionTable.put("pickingUp", pickingUpTransition);

        Map<String, Double> loadingTransition = new HashMap<>();
        loadingTransition.put("loading", 0.95);
        loadingTransition.put("unloading", 0.02);
        loadingTransition.put("waiting", 0.02);
        loadingTransition.put("pickingUp", 0.01);
        transitionTable.put("loading", loadingTransition);

        Map<String, Double> transportingTransition = new HashMap<>();
        transportingTransition.put("transporting", 0.95);
        transportingTransition.put("unloading", 0.01);
        transportingTransition.put("waiting", 0.04);
        transportingTransition.put("pickingUp", 0.0);
        transitionTable.put("transporting", transportingTransition);

        Map<String, Double> unloadingTransition = new HashMap<>();
        unloadingTransition.put("unloading", 0.95);
        unloadingTransition.put("transporting", 0.03);
        unloadingTransition.put("waiting", 0.02);
        unloadingTransition.put("pickingUp", 0.0);
        transitionTable.put("unloading", unloadingTransition);

        Map<String, Double> waitingTransition = new HashMap<>();
        waitingTransition.put("waiting", 0.15);
        waitingTransition.put("pickingUp", 0.05);
        waitingTransition.put("loading", 0.01);
        waitingTransition.put("transporting", 0.10);
        waitingTransition.put("idle", 0.05);
        transitionTable.put("waiting", waitingTransition);

        Map<String, Double> idleTransition = new HashMap<>();
        idleTransition.put("idle", 0.85);
        idleTransition.put("pickingUp", 0.0);
        idleTransition.put("loading", 0.0);
        idleTransition.put("transporting", 0.05);
        transitionTable.put("idle", idleTransition);
    }

    public String getNextState(String currentState) {
        Map<String, Double> transitions = transitionTable.get(currentState);
        Random random = new Random();
        double randomValue = random.nextDouble();

        double cumulativeProbability = 0.0;
        for (Map.Entry<String, Double> entry : transitions.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (randomValue < cumulativeProbability) {
                return entry.getKey();
            }
        }
        return null; // 理论上不会到达这里
    }
}
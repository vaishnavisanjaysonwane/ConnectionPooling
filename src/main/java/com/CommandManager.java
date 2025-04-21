package com;

import com.unused.HSMConnectionExecutionResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandManager {

    AtomicInteger counter = new AtomicInteger(0);

    Integer hsmId;

    public HSMConnectionExecutionResponse sendCommand(Object command, Map<String, List<ConnectionPool>> allPoolsForHSM, Integer retryCount) {
        if(counter.incrementAndGet() <= retryCount){
            List<ConnectionPool> pools = allPoolsForHSM.get(command.toString());
            for (ConnectionPool pool : pools) {

            }
        }
        return null;
    }

    public void setRklMode(String rklMode) {
    }
}

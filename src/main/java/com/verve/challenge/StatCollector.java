package com.verve.challenge;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * StatCollector class is used to collect statistics of the requests processed by the server.
 * It collects the statistics in a cycle and prints the total number of requests processed by the server.
 * The run method of the class should be invoked to start a new cycle of collecting statistics.
 */
public class StatCollector implements Runnable {
    private Statistics statCollector = new Statistics();
    private final static StatCollector INSTANCE = new StatCollector();
    private static final Logger logger = Logger.getLogger( StatCollector.class.getName() );
    private static int cycle = 0;

    private StatCollector(){

    }
    public static StatCollector getInstance(){
        return INSTANCE;
    }
    
    @Override
    public void run() {
        int totalRequestProcessed = statCollector.getTotalRequestsProcessed();
        if (cycle == 0){
            logger.info("Starting collecting statistics...");
        }else{
            statCollector = new Statistics();
            logger.log(Level.INFO, "Total request processed in cycle {0} : {1}", new Object[]{cycle, totalRequestProcessed});
        }
        cycle++;
    }

    public int notifyNewRequest(int id) {
        return statCollector.notifyRequestProcessed(id);
    }
    
    private static class Statistics {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private final Set<Integer> ids = ConcurrentHashMap.newKeySet();
        private Statistics(){

        }
        int notifyRequestProcessed(Integer id){
            if (ids.add(id)){
                return requestCount.incrementAndGet();
            }
            return requestCount.get();
        }
        private int getTotalRequestsProcessed(){
            return requestCount.get();
        }
    }
}

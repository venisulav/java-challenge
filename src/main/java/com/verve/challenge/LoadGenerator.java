package com.verve.challenge;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * LoadGenerator class is used to generate load on the server by sending multiple requests to the server.
 * Each request is send to the server with a unique id.
 */
public class LoadGenerator {
    // AtomicInteger to keep track of the number of requests sent to the server.
    static final AtomicInteger idCount = new AtomicInteger( 0);

    private static final Logger logger = Logger.getLogger( LoadGenerator.class.getName() );

    private static String endpoint;

    public static void runGenerator(int port, int nThreads) throws Exception, InterruptedException{
        endpoint = String.format("http://localhost:%d/api/verve/accept",port);
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        Future<?>[] results = new Future<?>[nThreads];
        for(int i=0;i<nThreads;i++){
            // Submitting the task to the executor
            // Each task continuously sends requests to the server with unique id.
            results[i]=executor.submit(new SyncRequestInvoker());
        }
        for(int i=0; i<nThreads;i++){
            results[i].get();
        }
        executor.shutdown();
    }

    static class SyncRequestInvoker implements Runnable{
        SyncRequestInvoker(){
        }
        /**
         * Run method to send continuously send requests to the server.
         */
        @Override
        public void run() {
            try(HttpClient client = HttpClient.newBuilder().build()) {
                while (!Thread.interrupted()) {
                    int requestCount = idCount.incrementAndGet();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(endpoint+"?id=" + requestCount)).build();
                    try {
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        if (response.statusCode() != 200) {
                            logger.severe("Unexpected errorCode: " + response.statusCode());
                            throw new RuntimeException("Request failed!");
                        }
                        if (requestCount % 100000 == 0){
                            logger.log(Level.INFO, "Processed:{0} requests", requestCount);
                        }
                    } catch (IOException | InterruptedException exception) {
                        logger.log(Level.SEVERE, "Exception was caught", exception);
                        throw new RuntimeException("Thread ran into error");
                    }
                }
            }
        }
    }

}

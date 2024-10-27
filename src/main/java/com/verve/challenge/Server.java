package com.verve.challenge;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Server class is used to create an HTTP server that listens on port 8000.
 * It creates a context for a specific path and sets the handler for the context.
 * The handler processes the incoming requests and sends a response back to the client.
 */
public class Server
{
    private static final Logger logger = Logger.getLogger( Server.class.getName());

    public static void runServer(int port, int nThreads) throws IOException
    {
        // Create an HTTP server that listens on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Create a context for the path "/api/verve/accept" and set the handler for the context
        StatCollector statCollector = StatCollector.getInstance();
        server.createContext("/api/verve/accept", new ApiHandler(statCollector));
        logger.log(Level.INFO, "Using {0} Threads", nThreads);
        server.setExecutor(Executors.newFixedThreadPool(nThreads));
        server.start();
        // Schedule a task to collect statistics every minute
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(statCollector, 0, 60, TimeUnit.SECONDS);

        logger.log(Level.INFO,"Server is running on port {0}", port);
    }

    /**
     * ApiHandler class is used to handle the incoming HTTP requests.
     * It processes the request, sends a request to another endpoint, and sends a response back to the client.
     */
    static class ApiHandler implements HttpHandler {
        private static final String ID_KEY = "id";
        private static final String ENDPOINT_KEY = "endpoint";
        private static final String SUCCESS_MESSAGE = "ok";
        private static final String FAILURE_MESSAGE = "failed";

        private final StatCollector statCollector;

        ApiHandler(StatCollector statCollector) {
            this.statCollector = statCollector;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException
        {
            boolean successful = false;
            try {
                String query = httpExchange.getRequestURI().getQuery();
                Map<String, Object> parameters = parseQuery(query);
                Integer id = (Integer) parameters.get(ID_KEY);
                String endpoint = (String) parameters.get(ENDPOINT_KEY);
                int totalRequestProcessed = statCollector.notifyNewRequest(id);
                if (endpoint != null) {
                    sendRequest(endpoint, totalRequestProcessed);
                }
                successful = true;
            }catch(Exception ex){
                logger.log(Level.SEVERE, "Exception while processing request: {0}", ex.getMessage());
            }finally{
                String response = successful ? SUCCESS_MESSAGE: FAILURE_MESSAGE;
                int responseCode = successful ? 200 : 500;
                httpExchange.sendResponseHeaders(responseCode, response.getBytes().length);
                try(OutputStream os = httpExchange.getResponseBody()){
                    os.write(response.getBytes());
                }
            }
        }
        /**
         * Sends a POST request to the specified endpoint with the total number of requests processed.
         * @param endpoint The endpoint to send the request to
         * @param requestProcessedCount The total number of requests processed
         * @throws IOException
         * @throws InterruptedException
         */
        private void sendRequest(String endpoint, int requestProcessedCount) throws IOException, InterruptedException {
            try(HttpClient client = HttpClient.newBuilder().build()){
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(endpoint))
                        .header("Content-Type", "text/plain")
                        .POST(HttpRequest.BodyPublishers.ofString(String.format("totalProcessed: %d", requestProcessedCount)))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();
                logger.log(Level.INFO,"Response from endpoint {0} : {1}", new Object[] { endpoint, statusCode });
            }
        }
        /**
         * Parses the query string and extracts the id and endpoint parameters.
         * @param query The query string to parse
         * @return A map containing the id and endpoint parameters
         */
        private Map<String, Object> parseQuery(String query){
            Map<String, Object> parameterMap = new HashMap<>();
            if (query != null){
                for (String param : query.split("&")) {
                    String[] entry = param.split("=");
                    if (entry[0].equals(ID_KEY) && entry.length == 2){
                        parameterMap.put(ID_KEY, Integer.valueOf(entry[1]));
                    }else if (entry[0].equals(ENDPOINT_KEY) && entry.length == 2){
                        parameterMap.put(ENDPOINT_KEY, entry[1]);
                    }
                }
            }
            if (parameterMap.get(ID_KEY) == null){
                throw new IllegalArgumentException("Mandatory parameter id is missing!");
            }
            return parameterMap;
        }
    }
}
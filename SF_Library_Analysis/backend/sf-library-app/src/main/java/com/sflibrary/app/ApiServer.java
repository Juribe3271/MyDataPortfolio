// src/main/java/com/sflibrary/app/ApiServer.java
// This class sets up a simple HTTP server to expose library usage data as REST API endpoints.

package com.sflibrary.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class ApiServer {

    private static final int PORT = 8080; // Port for the API server
    private LibraryUsageDAO dao; // Data Access Object to fetch data
    private Gson gson; // Gson instance for JSON serialization

    public ApiServer(LibraryUsageDAO dao) {
        this.dao = dao;
        // Create a Gson instance for pretty printing JSON (optional, good for debugging)
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Starts the HTTP server and sets up API endpoints.
     */
    public void start() {
        try {
            // Create an HTTP server bound to the specified port
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Create contexts (API endpoints) and assign handlers
            server.createContext("/api/patron-type-stats", this::handlePatronTypeStats);
            server.createContext("/api/age-range-stats", this::handleAgeRangeStats);
            server.createContext("/api/monthly-usage", this::handleMonthlyUsage);

            // Set a default executor for handling requests (thread pool)
            server.setExecutor(Executors.newFixedThreadPool(10)); // Use a thread pool for concurrent requests

            // Start the server
            server.start();
            System.out.println("API Server started on port " + PORT);
            System.out.println("Endpoints available:");
            System.out.println("  - http://localhost:" + PORT + "/api/patron-type-stats");
            System.out.println("  - http://localhost:" + PORT + "/api/age-range-stats");
            System.out.println("  - http://localhost:" + PORT + "/api/monthly-usage");

        } catch (IOException e) {
            System.err.println("Error starting API server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles requests for patron type statistics.
     * Fetches data from DAO, converts to JSON, and sends response.
     * @param exchange The HttpExchange object representing the request and response.
     */
    private void handlePatronTypeStats(HttpExchange exchange) throws IOException {
        // Set CORS headers to allow requests from any origin (for development)
        setCorsHeaders(exchange.getResponseHeaders());

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                List<Map<String, Object>> stats = dao.getTotalCheckoutsAndRenewalsByPatronType();
                String jsonResponse = gson.toJson(stats); // Convert Java object to JSON string
                sendResponse(exchange, 200, jsonResponse); // Send successful response
            } catch (Exception e) {
                System.err.println("Error handling patron type stats: " + e.getMessage());
                sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage()); // Send error response
            }
        } else {
            sendResponse(exchange, 405, "Method Not Allowed"); // Only GET is allowed
        }
    }

    /**
     * Handles requests for age range statistics.
     * @param exchange The HttpExchange object.
     */
    private void handleAgeRangeStats(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange.getResponseHeaders());

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                List<Map<String, Object>> stats = dao.getAverageCheckoutsByAgeRange();
                String jsonResponse = gson.toJson(stats);
                sendResponse(exchange, 200, jsonResponse);
            } catch (Exception e) {
                System.err.println("Error handling age range stats: " + e.getMessage());
                sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
            }
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    /**
     * Handles requests for monthly usage statistics.
     * @param exchange The HttpExchange object.
     */
    private void handleMonthlyUsage(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange.getResponseHeaders());

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                List<Map<String, Object>> stats = dao.getTotalUsageByMonth();
                String jsonResponse = gson.toJson(stats);
                sendResponse(exchange, 200, jsonResponse);
            } catch (Exception e) {
                System.err.println("Error handling monthly usage stats: " + e.getMessage());
                sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
            }
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    /**
     * Helper method to set CORS headers for allowing cross-origin requests.
     * @param headers The response headers to modify.
     */
    private void setCorsHeaders(Headers headers) {
        // IMPORTANT: For production, replace "*" with the specific origin(s) of your frontend application
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS"); // Allow necessary methods
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization"); // Allow necessary headers
        headers.add("Access-Control-Max-Age", "86400"); // Cache preflight requests for 24 hours
    }

    /**
     * Helper method to send an HTTP response.
     * @param exchange The HttpExchange object.
     * @param statusCode The HTTP status code (e.g., 200 OK, 500 Internal Server Error).
     * @param responseBody The body of the response (e.g., JSON string).
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String responseBody) throws IOException {
        // Set Content-Type header to indicate JSON response
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        // Send response headers and body length
        exchange.sendResponseHeaders(statusCode, responseBody.getBytes().length);
        // Write the response body
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBody.getBytes());
        }
    }
}

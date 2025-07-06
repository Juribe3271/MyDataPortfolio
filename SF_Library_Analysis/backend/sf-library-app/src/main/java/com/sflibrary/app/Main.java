// src/main/java/com/sflibrary/app/Main.java
// This is the main class to run the application and demonstrate data loading and querying.

package com.sflibrary.app;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        LibraryUsageDAO dao = new LibraryUsageDAO();
        LibraryDataLoader dataLoader = new LibraryDataLoader(dao);

        System.out.println("--- San Francisco Library Usage Application ---");

        // --- 1. Clear existing data (optional, useful for re-running) ---
        dao.clearTable(); // Ensure this line is UNCOMMENTED

        // --- 2. Load data from CSV into MySQL ---
        dataLoader.loadData();

        // --- 3. Perform some basic queries (keeping for console output, but data now available via API) ---
        System.out.println("\n--- Basic Querying Data (also available via API) ---");

        // Get total number of records (first 100 for display)
        System.out.println("\nTotal records (first 100 for display):");
        List<LibraryUsageRecord> allRecords = dao.getAllRecords();
        for (LibraryUsageRecord record : allRecords) {
            System.out.println(record);
        }
        if (allRecords.size() == 100) {
            System.out.println("... (displaying first 100 records only)");
        }

        // Get records by age range
        String targetAgeRange = "25-34";
        System.out.println("\nRecords for age range '" + targetAgeRange + "': (first 50 for display)");
        List<LibraryUsageRecord> ageRangeRecords = dao.getRecordsByAgeRange(targetAgeRange);
        if (ageRangeRecords.isEmpty()) {
            System.out.println("No records found for age range " + targetAgeRange);
        } else {
            for (LibraryUsageRecord record : ageRangeRecords) {
                System.out.println(record);
            }
            if (ageRangeRecords.size() == 50) {
                System.out.println("... (displaying first 50 records only)");
            }
        }

        // Get total checkouts for a specific library
        String targetLibraryCode = "EXM"; // Example library code
        System.out.println("\nTotal checkouts for library code '" + targetLibraryCode + "':");
        int totalCheckouts = dao.getTotalCheckoutsByLibrary(targetLibraryCode);
        if (totalCheckouts != -1) {
            System.out.println("Total checkouts: " + totalCheckouts);
        } else {
            System.out.println("Could not retrieve total checkouts for library " + targetLibraryCode);
        }

        // --- 4. Perform more complex analytical queries (also available via API) ---
        System.out.println("\n--- Analytical Queries (also available via API) ---");

        // Get total checkouts and renewals by Patron Type
        System.out.println("\nTotal Checkouts and Renewals by Patron Type:");
        List<Map<String, Object>> patronTypeStats = dao.getTotalCheckoutsAndRenewalsByPatronType();
        if (patronTypeStats.isEmpty()) {
            System.out.println("No data for patron type statistics.");
        } else {
            for (Map<String, Object> entry : patronTypeStats) {
                System.out.println("Patron Type: " + entry.get("patron_type") +
                        ", Total Checkouts: " + entry.get("total_checkouts") +
                        ", Total Renewals: " + entry.get("total_renewals"));
            }
        }

        // Get average checkouts per age range
        System.out.println("\nAverage Checkouts per Age Range:");
        List<Map<String, Object>> avgCheckoutsByAge = dao.getAverageCheckoutsByAgeRange();
        if (avgCheckoutsByAge.isEmpty()) {
            System.out.println("No data for average checkouts by age range.");
        } else {
            for (Map<String, Object> entry : avgCheckoutsByAge) {
                System.out.printf("Age Range: %s, Average Checkouts: %.2f%n",
                        entry.get("age_range"), entry.get("average_checkouts"));
            }
        }

        // Get top 5 libraries by total checkouts
        System.out.println("\nTop 5 Libraries by Total Checkouts:");
        List<Map<String, Object>> topLibrariesCheckouts = dao.getTopLibrariesByCheckouts(5);
        if (topLibrariesCheckouts.isEmpty()) {
            System.out.println("No data for top libraries by checkouts.");
        } else {
            for (Map<String, Object> entry : topLibrariesCheckouts) {
                System.out.println("Library: " + entry.get("home_library_definition") + ", Total Checkouts: " + entry.get("total_checkouts"));
            }
        }

        // Get top 5 libraries by total renewals
        System.out.println("\nTop 5 Libraries by Total Renewals:");
        List<Map<String, Object>> topLibrariesRenewals = dao.getTopLibrariesByRenewals(5);
        if (topLibrariesRenewals.isEmpty()) {
            System.out.println("No data for top libraries by renewals.");
        } else {
            for (Map<String, Object> entry : topLibrariesRenewals) {
                System.out.println("Library: " + entry.get("home_library_definition") + ", Total Renewals: " + entry.get("total_renewals"));
            }
        }

        // Total Checkouts and Renewals by Year
        System.out.println("\nTotal Checkouts and Renewals by Circulation Active Year:");
        List<Map<String, Object>> usageByYear = dao.getTotalUsageByYear();
        if (usageByYear.isEmpty()) {
            System.out.println("No data for usage by year.");
        } else {
            for (Map<String, Object> entry : usageByYear) {
                System.out.println("Year: " + entry.get("circulation_active_year") +
                        ", Total Checkouts: " + entry.get("total_checkouts") +
                        ", Total Renewals: " + entry.get("total_renewals"));
            }
        }

        // Total Checkouts and Renewals by Month
        System.out.println("\nTotal Checkouts and Renewals by Circulation Active Month:");
        List<Map<String, Object>> usageByMonth = dao.getTotalUsageByMonth();
        if (usageByMonth.isEmpty()) {
            System.out.println("No data for usage by month.");
        } else {
            for (Map<String, Object> entry : usageByMonth) {
                System.out.println("Month: " + entry.get("circulation_active_month") +
                        ", Total Checkouts: " + entry.get("total_checkouts") +
                        ", Total Renewals: " + entry.get("total_renewals"));
            }
        }

        // Top Libraries by Checkouts for specific Patron Types
        String[] patronTypesToAnalyze = {"ADULT", "JUVENILE", "SENIOR"};
        for (String pType : patronTypesToAnalyze) {
            System.out.println("\nTop 3 Libraries by Checkouts for Patron Type: " + pType + ":");
            List<Map<String, Object>> topLibrariesForPatronType = dao.getTopLibrariesByCheckoutsForPatronType(pType, 3);
            if (topLibrariesForPatronType.isEmpty()) {
                System.out.println("No data for " + pType + " patron type.");
            } else {
                for (Map<String, Object> entry : topLibrariesForPatronType) {
                    System.out.println("  Library: " + entry.get("home_library_definition") + ", Total Checkouts: " + entry.get("total_checkouts"));
                }
            }
        }

        System.out.println("\n--- Application finished (API Server starting...) ---");

        // --- Start the API Server ---
        ApiServer apiServer = new ApiServer(dao);
        apiServer.start();

        // The main method will exit, but the API server will continue running in its own threads.
        // You might want to add a mechanism to keep the main thread alive or to gracefully shut down the server.
        // For now, it will run until the JVM is stopped.
    }
}

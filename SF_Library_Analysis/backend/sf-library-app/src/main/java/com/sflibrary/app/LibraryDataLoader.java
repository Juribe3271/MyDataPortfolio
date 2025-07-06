// src/main/java/com/sflibrary/app/LibraryDataLoader.java
// This class handles reading the CSV and loading data into the MySQL database.

package com.sflibrary.app;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types; // For handling null integers

public class LibraryDataLoader {

    // Path to your CSV file relative to the project root or absolute path
    // Make sure 'Library_Usage.csv' is in the same directory as your pom.xml file.
    private static final String CSV_FILE_PATH = "Library_Usage.csv"; // Adjust this if your CSV is elsewhere

    private LibraryUsageDAO dao; // Use the DAO to interact with the database

    public LibraryDataLoader(LibraryUsageDAO dao) {
        this.dao = dao;
    }

    /**
     * Loads data from the CSV file into the database.
     * This method uses JDBC batch updates for efficiency.
     */
    public void loadData() {
        // SQL for batch insert - NOW INCLUDING circulation_active_month
        String sql = "INSERT INTO library_usage (patron_type_code, patron_type, circulation_active_year, age_range, total_checkouts, total_renewals, home_library_code, home_library_definition, circulation_active_month) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Reduced batch size for more frequent progress updates during loading
        int batchSize = 100; // Changed from 1000 to 100 for better visibility
        int count = 0; // Counter for records processed

        System.out.println("Starting data load from CSV: " + CSV_FILE_PATH);
        System.out.flush(); // Ensure this message is printed immediately

        Connection conn = null; // Declare connection outside try-with-resources to use in finally block
        try (Reader reader = new FileReader(CSV_FILE_PATH);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                     .setHeader() // Assumes the first row is the header
                     .setSkipHeaderRecord(true) // Skip the header row after reading it
                     .setIgnoreHeaderCase(true) // Ignore case when matching header names (though we're using exact names now)
                     .setTrim(true) // Trim leading/trailing whitespace
                     .build())) {

            conn = dao.getConnection(); // Get connection here, outside the PreparedStatement try-with-resources
            if (conn == null) {
                System.err.println("Failed to get database connection. Cannot load data.");
                System.err.flush();
                return; // Exit if no connection
            }
            conn.setAutoCommit(false); // Disable auto-commit for batch processing
            System.out.println("Database connection established and auto-commit disabled.");
            System.out.flush();

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                System.out.println("Starting CSV record processing...");
                System.out.flush();

                for (CSVRecord csvRecord : csvParser) {
                    try {
                        // Accessing values by EXACT header name from your CSV screenshot
                        Integer patronTypeCode = parseInteger(csvRecord.get("Patron Type Code"));
                        String patronType = csvRecord.get("Patron Type Definition");
                        Integer circulationActiveYear = parseInteger(csvRecord.get("Circulation Active Year"));
                        String ageRange = csvRecord.get("Age Range");
                        Integer totalCheckouts = parseInteger(csvRecord.get("Total Checkouts"));
                        Integer totalRenewals = parseInteger(csvRecord.get("Total Renewals"));
                        String homeLibraryCode = csvRecord.get("Home Library Code");
                        String homeLibraryDefinition = csvRecord.get("Home Library Definition");
                        // NEW: Get Circulation Active Month
                        String circulationActiveMonth = csvRecord.get("Circulation Active Month"); // Corrected to match CSV

                        // Set parameters for the prepared statement
                        if (patronTypeCode != null) pstmt.setInt(1, patronTypeCode); else pstmt.setNull(1, Types.INTEGER);
                        pstmt.setString(2, patronType);
                        if (circulationActiveYear != null) pstmt.setInt(3, circulationActiveYear); else pstmt.setNull(3, Types.INTEGER);
                        pstmt.setString(4, ageRange);
                        if (totalCheckouts != null) pstmt.setInt(5, totalCheckouts); else pstmt.setNull(5, Types.INTEGER);
                        if (totalRenewals != null) pstmt.setInt(6, totalRenewals); else pstmt.setNull(6, Types.INTEGER);
                        pstmt.setString(7, homeLibraryCode);
                        pstmt.setString(8, homeLibraryDefinition);
                        // NEW: Set Circulation Active Month
                        pstmt.setString(9, circulationActiveMonth); // Set the month string

                        pstmt.addBatch(); // Add to batch

                        // Print a message for every record to see granular progress
                        if (++count % 10 == 0) { // Print every 10 records for very granular feedback
                            System.out.println("Processed " + count + " records...");
                            System.out.flush();
                        }

                        if (count % batchSize == 0) {
                            System.out.println("Executing batch for " + count + " records...");
                            System.out.flush();
                            pstmt.executeBatch(); // Execute batch
                            conn.commit(); // Commit transaction
                            System.out.println("Inserted " + count + " records.");
                            System.out.flush();
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Skipping malformed record (line " + csvRecord.getRecordNumber() + "): " + e.getMessage());
                        System.err.flush();
                    }
                }

                // Execute any remaining records in the batch
                if (count % batchSize != 0) { // Only execute if there are remaining records not yet committed
                    System.out.println("Executing final batch for " + count + " records...");
                    System.out.flush();
                    pstmt.executeBatch();
                }
                conn.commit(); // Final commit
                System.out.println("Finished loading data. Total records inserted: " + count);
                System.out.flush();
            } // End of PreparedStatement try-with-resources

        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            System.err.flush();
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL error during data load: " + e.getMessage());
            System.err.flush();
            e.printStackTrace();
            try {
                if (conn != null) { // Check if conn is not null before rollback
                    conn.rollback(); // Rollback on error
                    System.err.println("Transaction rolled back.");
                    System.err.flush();
                }
            } catch (SQLException ex) {
                System.err.println("Error during rollback: " + ex.getMessage());
                System.err.flush();
            }
        } finally {
            try {
                if (conn != null && !conn.isClosed()) { // Check if conn is not null and not already closed
                    conn.setAutoCommit(true); // Re-enable auto-commit
                    conn.close(); // Close the connection in finally block
                    System.out.println("Database connection closed.");
                    System.out.flush();
                }
            } catch (SQLException e) {
                System.err.println("Error re-enabling auto-commit or closing connection: " + e.getMessage());
                System.err.flush();
            }
        }
    }

    /**
     * Helper method to safely parse an Integer from a String.
     * Returns null if the string is empty or cannot be parsed.
     */
    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            // Log or handle the error if a non-numeric string is found where an integer is expected
            System.err.println("Warning: Could not parse '" + value + "' to integer. Returning null.");
            System.err.flush();
            return null;
        }
    }
}

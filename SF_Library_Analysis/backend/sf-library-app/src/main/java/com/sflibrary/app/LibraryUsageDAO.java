// src/main/java/com/sflibrary/app/LibraryUsageDAO.java
// This DAO handles all database operations for the library_usage table.

package com.sflibrary.app;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryUsageDAO {

    // Database connection details
    // IMPORTANT: Ensure these match your MySQL server configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sf_library_db";
    private static final String DB_USER = "root"; // Use "root" as you configured
    private static final String DB_PASSWORD = "root"; // Use the password you set during MySQL installation (e.g., "root")

    public LibraryUsageDAO() {
        try {
            // Register the JDBC driver. This is necessary for the DriverManager to find the correct driver.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // If the driver is not found, print an error and exit.
            System.err.println("MySQL JDBC Driver not found. Make sure mysql-connector-java.jar is in your classpath.");
            e.printStackTrace();
            System.exit(1); // Exit if driver not found
        }
    }

    /**
     * Establishes a connection to the database.
     * This method is used internally by other DAO methods.
     * @return A Connection object if successful, null otherwise.
     */
    public Connection getConnection() {
        Connection connection = null;
        try {
            // Attempt to establish a connection using the defined URL, user, and password.
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // System.out.println("Database connection established."); // Uncomment for verbose connection messages
        } catch (SQLException e) {
            // Catch any SQL exceptions during connection and print an error.
            System.err.println("Error establishing database connection: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Inserts a single LibraryUsageRecord into the database.
     * Uses a PreparedStatement to prevent SQL injection and handle parameters safely.
     * @param record The LibraryUsageRecord object to add.
     * @return true if the record was added successfully, false otherwise.
     */
    public boolean addRecord(LibraryUsageRecord record) {
        String sql = "INSERT INTO library_usage (patron_type_code, patron_type, circulation_active_year, age_range, total_checkouts, total_renewals, home_library_code, home_library_definition) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); // Get a connection to the database
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Prepare the SQL statement, requesting generated keys

            // Set parameters for the prepared statement. Handle potential null values for Integer types.
            if (record.getPatronTypeCode() != null) pstmt.setInt(1, record.getPatronTypeCode()); else pstmt.setNull(1, Types.INTEGER);
            pstmt.setString(2, record.getPatronType());
            if (record.getCirculationActiveYear() != null) pstmt.setInt(3, record.getCirculationActiveYear()); else pstmt.setNull(3, Types.INTEGER);
            pstmt.setString(4, record.getAgeRange());
            if (record.getTotalCheckouts() != null) pstmt.setInt(5, record.getTotalCheckouts()); else pstmt.setNull(5, Types.INTEGER);
            if (record.getTotalRenewals() != null) pstmt.setInt(6, record.getTotalRenewals()); else pstmt.setNull(6, Types.INTEGER);
            pstmt.setString(7, record.getHomeLibraryCode());
            pstmt.setString(8, record.getHomeLibraryDefinition());

            int affectedRows = pstmt.executeUpdate(); // Execute the insert statement

            if (affectedRows > 0) {
                // If rows were affected, retrieve the auto-generated primary key (record_id).
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        record.setRecordId(generatedKeys.getInt(1)); // Set the generated ID back to the record object
                    }
                }
                return true; // Record added successfully
            }
        } catch (SQLException e) {
            // Catch any SQL exceptions during insertion and print an error.
            System.err.println("Error adding record: " + e.getMessage());
            // e.printStackTrace(); // Uncomment for full stack trace during debugging
        }
        return false; // Record not added
    }

    /**
     * Retrieves all library usage records from the database.
     * A LIMIT is applied to prevent fetching excessively large datasets for demonstration.
     * @return A list of LibraryUsageRecord objects.
     */
    public List<LibraryUsageRecord> getAllRecords() {
        List<LibraryUsageRecord> records = new ArrayList<>();
        String sql = "SELECT record_id, patron_type_code, patron_type, circulation_active_year, age_range, total_checkouts, total_renewals, home_library_code, home_library_definition FROM library_usage LIMIT 100"; // LIMIT for demonstration
        try (Connection conn = getConnection(); // Get a connection
             Statement stmt = conn.createStatement(); // Create a statement
             ResultSet rs = stmt.executeQuery(sql)) { // Execute the query and get results

            while (rs.next()) {
                // Iterate through the result set and map each row to a LibraryUsageRecord object.
                records.add(mapResultSetToRecord(rs));
            }
        } catch (SQLException e) {
            // Catch any SQL exceptions during retrieval and print an error.
            System.err.println("Error retrieving all records: " + e.getMessage());
            e.printStackTrace();
        }
        return records;
    }

    /**
     * Retrieves records by a specific age range.
     * @param ageRange The age range to filter by (e.g., "25-34").
     * @return A list of LibraryUsageRecord objects.
     */
    public List<LibraryUsageRecord> getRecordsByAgeRange(String ageRange) {
        List<LibraryUsageRecord> records = new ArrayList<>();
        String sql = "SELECT record_id, patron_type_code, patron_type, circulation_active_year, age_range, total_checkouts, total_renewals, home_library_code, home_library_definition FROM library_usage WHERE age_range = ? LIMIT 50";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ageRange); // Set the age range parameter
            try (ResultSet rs = pstmt.executeQuery()) { // Execute the query
                while (rs.next()) {
                    records.add(mapResultSetToRecord(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving records by age range: " + e.getMessage());
            e.printStackTrace();
        }
        return records;
    }

    /**
     * Calculates total checkouts for a specific home library.
     * @param libraryCode The code of the home library (e.g., "EXM").
     * @return The total checkouts for that library, or -1 if an error occurs.
     */
    public int getTotalCheckoutsByLibrary(String libraryCode) {
        String sql = "SELECT SUM(total_checkouts) FROM library_usage WHERE home_library_code = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, libraryCode); // Set the library code parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Get the sum
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total checkouts by library: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Indicate error
    }

    /**
     * Retrieves the top N age ranges by total checkouts.
     * @param limit The number of top age ranges to retrieve.
     * @return A list of Map objects, each containing "age_range" and "total_checkouts".
     */
    public List<Map<String, Object>> getTopAgeRangesByCheckouts(int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT age_range, SUM(total_checkouts) AS total_checkouts_sum " +
                "FROM library_usage " +
                "WHERE age_range IS NOT NULL AND total_checkouts IS NOT NULL " +
                "GROUP BY age_range " +
                "ORDER BY total_checkouts_sum DESC " +
                "LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("age_range", rs.getString("age_range"));
                    row.put("total_checkouts", rs.getLong("total_checkouts_sum")); // Use getLong for sum
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving top age ranges by checkouts: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Retrieves the top N libraries by total checkouts.
     * @param limit The number of top libraries to retrieve.
     * @return A list of Map objects, each containing "home_library_definition" and "total_checkouts".
     */
    public List<Map<String, Object>> getTopLibrariesByCheckouts(int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT home_library_definition, SUM(total_checkouts) AS total_checkouts_sum " +
                "FROM library_usage " +
                "WHERE home_library_definition IS NOT NULL AND total_checkouts IS NOT NULL " +
                "GROUP BY home_library_definition " +
                "ORDER BY total_checkouts_sum DESC " +
                "LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("home_library_definition", rs.getString("home_library_definition"));
                    row.put("total_checkouts", rs.getLong("total_checkouts_sum")); // Use getLong for sum
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving top libraries by checkouts: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Retrieves total checkouts and renewals for each patron type.
     * @return A list of Map objects, each containing "patron_type" and "total_checkouts" and "total_renewals".
     */
    public List<Map<String, Object>> getTotalCheckoutsAndRenewalsByPatronType() {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT patron_type, SUM(total_checkouts) AS total_checkouts_sum, SUM(total_renewals) AS total_renewals_sum " +
                "FROM library_usage " +
                "WHERE patron_type IS NOT NULL AND total_checkouts IS NOT NULL AND total_renewals IS NOT NULL " +
                "GROUP BY patron_type " +
                "ORDER BY total_checkouts_sum DESC"; // Order by checkouts for a sensible default
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("patron_type", rs.getString("patron_type"));
                    row.put("total_checkouts", rs.getLong("total_checkouts_sum"));
                    row.put("total_renewals", rs.getLong("total_renewals_sum"));
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving total checkouts and renewals by patron type: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Retrieves the average checkouts for each age range.
     * @return A list of Map objects, each containing "age_range" and "average_checkouts".
     */
    public List<Map<String, Object>> getAverageCheckoutsByAgeRange() {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT age_range, AVG(total_checkouts) AS average_checkouts " +
                "FROM library_usage " +
                "WHERE age_range IS NOT NULL AND total_checkouts IS NOT NULL " +
                "GROUP BY age_range " +
                "ORDER BY average_checkouts DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("age_range", rs.getString("age_range"));
                    row.put("average_checkouts", rs.getDouble("average_checkouts")); // Use getDouble for AVG
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving average checkouts by age range: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Retrieves the top N libraries by total renewals.
     * @param limit The number of top libraries to retrieve.
     * @return A list of Map objects, each containing "home_library_definition" and "total_renewals".
     */
    public List<Map<String, Object>> getTopLibrariesByRenewals(int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT home_library_definition, SUM(total_renewals) AS total_renewals_sum " +
                "FROM library_usage " +
                "WHERE home_library_definition IS NOT NULL AND total_renewals IS NOT NULL " +
                "GROUP BY home_library_definition " +
                "ORDER BY total_renewals_sum DESC " +
                "LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("home_library_definition", rs.getString("home_library_definition"));
                    row.put("total_renewals", rs.getLong("total_renewals_sum")); // Use getLong for sum
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving top libraries by renewals: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Retrieves total checkouts and renewals grouped by circulation active year.
     * @return A list of Map objects, each containing "circulation_active_year", "total_checkouts", and "total_renewals".
     */
    public List<Map<String, Object>> getTotalUsageByYear() {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT circulation_active_year, SUM(total_checkouts) AS total_checkouts_sum, SUM(total_renewals) AS total_renewals_sum " +
                "FROM library_usage " +
                "WHERE circulation_active_year IS NOT NULL AND total_checkouts IS NOT NULL AND total_renewals IS NOT NULL " +
                "GROUP BY circulation_active_year " +
                "ORDER BY circulation_active_year ASC"; // Order by year
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("circulation_active_year", rs.getInt("circulation_active_year"));
                    row.put("total_checkouts", rs.getLong("total_checkouts_sum"));
                    row.put("total_renewals", rs.getLong("total_renewals_sum"));
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving total usage by year: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Retrieves total checkouts and renewals grouped by circulation active month.
     * Note: This assumes 'Circulation Active Month' in CSV is a month name.
     * We convert month names to numbers for proper ordering.
     * @return A list of Map objects, each containing "circulation_active_month", "total_checkouts", and "total_renewals".
     */
    public List<Map<String, Object>> getTotalUsageByMonth() {
        List<Map<String, Object>> results = new ArrayList<>();
        // Using FIELD() for custom sorting of month names
        String sql = "SELECT circulation_active_month, SUM(total_checkouts) AS total_checkouts_sum, SUM(total_renewals) AS total_renewals_sum " +
                "FROM library_usage " +
                "WHERE circulation_active_month IS NOT NULL AND total_checkouts IS NOT NULL AND total_renewals IS NOT NULL " +
                "GROUP BY circulation_active_month " +
                "ORDER BY FIELD(circulation_active_month, 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December')";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("circulation_active_month", rs.getString("circulation_active_month"));
                    row.put("total_checkouts", rs.getLong("total_checkouts_sum"));
                    row.put("total_renewals", rs.getLong("total_renewals_sum"));
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving total usage by month: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Retrieves top N libraries by checkouts for a specific patron type.
     * @param patronType The patron type to filter by (e.g., "ADULT").
     * @param limit The number of top libraries to retrieve.
     * @return A list of Map objects, each containing "home_library_definition" and "total_checkouts".
     */
    public List<Map<String, Object>> getTopLibrariesByCheckoutsForPatronType(String patronType, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT home_library_definition, SUM(total_checkouts) AS total_checkouts_sum " +
                "FROM library_usage " +
                "WHERE patron_type = ? AND home_library_definition IS NOT NULL AND total_checkouts IS NOT NULL " +
                "GROUP BY home_library_definition " +
                "ORDER BY total_checkouts_sum DESC " +
                "LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patronType);
            pstmt.setInt(2, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("home_library_definition", rs.getString("home_library_definition"));
                    row.put("total_checkouts", rs.getLong("total_checkouts_sum"));
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving top libraries by checkouts for patron type '" + patronType + "': " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }


    /**
     * Helper method to map a ResultSet row to a LibraryUsageRecord object.
     * This reduces code duplication in retrieval methods.
     */
    private LibraryUsageRecord mapResultSetToRecord(ResultSet rs) throws SQLException {
        return new LibraryUsageRecord(
                rs.getInt("record_id"),
                (Integer) rs.getObject("patron_type_code"), // Use getObject for nullable int
                rs.getString("patron_type"),
                (Integer) rs.getObject("circulation_active_year"),
                rs.getString("age_range"),
                (Integer) rs.getObject("total_checkouts"),
                (Integer) rs.getObject("total_renewals"),
                rs.getString("home_library_code"),
                rs.getString("home_library_definition")
        );
    }

    /**
     * Clears all data from the library_usage table. Use with caution!
     */
    public void clearTable() {
        String sql = "DELETE FROM library_usage";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("library_usage table cleared.");
        } catch (SQLException e) {
            System.err.println("Error clearing table: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

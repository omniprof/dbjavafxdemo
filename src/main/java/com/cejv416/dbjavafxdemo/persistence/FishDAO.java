/**
 * This class provides the functionality to: <ul> <li>1) Open a database <li>2)
 * Retrieve all the records from a user query and return them as an ArrayList
 * <li>3) Close the database </ul>
 * Added logging Changed read to 3 methods, findAll, findID and findDiet
 * Eliminated returning null references
 *
 * @author Ken Fogel
 * @version 1.8
 */
package com.cejv416.dbjavafxdemo.persistence;

import com.cejv416.dbjavafxdemo.beans.FishData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exceptions are possible whenever a JDBC object is used. When these exceptions
 * occur it should result in some message appearing to the USER and possibly
 * some corrective action. To simplify this, all exceptions are thrown and not
 * caught here. The methods that you write to call any of these methods must
 * either use a try/catch or continue throwing the exception.

 * Updated by creating a new method createFishData that used the resultSet to
 * create an object. Originally this code was repeated three times.
 *
 */
public class FishDAO {

    private static final Logger LOG = Logger.getLogger(FishDAO.class.getName());


    // This information should be coming from a Properties file
    private final static String URL = "jdbc:mysql://localhost:3306/AQUARIUM?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";
    private final static String USER = "fish";
    private final static String PASSWORD = "kfstandard";

    /**
     * Retrieve all the records for the given table and returns the data as an
     * ArrayList of FishData objects
     *
     * @return The ArrayList of FishData objects
     * @throws java.sql.SQLException
     */
    public List<FishData> findAll() throws SQLException {

        List<FishData> rows = new ArrayList<>();

        String selectQuery = "SELECT ID, COMMONNAME, LATIN, PH, KH, TEMP, FISHSIZE, SPECIESORIGIN, TANKSIZE, STOCKING, DIET FROM FISH";

        // Using try with resources
        // This ensures that the objects in the parenthesis () will be closed
        // when block ends. In this case the Connection, PreparedStatement and
        // the ResultSet will all be closed.
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                // You must use PreparedStatements to guard against SQL
                // Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = pStatement.executeQuery()) {
            while (resultSet.next()) {
                rows.add(createFishData(resultSet));
            }
        }
        LOG.log(Level.INFO, "# of records found : {0}", rows.size());
        return rows;
    }

    /**
     * Retrieve one record from the given table based on the primary key
     *
     * @param id
     * @return The FishData object
     * @throws java.sql.SQLException
     */
    public FishData findID(int id) throws SQLException {

        FishData fishData = new FishData();

        String selectQuery = "SELECT ID, COMMONNAME, LATIN, PH, KH, TEMP, FISHSIZE, SPECIESORIGIN, TANKSIZE, STOCKING, DIET FROM FISH WHERE ID = ?";

        // Using try with resources
        // Class that implement the Closable interface created in the
        // parenthesis () will be closed when the block ends.
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                // You must use PreparedStatements to guard against SQL
                // Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {
            // Only object creation statements can be in the parenthesis so
            // first try-with-resources block ends
            pStatement.setInt(1, id);
            // A new try-with-resources block for creating the ResultSet object
            // begins
            try (ResultSet resultSet = pStatement.executeQuery()) {
                if (resultSet.next()) {
                    fishData = createFishData(resultSet);
                }
            }
        }
        LOG.log(Level.INFO, "Found {0}?: {1}", new Object[]{id, fishData != null});
        return fishData;
    }

    /**
     * Retrieve all the records from the given table that share the same value
     * in the Diet column and returns the data as an ArrayList of FishData
     * objects
     *
     * @param diet
     * @return The ArrayList of FishData objects
     * @throws java.sql.SQLException
     */
    public List<FishData> findDiet(String diet) throws SQLException {

        List<FishData> rows = new ArrayList<>();

        String selectQuery = "SELECT ID, COMMONNAME, LATIN, PH, KH, TEMP, FISHSIZE, SPECIESORIGIN, TANKSIZE, STOCKING, DIET FROM FISH WHERE DIET = ?";

        // Using try with resources
        // Class that implement the Closable interface created in the
        // parenthesis () will be closed when the block ends.
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                // You must use PreparedStatements to guard against SQL
                // Injection
                PreparedStatement pStatement = connection.prepareStatement(selectQuery);) {
            // Only object creation statements can be in the parenthesis so
            // first try-with-resources block ends
            pStatement.setString(1, diet);
            // A new try-with-resources block begins for creating the ResultSet
            // object
            try (ResultSet resultSet = pStatement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(createFishData(resultSet));
                }
            }
        }
        LOG.log(Level.INFO, "# of records found : {0}", rows.size());
        return rows;
    }

    /**
     * Private method that creates an object of type FishData from the current
     * record in the ResultSet
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private FishData createFishData(ResultSet resultSet) throws SQLException {
        FishData fishData = new FishData();
        fishData.setCommonName(resultSet.getString("COMMONNAME"));
        fishData.setDiet(resultSet.getString("DIET"));
        fishData.setKh(resultSet.getString("KH"));
        fishData.setLatin(resultSet.getString("LATIN"));
        fishData.setPh(resultSet.getString("PH"));
        fishData.setFishSize(resultSet.getString("FISHSIZE"));
        fishData.setSpeciesOrigin(resultSet.getString("SPECIESORIGIN"));
        fishData.setStocking(resultSet.getString("STOCKING"));
        fishData.setTankSize(resultSet.getString("TANKSIZE"));
        fishData.setTemp(resultSet.getString("TEMP"));
        fishData.setId(resultSet.getInt("ID"));
        return fishData;
    }

    /**
     * This method adds a FishData object as a record to the database. The
     * column list does not include ID as this is an auto increment value in the
     * table.
     *
     * @param fishData
     * @return The number of records created, should always be 1
     * @throws SQLException
     */
    public int create(FishData fishData) throws SQLException {

        int result;
        String createQuery = "INSERT INTO FISH (COMMONNAME, LATIN, PH, KH, TEMP, FISHSIZE, SPECIESORIGIN, TANKSIZE, STOCKING, DIET) VALUES (?,?,?,?,?,?,?,?,?,?)";

        // Connection is only open for the operation and then immediately closed
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                // Using a prepared statement to handle the conversion
                // of special characters in the SQL statement and guard against
                // SQL Injection
                PreparedStatement ps = connection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, fishData.getCommonName());
            ps.setString(2, fishData.getLatin());
            ps.setString(3, fishData.getPh());
            ps.setString(4, fishData.getKh());
            ps.setString(5, fishData.getTemp());
            ps.setString(6, fishData.getFishSize());
            ps.setString(7, fishData.getSpeciesOrigin());
            ps.setString(8, fishData.getTankSize());
            ps.setString(9, fishData.getStocking());
            ps.setString(10, fishData.getDiet());

            result = ps.executeUpdate();
            
            // Retrieve generated primary key value and assign to bean
            try (ResultSet rs = ps.getGeneratedKeys();) {
                int recordNum = -1;
                if (rs.next()) {
                    recordNum = rs.getInt(1);
                }
                fishData.setId(recordNum);
                LOG.log(Level.FINEST, "New record ID is {0}", recordNum);
            }
        }
        LOG.log(Level.INFO, "# of records created : {0}", result);
        return result;
    }

    /**
     * This method deletes a single record based on the criteria of the primary
     * key field ID value. It should return either 0 meaning that there is no
     * record with that ID or 1 meaning a single record was deleted. If the
     * value is greater than 1 then something unexpected has happened. A
     * criteria other than ID may delete more than one record.
     *
     * @param id The primary key to use to identify the record that must be
     * deleted
     * @return The number of records deleted, should be 0 or 1
     * @throws SQLException
     */
    public int delete(int id) throws SQLException {

        int result;

        String deleteQuery = "DELETE FROM FISH WHERE ID = ?";

        // Connection is only open for the operation and then immediately closed
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                // You must use PreparedStatements to guard against SQL
                // Injection
                PreparedStatement ps = connection.prepareStatement(deleteQuery);) {
            ps.setInt(1, id);
            result = ps.executeUpdate();
        }
        LOG.log(Level.INFO, "# of records deleted : {0}", result);
        return result;
    }

    /**
     * This method will update all the fields of a record except ID. Usually
     * updates are tied to specific fields and so only those fields need appear
     * in the SQL statement.
     *
     * @param fishData An object with an existing ID and new data in the fields
     * @return The number of records updated, should be 0 or 1
     * @throws SQLException
     *
     */
    public int update(FishData fishData) throws SQLException {

        int result;

        String updateQuery = "UPDATE FISH SET COMMONNAME=?, LATIN=?, PH=?, KH=?, TEMP=?, FISHSIZE=?, SPECIESORIGIN=?, TANKSIZE=?, STOCKING=?, DIET=? WHERE ID = ?";

        // Connection is only open for the operation and then immediately closed
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                // You must use a prepared statement to handle the conversion
                // of special characters in the SQL statement and guard against
                // SQL Injection
                PreparedStatement ps = connection.prepareStatement(updateQuery);) {
            ps.setString(1, fishData.getCommonName());
            ps.setString(2, fishData.getLatin());
            ps.setString(3, fishData.getPh());
            ps.setString(4, fishData.getKh());
            ps.setString(5, fishData.getTemp());
            ps.setString(6, fishData.getFishSize());
            ps.setString(7, fishData.getSpeciesOrigin());
            ps.setString(8, fishData.getTankSize());
            ps.setString(9, fishData.getStocking());
            ps.setString(10, fishData.getDiet());

            result = ps.executeUpdate();
        }
        LOG.log(Level.INFO, "# of records updated : {0}", result);
        return result;
    }
}

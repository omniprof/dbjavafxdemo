package com.cejv416.dbjavafxdemo.unittests;

import com.cejv416.dbjavafxdemo.beans.FishData;
import com.cejv416.dbjavafxdemo.persistence.FishDAO;
import com.cejv416.dbjavafxdemo.persistence.FishDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * A basic unit test
 *
 * @author Ken Fogel
 * @version 2.0
 */
public class DemoTestCase {

    private static final Logger LOG = Logger.getLogger(DemoTestCase.class.getName());


    private final static String URL = "jdbc:mysql://localhost:3306/AQUARIUM"; //?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";
    // The MySQL USER must have Drop Table and Create Table privileges
    private final static String USER = "fish";
    private final static String PASSWORD = "kfstandard";

    /**
     * Simple test that retrieves all the records and checks the size of the
     * List
     *
     * @throws SQLException
     */
    @Test
    public void testFindAll() throws SQLException {
        FishDAO fd = new FishDAO();
        List<FishData> lfd = fd.findAll();
        assertEquals(200, lfd.size(), "testFindAll: ");
    }

    /**
     * Creates a record that will contain the same data as is found in the
     * record with an ID value of 6. The assertEquals will invoke the equals
     * method of the first object to compare it to the second. The reason for
     * having the equals() method.
     *
     * @throws SQLException
     */
    @Test
    public void testFindByID6() throws SQLException {
        FishData fishData1 = new FishData(6, "African Brown Knife", "Xenomystus nigri", "6.0-8.0", "5-19 dH", "72-78F",
                "12 in TL", "Africa", "", "", "Carnivore");
        FishDAO fd = new FishDAO();
        FishData fishData2 = fd.findID(6);
        assertEquals(fishData1, fishData2, "testFindByID6: ");
    }

    /**
     * The database is recreated before each test. If the last test is
     * destructive then the database is in an unstable state. @AfterClass is
     * called just once when the test class is finished with by the JUnit
     * framework. It is instantiating the test class anonymously so that it can
     * execute its non-static seedDatabase routine.
     */
    @AfterEach
    public void seedAfterTestCompleted() {
        LOG.info("@AfterClass seeding");
        new DemoTestCase().seedDatabase();
    }

    /**
     * This routine recreates the database before every test. This makes sure
     * that a destructive test will not interfere with any other test. Does not
     * support stored procedures.
     *
     * This routine is courtesy of Bartosz Majsak, an Arquillian developer at
     * JBoss
     */
    @BeforeEach
    public void seedDatabase() {
        LOG.info("@BeforeEach seeding");

        final String seedDataScript = loadAsString("createFishTable.sql");
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);) {
            for (String statement : splitStatements(new StringReader(seedDataScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed seeding database", e);
        }
    }

    /**
     * The following methods support the seedDatabse method
     */
    private String loadAsString(final String path) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
                Scanner scanner = new Scanner(inputStream)) {
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close input stream.", e);
        }
    }

    private List<String> splitStatements(Reader reader, String statementDelimiter) {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final StringBuilder sqlStatement = new StringBuilder();
        final List<String> statements = new LinkedList<>();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || isComment(line)) {
                    continue;
                }
                sqlStatement.append(line);
                if (line.endsWith(statementDelimiter)) {
                    statements.add(sqlStatement.toString());
                    sqlStatement.setLength(0);
                }
            }
            return statements;
        } catch (IOException e) {
            throw new RuntimeException("Failed parsing sql", e);
        }
    }

    private boolean isComment(final String line) {
        return line.startsWith("--") || line.startsWith("//") || line.startsWith("/*");
    }
}

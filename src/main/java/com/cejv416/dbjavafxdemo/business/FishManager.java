package com.cejv416.dbjavafxdemo.business;

import com.cejv416.dbjavafxdemo.beans.FishData;
import com.cejv416.dbjavafxdemo.persistence.FishDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business class that uses the persistence layer to retrieve records
 *
 * @author Ken Fogel
 * @version 1.1
 *
 */
public class FishManager {

    private static final Logger LOG = Logger.getLogger(FishManager.class.getName());
    

    private final FishDAO fishDAO;

    /**
     * Constructor
     */
    public FishManager() {
        fishDAO = new FishDAO();
    }

    /**
     * This method retrieves all the records and returns them as a string so
     * that the string can be displayed in the UI
     *
     * @return String containing all the fish
     */
    public String retrieveFish() {

        var sb = new StringBuilder();

        List<FishData> data;
        try {
            data = fishDAO.findAll();
            data.stream().forEach((fd) -> {
                sb.append(fd.toString()).append("\n");
            });
        } catch (SQLException e) {
            LOG.log(Level.SEVERE,"Error retrieving records: ", e.getCause());
            sb.append("\nSQL Error ").append(e.getMessage());
        }
        return sb.toString();
    }
}

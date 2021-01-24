package io.github.divios.epic_tabcompletefilter.databaseUtils;

import io.github.divios.epic_tabcompletefilter.EpicCommandsFilter;
import io.github.divios.epic_tabcompletefilter.utils;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class databaseManager {

    private static databaseManager instance = null;
    private static HashMap<String, List<String>> filters = null;
    private static EpicCommandsFilter main = EpicCommandsFilter.getInstance();

    public static databaseManager getInstance() {
        if (instance == null) init();
        return instance;
    }

    public static void init() {
        instance = new databaseManager();
        files.createDatabase();
        sqlite.getInstance();   /* Initiate sqlite */
        instance.createTables();
        instance.getDbFilters();
        instance.saveAllFiltersTask();
    }

    public void createTables() {

        utils.sync(() -> {
            try {
                Connection con = sqlite.getConnection();
                Statement statement = con.createStatement();
                statement.execute("CREATE TABLE IF NOT EXISTS filters"
                        + "(id varchar [255], command varchar [255]);");
            } catch (SQLException e) {
                e.printStackTrace();
                main.getLogger().severe("Couldn't load tables from database");
                main.getServer().getPluginManager().disablePlugin(main);
            }
        });
    }

    public void getDbFilters() {

        filters = new HashMap<>();
        utils.sync(() -> {
            try {
                Connection con = sqlite.getConnection();
                String selectTimer = "SELECT * FROM " + "filters";
                PreparedStatement statement = con.prepareStatement(selectTimer);
                ResultSet result = statement.executeQuery();
                                /* Instantiate the hashmap with the types and values*/
                while (result.next()) {
                    String resultStr = result.getString(1);
                    if(!filters.containsKey(resultStr)) filters.put(resultStr, new ArrayList<>());
                    filters.get(resultStr).add(result.getString(2));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void saveAllFilters() {
        utils.async(() -> {
            try {
                Connection con = sqlite.getConnection();
                String deleteTable = "DELETE FROM " + "filters;";
                PreparedStatement statement = con.prepareStatement(deleteTable);
                statement.executeUpdate();

                String insertItem = "INSERT INTO filters (id, command) VALUES (?, ?)";
                statement = con.prepareStatement(insertItem);
                /* Instantiate the hashmap with the types and values*/
                for(Map.Entry<String, List<String>> entry: filters.entrySet()) {
                    String id = entry.getKey();
                    for (String s: entry.getValue()) {
                        statement.setString(1, id);
                        statement.setString(2, s);
                        statement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void saveAllSyncFilters() {

            try {
                Connection con = sqlite.getConnection();
                String deleteTable = "DELETE FROM " + "filters;";
                PreparedStatement statement = con.prepareStatement(deleteTable);
                statement.executeUpdate();

                String insertItem = "INSERT INTO filters (id, command) VALUES (?, ?)";
                statement = con.prepareStatement(insertItem);
                /* Instantiate the hashmap with the types and values*/
                for(Map.Entry<String, List<String>> entry: filters.entrySet()) {
                    String id = entry.getKey();
                    for (String s: entry.getValue()) {
                        statement.setString(1, id);
                        statement.setString(2, s);
                        statement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }

    public void saveAllFiltersTask() {
        Bukkit.getScheduler().runTaskTimer(main, this::saveAllFilters,
                9600L, 9600L); //9600
    }

    public HashMap<String, List<String>> getFilters() {
        return filters;
    }

    public void createNewGroup(String group) {
        filters.put(group, new ArrayList<>());
    }

}

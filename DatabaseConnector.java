import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DatabaseConnector {
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/lifequest";
    private static final String USER = "root";
    private static final String PASS = "";
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
    public Map<String, Object> loadPlayerData(String playerName) throws SQLException {
        Map<String, Object> playerStats = new HashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM playerinfo WHERE name = ?")) {
            stmt.setString(1, playerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object columnValue = rs.getObject(columnName);

                        if (columnValue == null) {
                            if (metaData.getColumnType(i) == Types.INTEGER) {

                                playerStats.put(columnName, 0);
                            } else {

                                System.err.println("Warning: Null value found for column: " + columnName);
                                playerStats.put(columnName, "");
                            }
                        } else {
                            playerStats.put(columnName, columnValue);
                        }
                    }
                } else {
                    throw new SQLException("Player not found: " + playerName);
                }
            }
        }
        return playerStats;
    }
    public void updatePlayerData(String playerName, Map<String, Integer> newPlayerData) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE playerinfo SET level = ?, experience = ? WHERE name = ?"
             )) {
            stmt.setInt(1, newPlayerData.get("level"));
            stmt.setInt(2, newPlayerData.get("experience"));
            stmt.setString(3, playerName);
            stmt.executeUpdate();
        }
    }

    public Map<String, Object> loadRandomEnemyData() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM enemyinfo ORDER BY RAND() LIMIT 1")) {
            if (rs.next()) {
                Map<String, Object> enemyData = new HashMap<>();
                enemyData.put("name", rs.getString("name"));
                enemyData.put("health", rs.getInt("health"));
                enemyData.put("maxHealth", rs.getInt("maxHealth"));
                enemyData.put("strength", rs.getInt("strength"));
                enemyData.put("level", rs.getInt("level"));
                return enemyData;
            } else {
                throw new SQLException("No enemies found in the database.");
            }
        }
    }
    public List<Item> loadItems(List<String> itemNames, LifeQuestUI ui) throws SQLException {
        List<Item> items = new ArrayList<>();
        try (Connection conn = getConnection()) {
            for (String itemName : itemNames) {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM iteminfo WHERE name = ?");
                stmt.setString(1, itemName);
                ResultSet rs = null;
                try {
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        String name = rs.getString("name");
                        int atk = rs.getInt("atk");
                        int hp = rs.getInt("hp");
                        int mp = rs.getInt("mp");
                        String description = rs.getString("description");
                        String type = rs.getString("type");

                        String imagePath = "/resources/icons/" + name.toLowerCase() + ".png";
                        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));

                        Item item = new Item(name, atk, hp, mp, icon, description, type);
                        items.add(item);
                    }
                }
                finally {
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                }
            }
        }
        return items;
    }
    public List<Map<String, Object>> loadQuestData() throws SQLException {
        List<Map<String, Object>> quests = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM questinfo")) {
            while (rs.next()) {
                Map<String, Object> questData = new HashMap<>();
                questData.put("name", rs.getString("name"));
                questData.put("exp", rs.getInt("exp"));
                questData.put("description", rs.getString("description"));
                quests.add(questData);
            }
        }
        return quests;
    }
    public Map<String, Object> loadRandomQuestData() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM questinfo ORDER BY RAND() LIMIT 1")) {
            if (rs.next()) {
                Map<String, Object> questData = new HashMap<>();
                questData.put("name", rs.getString("name"));
                questData.put("exp", rs.getInt("exp"));
                questData.put("description", rs.getString("description"));
                return questData;
            } else {

                System.err.println("Error: No quests found in the database.");
                return null;
            }
        }
    }
}

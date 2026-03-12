package kz.ifihtich.aelo;

import java.io.File;
import java.sql.*;

public class Database {

    private Connection connection;
    private final File dbFile;


    public Database() {
        this.dbFile = new File(AElo.getInstance().getDataFolder(), "elo.db");
    }

    public void connect() throws SQLException{

        if (!dbFile.exists()){
            try {
                dbFile.createNewFile();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());


    }

    public void close(){
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void createTable() throws SQLException{
        String sql = "CREATE TABLE IF NOT EXISTS player_data (" + "nickname TEXT PRIMARY KEY," + "value INTEGER DEFAULT 0" + ");";
        try (Statement stmt = connection.createStatement()){
            stmt.execute(sql);
        }
    }

    public void addValue(String nickname, int amount) throws SQLException {
        String sql = "INSERT INTO player_data (nickname, value) VALUES (?, ?) " + "ON CONFLICT(nickname) DO UPDATE SET value = value + excluded.value;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nickname);
            ps.setInt(2, amount);
            ps.executeUpdate();
        }
    }

    public void setValue(String nickname, int amount) throws SQLException{
        String sql = "INSERT INTO player_data (nickname, value) VALUES (?, ?) " + "ON CONFLICT(nickname) DO UPDATE SET value = excluded.value;";

        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, nickname);
            ps.setInt(2, amount);
            ps.executeUpdate();
        }
    }

    public void removeValue(String nickname, int amount) throws SQLException {
        String sql = "UPDATE player_data SET value = value - ? WHERE nickname = ?;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setString(2, nickname);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                String insert = "INSERT INTO player_data (nickname, value) VALUES (?, ?);";
                try (PreparedStatement ps2 = connection.prepareStatement(insert)) {
                    ps2.setString(1, nickname);
                    ps2.setInt(2, -amount);
                    ps2.executeUpdate();
                }
            }
        }
    }

    public int getValue(String nickname) throws SQLException{
        String sql = "SELECT value FROM player_data WHERE nickname = ?;";
        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, nickname);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return  rs.getInt("value");
                }
            }
        }
        return 0;
    }
}

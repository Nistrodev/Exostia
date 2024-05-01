package fr.nistro.exostia.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class DatabaseUtil {
	private static DatabaseUtil instance;
	private Connection connection;

	public static DatabaseUtil getInstance() {
		if (DatabaseUtil.instance == null) {
			DatabaseUtil.instance = new DatabaseUtil();
		}

		return DatabaseUtil.instance;
	}
	
	public DatabaseUtil() {
		this.connectToDatabase();
	}

	public Connection getConnection() {
		return this.connection;
	}
	
	private void connectToDatabase() {
        final String host = Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("database.host");
        final String database = Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("database.database");
        final String username = Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("database.username");
        final String password = Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("database.password");

        final String url = "jdbc:mysql://" + host + "/" + database + "?useSSL=false";

        try {
            this.connection = DriverManager.getConnection(url, username, password);
            this.createTables();
            Bukkit.getLogger().info("Connected to the database !");
        } catch (final SQLException e) {
			Bukkit.getLogger().severe(ChatColor.RED + "Could not connect to the database !" + e.getMessage());
        }
    }
	
	public void createTables() {
		try {
			final PreparedStatement statement = this.connection.prepareStatement(
					"CREATE TABLE IF NOT EXISTS players (id INT AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(36) NOT NULL)");
			statement.executeUpdate();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isPlayerRegistered(UUID playerUUID) {
        try {
            final PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM players WHERE uuid = ?");
            statement.setString(1, playerUUID.toString());
            final ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (final SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

	public void registerPlayer(UUID playerUUID) {
        try {
            final PreparedStatement statement = this.connection.prepareStatement("INSERT INTO players (uuid) VALUES (?)");
            statement.setString(1, playerUUID.toString());
            statement.executeUpdate();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }
	
	public static Integer getPlayersCount() {
		try {
            final PreparedStatement statement = DatabaseUtil.getInstance().getConnection().prepareStatement("SELECT COUNT(*) FROM players");
            final ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) + 1;
        } catch (final SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

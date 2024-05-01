package fr.nistro.exostia;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import fr.nistro.exostia.command.ChatCommand;
import fr.nistro.exostia.command.WelcomeCommand;
import fr.nistro.exostia.listener.PlayerJoinListener;
import fr.nistro.exostia.listener.PlayerQuitListener;
import fr.nistro.exostia.listener.PlayerChat.AsyncPlayerChatListener;
import fr.nistro.exostia.util.ConfigUtil;
import fr.nistro.exostia.util.DatabaseUtil;
import fr.nistro.exostia.util.DependenciesUtil;

public class Main extends JavaPlugin implements Listener {
	
    private Connection connection = null;
    public static String prefix;
    public static Map<String, List<String>> playerMessages = new HashMap<>();
    private static List<String> swearWords;
    private final File swearWordsFile = new File(this.getDataFolder(), "swearWords.yml");

    
    @Override
	public void onEnable() {
    	// Vérification des dépendances
    	DependenciesUtil.checkDependencies();
    	
        // Enregistrement des listeners
    	this.getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
    	this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    	this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
    	
    	// Enregistrement des commandes
    	this.getCommand("chat").setExecutor(new ChatCommand());
    	this.getCommand("welcome").setExecutor(new WelcomeCommand());
    	
        this.saveDefaultConfig();
        this.saveDefaultSweardWords();
        
        // Référencement du préfixe
        Main.prefix = this.getConfig().getString("prefix"); 
        
        final ConfigUtil config = new ConfigUtil(this, "config.yml");
        config.save();
        
        final ConfigUtil swearWordsConfig = new ConfigUtil(this, "swearWords.yml");
        swearWordsConfig.save();
        Main.swearWords = swearWordsConfig.getConfig().getStringList("swearWords");
        
        this.connection = DatabaseUtil.getInstance().getConnection();
        
      
        this.getLogger().info("Plugin Exostia enabled !");
    }
    
    @Override
    public void onDisable() {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
    	
        this.getLogger().info("Plugin Exostia disabled !");
        this.getLogger().info(String.format("[%s] Disabled Version %s", this.getDescription().getName(), this.getDescription().getVersion()));
    }
    
	public static String getPrefix() {
		return Main.prefix;
	}
	
	public static List<String> getSwearWords() {
		return Main.swearWords;
	}
	
	public void saveDefaultSweardWords() {
		if (!this.swearWordsFile.exists()) {
			this.saveResource("swearWords.yml", false);
		}
    }
}

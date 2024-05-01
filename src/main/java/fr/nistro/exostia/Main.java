package fr.nistro.exostia;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import fr.nistro.exostia.command.ChatCommand;
import fr.nistro.exostia.command.WelcomeCommand;
import fr.nistro.exostia.listener.AsyncPlayerChatListener;
import fr.nistro.exostia.listener.PlayerJoinListener;
import fr.nistro.exostia.util.ConfigUtil;
import fr.nistro.exostia.util.DatabaseUtil;
import fr.nistro.exostia.util.VaultUtil;

public class Main extends JavaPlugin {
	
    private Connection connection = null;
    public static String prefix;
    public static Map<String, List<String>> playerMessages = new HashMap<>();

    
    @Override
	public void onEnable() {
    	if (!VaultUtil.setupEconomy() ) {
            this.getLogger().severe(String.format("Disabled due to no Vault dependency found!", this.getDescription().getName()));
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
    	
        // Enregistrement des listeners
    	this.getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
    	this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    	
    	// Enregistrement des commandes
    	this.getCommand("chat").setExecutor(new ChatCommand());
    	this.getCommand("welcome").setExecutor(new WelcomeCommand());
    	
        this.saveDefaultConfig();
        
        // Référencement du préfixe
        Main.prefix = this.getConfig().getString("prefix"); 
        
        final ConfigUtil config = new ConfigUtil(this, "config.yml");
        config.save();
        
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

}

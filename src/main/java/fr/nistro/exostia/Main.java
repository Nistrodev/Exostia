package fr.nistro.exostia;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import fr.nistro.exostia.command.ChatCommand;
import fr.nistro.exostia.listener.AsyncPlayerChatListener;
import fr.nistro.exostia.util.ConfigUtil;

public class Main extends JavaPlugin {
    
    public static String prefix;
    public static Map<String, List<String>> playerMessages = new HashMap<>();

    
    @Override
	public void onEnable() {
        // Enregistrement des listeners
    	this.getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
    	
    	// Enregistrement des commandes
    	this.getCommand("chat").setExecutor(new ChatCommand());
    	
        this.saveDefaultConfig();
        
        // Référencement du préfixe
        Main.prefix = this.getConfig().getString("prefix"); 
        
        final ConfigUtil config = new ConfigUtil(this, "config.yml");
        config.save();
        
      
        this.getLogger().info("Plugin Exostia enabled !");
    }
    
    @Override
    public void onDisable() {
        this.getLogger().info("Plugin Exostia disabled !");
    }
    
	public static String getPrefix() {
		return Main.prefix;
	}

}

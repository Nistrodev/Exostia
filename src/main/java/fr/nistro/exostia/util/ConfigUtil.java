package fr.nistro.exostia.util;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigUtil {
	private File file;
	private FileConfiguration config;
	
	public ConfigUtil(Plugin plugin, String path) {
		this(plugin.getDataFolder().getAbsolutePath() + "/" + path);
	}
	
	public ConfigUtil(String path) {
		this.file = new File(path);
		this.config = YamlConfiguration.loadConfiguration(this.file);
		this.config.options().copyDefaults(true);
	}
	
	public boolean save() {
		try {
			this.config.options().copyDefaults(true);
			this.config.save(this.file);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public FileConfiguration getConfig() {
		return this.config;
	}
}

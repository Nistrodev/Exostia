package fr.nistro.exostia.util;

import org.bukkit.Bukkit;

public class DependenciesUtil {
	public static void checkDependencies() {
    	if (!VaultUtil.setupEconomy() ) {
            Bukkit.getLogger().severe(String.format("Disabled due to no Vault dependency found!", Bukkit.getPluginManager().getPlugin("Exostia").getDescription().getName()));
            Bukkit.getServer().getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("Exostia"));
        }
    	
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
			Bukkit.getLogger().severe(String.format("Disabled due to no PlaceholderAPI dependency found!",
					Bukkit.getPluginManager().getPlugin("Exostia").getDescription().getName()));
			Bukkit.getServer().getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("Exostia"));
		}
	}
}

package fr.nistro.exostia.util;


import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultUtil {
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
	
	public static boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        VaultUtil.econ = rsp.getProvider();
        return VaultUtil.econ != null;
    }
    
	public static boolean setupChat() {
        final RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        VaultUtil.chat = rsp.getProvider();
        return VaultUtil.chat != null;
    }
    
	public static boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        VaultUtil.perms = rsp.getProvider();
        return VaultUtil.perms != null;
    }
    
    public static Economy getEconomy() {
        return VaultUtil.econ;
    }
    
    public static Permission getPermissions() {
        return VaultUtil.perms;
    }
    
    public static Chat getChat() {
        return VaultUtil.chat;
    }
}

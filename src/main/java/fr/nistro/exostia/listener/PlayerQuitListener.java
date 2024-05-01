package fr.nistro.exostia.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        
        PlayerJoinListener.sendActionBarToAllPlayers(PlaceholderAPI.setPlaceholders(player, Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.actionBarQuit").replace("%player%", player.getName())));
    }
}

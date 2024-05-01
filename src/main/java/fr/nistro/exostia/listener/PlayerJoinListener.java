package fr.nistro.exostia.listener;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.nistro.exostia.util.DatabaseUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class PlayerJoinListener implements Listener {
	private static Player newPlayer;
	private static Date firstJoin;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
    	// Show a message above the action bar
    	final Player player = event.getPlayer();
    	
    	PlayerJoinListener.sendActionBarToAllPlayers(PlaceholderAPI.setPlaceholders(player, Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.actionBarWelcome").replace("%player%", player.getName())));
    	
        final UUID playerUUID = event.getPlayer().getUniqueId();

        if (!DatabaseUtil.getInstance().isPlayerRegistered(playerUUID)) {
			PlayerJoinListener.newPlayer = event.getPlayer();
			PlayerJoinListener.firstJoin = new Date();
			
            // Enregistrement du joueur dans la base de données
            DatabaseUtil.getInstance().registerPlayer(playerUUID);
        }
    }
    
    // Méthode pour envoyer un message au-dessus de la barre d'action à tous les joueurs
    public static void sendActionBarToAllPlayers(String message) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(message).create());
        }
    }
	
	public static String getRandomWelcomeMessage() {
		final List<String> messages = Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getStringList("messages.welcomeMessages");
		
		return messages.get((int) (Math.random() * messages.size())).replace("%playerCount%", DatabaseUtil.getPlayersCount().toString()).replace("%player%", PlayerJoinListener.newPlayer.getName());
	}
	
	public static Date getFirstJoin() {
		return PlayerJoinListener.firstJoin;
	}
	
	public static Player getNewPlayer() {
		return PlayerJoinListener.newPlayer;
	}
}

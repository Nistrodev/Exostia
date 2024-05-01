package fr.nistro.exostia.listener;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.nistro.exostia.Main;
import fr.nistro.exostia.util.DatabaseUtil;

public class PlayerJoinListener implements Listener {
	private static Player newPlayer;
	private static Date firstJoin;

	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final UUID playerUUID = event.getPlayer().getUniqueId();

        if (!DatabaseUtil.getInstance().isPlayerRegistered(playerUUID)) {
			event.setJoinMessage(Main.getPrefix() + (Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.welcome").replace("%player%", event.getPlayer().getName()).replace("%playerCount%", DatabaseUtil.getPlayersCount().toString())));
			PlayerJoinListener.newPlayer = event.getPlayer();
			PlayerJoinListener.firstJoin = new Date();
			
            // Enregistrement du joueur dans la base de données
            DatabaseUtil.getInstance().registerPlayer(playerUUID);
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

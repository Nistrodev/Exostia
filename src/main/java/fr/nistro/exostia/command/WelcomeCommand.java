package fr.nistro.exostia.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nistro.exostia.Main;
import fr.nistro.exostia.listener.PlayerJoinListener;

public class WelcomeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Si la commande n'est pas envoyée par un joueur
		if (!(sender instanceof Player)) {
			sender.sendMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.noConsole"));
			return true;
		}
		
		final Player newPlayer = PlayerJoinListener.getNewPlayer();
		if (newPlayer == null) {
			sender.sendMessage(Main.getPrefix()
					+ Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.noNewPlayer"));
			return true;
		}
		
		final Player player = (Player) sender;
		
		if (newPlayer == player) {
			sender.sendMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig()
					.getString("messages.cantWelcomeYourself"));
			return true;
		}
		
		// In seconds
		final Integer maxTimeToWelcome = Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getInt("maxTimeToWelcome");
		final long timeSinceFirstJoin = (System.currentTimeMillis() - PlayerJoinListener.getFirstJoin().getTime()) / 1000;
		
		if (timeSinceFirstJoin > maxTimeToWelcome) {
			sender.sendMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig()
					.getString("messages.tooLateToWelcome"));
			return true;
		}

		final String welcomeMessage = PlayerJoinListener.getRandomWelcomeMessage();
        for (final Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Main.getPrefix() + welcomeMessage);
        }
		
		return true;
	}

}

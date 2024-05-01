package fr.nistro.exostia.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nistro.exostia.Main;
import fr.nistro.exostia.listener.PlayerJoinListener;
import fr.nistro.exostia.util.VaultUtil;
import net.milkbowl.vault.economy.EconomyResponse;

public class WelcomeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		final Player newPlayer = PlayerJoinListener.getNewPlayer();
		final Player player = (Player) sender;
		
		if (!sender.hasPermission(
				Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("permissions.welcome"))) {
			sender.sendMessage(Main.getPrefix()
					+ Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.noPermission"));
			return true;
		}
		
		// Si la commande n'est pas envoyée par un joueur
		if (!(sender instanceof Player)) {
			sender.sendMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.noConsole"));
			return true;
		}
		
		if (newPlayer == null) {
			sender.sendMessage(Main.getPrefix()
					+ Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.noNewPlayer"));
			return true;
		}
		
		// In seconds
		final Integer maxTimeToWelcome = Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getInt("maxTimeToWelcome");
		final long timeSinceFirstJoin = (System.currentTimeMillis() - PlayerJoinListener.getFirstJoin().getTime()) / 1000;
		
		if (timeSinceFirstJoin > maxTimeToWelcome) {
			sender.sendMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig()
					.getString("messages.tooLateToWelcome").replace("%player%", newPlayer.getName()));
			return true;
		}
		
		if (newPlayer == player) {
			sender.sendMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig()
					.getString("messages.cantWelcomeYourself"));
			return true;
		}

		final String welcomeMessage = PlayerJoinListener.getRandomWelcomeMessage();
        for (final Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Main.getPrefix() + welcomeMessage);
        }
        
        final EconomyResponse r = VaultUtil.getEconomy().depositPlayer(player, Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getDouble("rewards.welcome"));
		if (r.transactionSuccess()) {
			sender.sendMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig()
					.getString("messages.welcomeReward")
					.replace("%player%", newPlayer.getName())
					.replace("%reward%", String.valueOf(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getDouble("rewards.welcome"))));
		} else {
			sender.sendMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig()
					.getString("messages.welcomeRewardError").replace("%player%", newPlayer.getName()));
		}
		
		return true;
	}

}

package fr.nistro.exostia.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.nistro.exostia.Main;

public class ChatCommand implements CommandExecutor {

	public static boolean chatEnabled = true;
	public static Player playerDisabledChat;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		 final Player player = (Player) sender;
	        
	        if (args.length == 0) {
	            player.sendMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.chatUsage"));
	            return true;
	        }
	        
	        // Si le joueur a spécifié "clear" comme argument
	        if (args[0].equalsIgnoreCase("clear")) {
	        	this.clearChat(player, command, label, args);
	            return true;
	        }
	        
	        // Si le joueur a spécifié "toggle" comme argument
	        if (args[0].equalsIgnoreCase("toggle")) {
	        	this.toggleChat(player, command, label, args);
	            return true;
	        }
	        
	        // Si l'argument n'est ni "clear" ni "toggle"
	        player.sendMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.chatUsage"));
	        return true;
	}
	
	public boolean clearChat(Player sender, Command command, String label, String[] args) {
		if (!sender.hasPermission(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("permissions.clear"))) {
			sender.sendMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.noPermission"));
			return false;
		}
		for (int i = 0; i < 100; i++) {
			Bukkit.broadcastMessage("");
		}
		Bukkit.broadcastMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.chatCleared").replace("%player%", sender.getName()));
		return true;
	}
	
	public boolean toggleChat(Player sender, Command command, String label, String[] args) {
		if (!sender.hasPermission(
				Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("permissions.toggle"))) {
			sender.sendMessage(Main.getPrefix()
					+ Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.noPermission"));
			return false;
		}
		if (ChatCommand.chatEnabled) {
			ChatCommand.chatEnabled = false;
			ChatCommand.playerDisabledChat = sender;
			Bukkit.broadcastMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig()
					.getString("messages.chatDisabled").replace("%player%", sender.getName()));
		} else {
			ChatCommand.chatEnabled = true;
			ChatCommand.playerDisabledChat = null;
			Bukkit.broadcastMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig()
					.getString("messages.chatEnabled").replace("%player%", sender.getName()));
		}
		return true;
	}
	
	public static boolean getChatStatus() {
		return ChatCommand.chatEnabled;
	}
	
	public static Player getPlayerDisabledChat() {
		return ChatCommand.playerDisabledChat;
	}

}

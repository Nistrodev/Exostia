package fr.nistro.exostia.listener;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import fr.nistro.exostia.Main;
import fr.nistro.exostia.command.ChatCommand;
import fr.nistro.exostia.util.DiscordWebhookUtil;
import fr.nistro.exostia.util.ReflectionUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class AsyncPlayerChatListener implements Listener {
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        final Player player = event.getPlayer();
        String message = event.getMessage();
        
		if (!ChatCommand.chatEnabled && !player.hasPermission(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("permissions.chatBypass"))) {
			player.sendMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.chatDisabled").replace("%player%", ChatCommand.getPlayerDisabledChat().getName()));
			event.setCancelled(true);
			return;
		}
        
        // Gére les couleurs
        message = message.replace("&", "§");
        
        final FileConfiguration config = Bukkit.getPluginManager().getPlugin("Exostia").getConfig();
        final ConfigurationSection permissionPrefixSection = config.getConfigurationSection("permission_prefix");

        String prefixPlayer = config.getString("permission_prefix.Default.prefix");
        
        if (permissionPrefixSection != null) {
            for (final String key : permissionPrefixSection.getKeys(false)) {
                final String permission = permissionPrefixSection.getString(key + ".permission");
                final String prefix = permissionPrefixSection.getString(key + ".prefix");
                
                if ((permission != null) && (prefix != null) && player.hasPermission(permission)) {
                    prefixPlayer = prefix;
                    break;
                }
            }
        }
        
        // Créer un textComponent pour reporter le message
        final TextComponent reportMessage = new TextComponent();
        reportMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(this.getReportPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.hoverText"))}));
        reportMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + player.getName() + " " + message)); // Utilisation d'une commande fictive
        reportMessage.addExtra(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.symbol"));
        
        final TextComponent messageToSend = new TextComponent();
        messageToSend.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(new TextComponent(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.hoverTextResponse").replace("%player%", player.getName())))}));
        messageToSend.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName() + " "));
        // On ajoute le message de report
        messageToSend.addExtra(reportMessage);
        messageToSend.addExtra(prefixPlayer);
        messageToSend.addExtra(player.getName());
        messageToSend.addExtra(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.symbolBeforeMessage"));
        
     // Vérifie si le message contient [i]
     // Vérifie si le message contient [i]
        if (message.contains("[i]")) {
            // Trouve l'index de [i] dans le message
            final int index = message.indexOf("[i]");

            // Récupère la partie du message avant [i]
            final String messageBeforeItem = message.substring(0, index);

            // Ajoute la partie avant [i] au messageToSend
            messageToSend.addExtra(messageBeforeItem);

            // On récupère l'item en main du joueur
            if ((player.getInventory().getItemInMainHand() == null) || (player.getInventory().getItemInMainHand().getType() == Material.AIR)) {
                player.sendMessage(Main.getPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.noItemInHand"));
                event.setCancelled(true);
                return;
            }
			final ItemStack item = player.getInventory().getItemInMainHand();
			final TextComponent itemMessage = this.getItemTooltipMessage(player, item);
			// On ajoute l'item
			messageToSend.addExtra(itemMessage);

            // Récupère la partie du message après [i]
            final String messageAfterItem = message.substring(index + 3); // 3 est la longueur de "[i]"

            // Ajoute la partie après [i] au messageToSend
            messageToSend.addExtra(messageAfterItem);
        } else {
            // Si le message ne contient pas [i], on ajoute simplement le message
            messageToSend.addExtra(message);
        }

        
        
        // On envoie le message à tous les joueurs
        for (final Player p : Bukkit.getOnlinePlayers()) {
            p.spigot().sendMessage(messageToSend);
        }
        
        // On annule l'envoi du message
        event.setCancelled(true);
    }
    
    // Ajout de l'écouteur pour intercepter la commande fictive
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        final String message = event.getMessage();
        final Player player = event.getPlayer();
        
        // Vérifie si la commande est la commande fictive utilisée pour le report
        if (message.startsWith("/report ")) {
            // Divise le message en parties
            final String[] parts = message.split(" ", 3);
            if (parts.length == 3) {
                final String targetName = parts[1];
                final String reportMessage = parts[2];
                
                final Player target = Bukkit.getPlayer(targetName);
                
				if (player.getName().equals(targetName)) {
					player.sendMessage(this.getReportPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.cantReportYourself"));
					event.setCancelled(true);
					return;
				}
                
                if (target != null) {
                    // Appel de la méthode pour envoyer le report sur Discord
                    this.reportOnDiscordWebhook(player, target, reportMessage);
                    player.sendMessage(this.getReportPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.reportSent"));
                } else {
                    player.sendMessage(this.getReportPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.playerNotFound"));
                }
            } else {
                player.sendMessage(this.getReportPrefix() + Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.usage"));
            }
            // Annule l'événement pour empêcher l'exécution de la commande fictive
            event.setCancelled(true);
        }
    }
    
    public void reportOnDiscordWebhook(Player player, Player target, String message) {
    	if ((player == null) || (target == null) || (message == null)) {
			return;
		}
    	
        final DiscordWebhookUtil webhook = new DiscordWebhookUtil();
        
        webhook.setUsername(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.embed.username"));
        webhook.setAvatarUrl(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.embed.logo"));
        
        webhook.addEmbed(
                new DiscordWebhookUtil.EmbedObject()
                        .setTitle(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.embed.title"))
                        .addField(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.embed.player"), player.getName(), true)
                        .addField(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.embed.target"), target.getName(), true)
                        .addField(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.embed.message"), message, false)
                        .setColor(Color.RED)
                        .setThumbnail(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.embed.thumbnail"))
                        .setFooter(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.embed.footer"), Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.embed.logo"))
                        .setTimestampNow());
        
        
        try {
            webhook.execute();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
	public String getReportPrefix() {
        return Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("report.prefix");
	}
	
	@SuppressWarnings("deprecation")
	public String convertItemStackToJson(ItemStack itemStack) {
	    // ItemStack methods to get a net.minecraft.server.ItemStack object for serialization
	    final Class<?> craftItemStackClazz = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
	    final Method asNMSCopyMethod = ReflectionUtil.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);

	    // NMS Method to serialize a net.minecraft.server.ItemStack to a valid Json string
	    final Class<?> nmsItemStackClazz = ReflectionUtil.getNMSClass("ItemStack");
	    final Class<?> nbtTagCompoundClazz = ReflectionUtil.getNMSClass("NBTTagCompound");
	    final Method saveNmsItemStackMethod = ReflectionUtil.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

	    Object nmsNbtTagCompoundObj; // This will just be an empty NBTTagCompound instance to invoke the saveNms method
	    Object nmsItemStackObj; // This is the net.minecraft.server.ItemStack object received from the asNMSCopy method
	    Object itemAsJsonObject; // This is the net.minecraft.server.ItemStack after being put through saveNmsItem method

	    try {
	        nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
	        nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
	        itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
	    } catch (final Throwable t) {
	        Bukkit.getLogger().log(Level.SEVERE, "failed to serialize itemstack to nms item", t);
	        return null;
	    }

	    // Return a string representation of the serialized object
	    return itemAsJsonObject.toString();
	}
	
	public TextComponent getItemTooltipMessage(Player player, ItemStack item) {
	    final String itemJson = this.convertItemStackToJson(item);
	    String itemName = item.getItemMeta().getDisplayName();
	    final Integer itemAmount = item.getAmount();
		if (itemName == null) {
	        itemName = WordUtils.capitalizeFully(item.getType().name().replace("_", " "));
		}

	    // Prepare a BaseComponent array with the itemJson as a text component
	    final BaseComponent[] hoverEventComponents = {
	            new TextComponent(itemJson) // The only element of the hover events basecomponents is the item json
	    };

	    // Create the hover event
	    final HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

	    /* And now we create the text component (this is the actual text that the player sees)
	     * and set it's hover event to the item event */
	    final TextComponent component = new TextComponent(Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getString("messages.itemSymbol").replace("%itemName%", itemName).replace("%itemAmount%", itemAmount.toString()));
	    component.setHoverEvent(event);
	    
	    return component;
	}
    
}

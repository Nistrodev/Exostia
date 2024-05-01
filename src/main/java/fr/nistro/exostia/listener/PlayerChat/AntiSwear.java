package fr.nistro.exostia.listener.PlayerChat;

import java.util.List;

import org.bukkit.Bukkit;

import fr.nistro.exostia.Main;

public class AntiSwear {
	
	public static String checkSwearWords(String message) {
		if (!Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getBoolean("antiSwear.enabled")) {
			return message;
		}
		final List<String> swearWords = Main.getSwearWords();
		
		if (swearWords.isEmpty()) {
			return message;
		}

		for (final String word : swearWords) {
			if (message.toLowerCase().contains(word.toLowerCase())) {
				return AntiSwear.getReplacementPhrases();
			}
		}

		return message;
	}
	
	public static String getReplacementPhrases() {
		final List<String> replacementPhrases = Bukkit.getPluginManager().getPlugin("Exostia").getConfig().getStringList("antiSwear.replacementPhrases");
		
		if (replacementPhrases.isEmpty()) {
			return null;
		}
		
		return replacementPhrases.get((int) (Math.random() * replacementPhrases.size()));
	}
}

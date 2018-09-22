package net.syntaxjedi.betterkits;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.external.EZPlaceholderHook;

public class PlaceholderHandler extends EZPlaceholderHook {
	private BetterKits plugin;

	public PlaceholderHandler(BetterKits plugin) {
		super(plugin, "betterkits");
		this.plugin = plugin;
	}

	@Override
	public String onPlaceholderRequest(Player p, String type) {
		if(p == null){
			return "";
		}
		
		switch(type){
		case "level":
			int level = PlayerHandler.getLevel(p);
			return Integer.toString(level);
		case "points":
			int points = PlayerHandler.getPoints(p);
			return Integer.toString(points);
		case "kills":
			int kills = PlayerHandler.getKills(p);
			return Integer.toString(kills);
		case "deaths":
			int deaths = PlayerHandler.getDeaths(p);
			return Integer.toString(deaths);
		case "kd":
			Double kd = PlayerHandler.getKD(p);
			return Double.toString(kd);
		case "streak":
			int streak = PlayerHandler.getStreak(p);
			return Integer.toString(streak);
		}
		
		return null;
	}

}

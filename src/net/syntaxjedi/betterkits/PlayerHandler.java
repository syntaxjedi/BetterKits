package net.syntaxjedi.betterkits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.md_5.bungee.api.ChatColor;

public class PlayerHandler implements Listener{
	private static final Logger log = Logger.getLogger("Minecraft");
	Map<UUID, Integer> killStreak = new HashMap<UUID, Integer>();
	double exp = 0;
	double newExp = 10;
	int level = 0;
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		if(e.getEntity().getKiller() instanceof Player){
			Player killer = e.getEntity().getKiller();
			e.setDeathMessage(p.getDisplayName() + " was pwnd by " + killer.getDisplayName());
			killer.sendMessage(ChatColor.GOLD + "You gained " + ChatColor.GREEN + "10 " + ChatColor.GOLD + "experience.");
			editExp(killer, 10);
			killStreak(killer, false);
			GUIHandler.setScoreboard(p, false, 0, 0, 1);
		}else{
			e.setDeathMessage(p.getDisplayName() + " died an unfortunate death");
		}
	}
	
	public void editExp(Player p, int amount){
		Map<String, Object> expMod = FileHandler.getExpConfig();
		
		String type = (String) expMod.get("type");
		double modifier = (Double) expMod.get("mod");
		double base = (Double) expMod.get("base");
		exp += amount;
		
		if(type.equals("exponential")){
			if(exp >= newExp){
				level += 1;
				newExp = Math.round(Math.pow(modifier, level)*base);
				exp = 0;
				p.sendMessage(ChatColor.GOLD + "Level Up!" + "\nnew level: " + ChatColor.GREEN + level + ChatColor.GOLD + "\nexp needed for next level: " + ChatColor.GREEN + newExp);
			}
		}else if(type.equals("linear")){
			if(exp >= newExp){
				level += 1;
				newExp = Math.round(base*modifier);
				exp = 0;
				p.sendMessage(ChatColor.GOLD + "Level Up!\nnew level: " + ChatColor.GREEN + level + ChatColor.GOLD + "\nexp needed for next level: " + ChatColor.GREEN + newExp);
			}
		}
	}
	
	public void killStreak(Player p, Boolean end){
		UUID unique = p.getUniqueId();
		if(killStreak.containsKey(unique) && end == false){
			int kills = killStreak.get(unique);
			kills++;
			killStreak.put(unique, kills);
			if(kills == 5){
				p.sendMessage(ChatColor.GOLD + "You've activated the killstreak " + ChatColor.BLUE + "Devestater" + ChatColor.GOLD + "!\nYour current streak is: " + ChatColor.GREEN + kills);
			}
			GUIHandler.setScoreboard(p, false, kills, 1, 0);
		}else if(killStreak.containsKey(unique) && end == true){
			killStreak.put(unique, 0);
		}else if(!(killStreak.containsKey(unique)) && end == false){
			killStreak.put(unique, 1);
			GUIHandler.setScoreboard(p, false, 1, 1, 0);
		}else{
			return;
		}
	}
}

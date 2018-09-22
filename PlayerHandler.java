package net.syntaxjedi.betterkits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class PlayerHandler implements Listener{
	private static final Logger log = Logger.getLogger("Minecraft");
	static Map<UUID, Map<String, Integer>> killsDeaths = new HashMap<UUID, Map<String, Integer>>();
	static Map<UUID, Boolean> canGetKit = new HashMap<UUID, Boolean>();
	static Plugin plugin = BetterKits.getPlugin(BetterKits.class);
	double exp = 0;
	int level = 0;
	
	private static String type;
	private static double modifier;
	private static double base;
	private static int perKill;
	private static int pointsAmount;
	private static Boolean perLevel = false;
	private static int atEach;
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		SQLHandler.getStats(p);
		canGetKit.put(p.getUniqueId(), true);
		//((BetterKits)plugin).giveKit(p, "fighter");
	}
	
	@EventHandler
	public void playerLeave(PlayerQuitEvent e){
		Player p = e.getPlayer();
		SQLHandler.saveStats(p, killsDeaths.get(p.getUniqueId()).get("kills"), 
				killsDeaths.get(p.getUniqueId()).get("deaths"), 
				killsDeaths.get(p.getUniqueId()).get("exp"), 
				killsDeaths.get(p.getUniqueId()).get("level"), 
				killsDeaths.get(p.getUniqueId()).get("nextLevel"),
				"Kit 1", "Kit 2", "Kit 3", 
				killsDeaths.get(p.getUniqueId()).get("kit").toString(), 
				killsDeaths.get(p.getUniqueId()).get("points"));
		killsDeaths.remove(p.getUniqueId());
		canGetKit.remove(p.getUniqueId());
	}
	
	public static void getConfig(){
		Map<String, Object> expMod = FileHandler.getExpConfig();
		
		type = (String) expMod.get("type");
		modifier = (Double) expMod.get("mod");
		base = (Double) expMod.get("base");
		perKill = (Integer) expMod.get("perKill");
		pointsAmount = (Integer) expMod.get("pAmount");
		if(expMod.get("pPerLevel").equals(true)){
			atEach = (Integer) expMod.get("perLevel");
			perLevel = true;
		}
	}
	
	public static boolean getKitBool(Player p){
		Boolean available = canGetKit.get(p.getUniqueId());
		return available;
	}
	
	public static void setKitBool(Player p){
		Boolean setAvailable = canGetKit.get(p.getUniqueId());
		if(setAvailable){
			canGetKit.put(p.getUniqueId(), false);
		}else{
			canGetKit.put(p.getUniqueId(), true);
		}
	}
	
	public static Map<String, Integer> getInfo(Player p){
		Map<String, Integer> playerMap = killsDeaths.get(p.getUniqueId());
		return playerMap;
	}
	
	public static int getPoints(Player p){
		if(killsDeaths.containsKey(p.getUniqueId())){
			int points = killsDeaths.get(p.getUniqueId()).get("points");
			return points;
		}else{
			return 0;
		}
	}
	
	public static int getLevel(Player p){
		if(killsDeaths.containsKey(p.getUniqueId())){
			int level = killsDeaths.get(p.getUniqueId()).get("level");
			return level;	
		}else{
			return 0;
		}
	}
	
	public static int getKit(Player p) {
		if(killsDeaths.containsKey(p.getUniqueId())) {
			int kitLevel = killsDeaths.get(p.getUniqueId()).get("kit");
			return kitLevel;
		}else {
			return 1;
		}
	}
	
	public static int getStreak(Player p){
		if(killsDeaths.containsKey(p.getUniqueId())){
			int streak = killsDeaths.get(p.getUniqueId()).get("streak");
			return streak;			
		}else{
			return 0;
		}

	}
	
	public static int getKills(Player p){
		if(killsDeaths.containsKey(p.getUniqueId())){
			int kills = killsDeaths.get(p.getUniqueId()).get("kills");
			return kills;
		}else{
			return 0;
		}
	}
	
	public static int getDeaths(Player p){
		if(killsDeaths.containsKey(p.getUniqueId())){
			int deaths = killsDeaths.get(p.getUniqueId()).get("deaths");
			return deaths;
		}else{
			return 0;
		}
	}
	
	public static double getKD(Player p){
		if(killsDeaths.containsKey(p.getUniqueId()) && killsDeaths.get(p.getUniqueId()).get("kills") >= 1){
			double kD = killsDeaths.get(p.getUniqueId()).get("kills") / killsDeaths.get(p.getUniqueId()).get("deaths");
			return kD;
		}else{
			return 0.0;
		}
	}
	
	public static void setPoints(Player p, int amount){
		if(!killsDeaths.containsKey(p.getUniqueId())){
			setStats(p, 0, 0, 1, 0, 0, amount, 1);
		}else{
			Map<String, Integer> playerStats = killsDeaths.get(p.getUniqueId());
			int points = playerStats.get("points");
			points = amount;
			playerStats.put("points", points);
			killsDeaths.put(p.getUniqueId(), playerStats);
		}
	}
	
	public static void givePoints(Player p, int amount){
		if(!killsDeaths.containsKey(p.getUniqueId())){
			setStats(p, 0, 0, 1, 0, 0, amount, 1);
		}else{
			Map<String, Integer> playerStats = killsDeaths.get(p.getUniqueId());
			int points = playerStats.get("points");
			points += amount;
			playerStats.put("points", points);
			killsDeaths.put(p.getUniqueId(), playerStats);
		}
	}
	
	public static void setKit(Player p, int kitLevel) {
		Map<String, Integer> currentStats = killsDeaths.get(p.getUniqueId());
		int kit = currentStats.get("kit");
		kit = kitLevel;
		currentStats.put("kit", kit);
		log.info("Kit level: " + kit);
		killsDeaths.put(p.getUniqueId(), currentStats);
	}
	
	public static void setStats(Player p, int kills, int deaths, int level, int nextLevel, int exp, int points, int kit){
		Map<String, Integer> newStats = new HashMap<String, Integer>();
		newStats.put("kills", kills);
		newStats.put("deaths", deaths);
		newStats.put("streak", 0);
		newStats.put("exp", exp);
		newStats.put("level", level);
		newStats.put("points", points);
		newStats.put("kit", kit);
		if(type.equalsIgnoreCase("exponential") && nextLevel == 0){
			newStats.put("nextLevel", (int) Math.round(Math.pow(modifier, 0)*base));
		}else if(type.equalsIgnoreCase("linear") && nextLevel == 0){
			newStats.put("nextLevel", (int) Math.round(base*modifier));
		}else if(nextLevel >= 1){
			newStats.put("nextLevel", nextLevel);
		}
		
		
		killsDeaths.put(p.getUniqueId(), newStats);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		setKitBool(p);
		if(e.getEntity().getKiller() instanceof Player){
			
			Player killer = e.getEntity().getKiller();
			e.setDeathMessage(p.getDisplayName() + " was pwnd by " + killer.getDisplayName());
			
			editExp(killer);
			killStreak(killer);
			
		}else{
			e.setDeathMessage(p.getDisplayName() + " died an unfortunate death");	
		}
		if(killsDeaths.containsKey(p.getUniqueId())){
			Map<String, Integer> trackStats = killsDeaths.get(p.getUniqueId());
			int currentDeaths = trackStats.get("deaths");
			currentDeaths++;
			trackStats.put("deaths", currentDeaths);
			trackStats.put("streak", 0);
			killsDeaths.put(p.getUniqueId(), trackStats);
			if(trackStats.get("kills") == 0) {
				GUIHandler.setScoreboard(p, trackStats.get("kills"), currentDeaths, 0, 0.0);
			}else if(trackStats.get("kills") > 0) {
				double killRatio = Math.round(((double)trackStats.get("kills") / (double)currentDeaths) * 100.0) / 100.0;
				GUIHandler.setScoreboard(p, trackStats.get("kills"), currentDeaths, 0, killRatio);
			}
			
		}else if(!(killsDeaths.containsKey(p.getUniqueId()))){
			setStats(p, 0, 1, 0, (int)base, 0, 0, 1);
			GUIHandler.setScoreboard(p, 0, 1, 0, 0.0);
		}
	}
	
	public void editExp(Player p){
		Map<String, Integer> playerStats = killsDeaths.get(p.getUniqueId());
		int level = playerStats.get("level");
		int exp = playerStats.get("exp");
		exp += perKill;
		
		long newExp = playerStats.get("nextLevel");
		
		p.sendMessage(ChatColor.GOLD + "You gained " + ChatColor.GREEN + perKill + ChatColor.GOLD + " experience.");
		if(type.equals("exponential")){
			if(exp >= (int) newExp){
				level += 1;
				newExp = Math.round(Math.pow(modifier, level)*base);
				exp = 0;
				if(perLevel){
					if(level%atEach == 0 && level >= 1){
						int points = playerStats.get("points");
						points += pointsAmount;
						playerStats.put("points", points);
						p.sendMessage(ChatColor.GOLD + "You Got " + ChatColor.BLUE + pointsAmount + ChatColor.GOLD + " Points!");
					}
				}
				p.sendMessage(ChatColor.GOLD + "Level Up!" + "\nnew level: " + ChatColor.GREEN + level + ChatColor.GOLD + "\nexp needed for next level: " + ChatColor.GREEN + newExp);
			}
		}else if(type.equals("linear")){
			if(exp >= (int) newExp){
				level += 1;
				newExp = Math.round(level*modifier);
				exp = 0;
				if(perLevel){
					if(level%atEach == 0 && level >= 1){
						int points = playerStats.get("points");
						points += pointsAmount;
						playerStats.put("points", points);
						p.sendMessage(ChatColor.GOLD + "You Got " + ChatColor.BLUE + pointsAmount + ChatColor.GOLD + " Points!");
					}
				}
				p.sendMessage(ChatColor.GOLD + "Level Up!\nnew level: " + ChatColor.GREEN + level + ChatColor.GOLD + "\nexp needed for next level: " + ChatColor.GREEN + newExp);
			}
		}
		playerStats.put("exp", exp);
		playerStats.put("nextLevel", (int) newExp);
		playerStats.put("level", level);
		killsDeaths.put(p.getUniqueId(), playerStats);
	}
	
	public void killStreak(Player p){
		UUID unique = p.getUniqueId();
		Map<String, Integer> streak = killsDeaths.get(unique);
		if(killsDeaths.containsKey(unique)){
			int kills = streak.get("kills");
			kills++;
			streak.put("kills", kills);
			
			int currentStreak = streak.get("streak");
			currentStreak++;
			streak.put("streak", currentStreak);
			
			killsDeaths.put(unique, streak);
			if(kills == 5){
				p.sendMessage(ChatColor.GOLD + "You've activated the killstreak " + ChatColor.BLUE + "Devestater" + ChatColor.GOLD + "!\nYour current streak is: " + ChatColor.GREEN + kills);
			}
			int deaths = streak.get("deaths");
			if(streak.get("deaths") == 0) {
				GUIHandler.setScoreboard(p, kills, streak.get("deaths"), currentStreak, kills + 0.0);
			}else if(streak.get("deaths") >= 1) {
				double killRatio = Math.round(((double)kills / (double)streak.get("deaths")) * 100.0)/100.0;
				GUIHandler.setScoreboard(p, kills, streak.get("deaths"), currentStreak, killRatio);
			}
			
		}else if(!(killsDeaths.containsKey(unique))){
			setStats(p, 1, 0, 0, (int)base, perKill, 0, 1);
			GUIHandler.setScoreboard(p, 1, 0, 1, 1.0);
		}
	}
}

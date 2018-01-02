package net.syntaxjedi.betterkits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import net.md_5.bungee.api.ChatColor;
import net.syntaxjedi.betterkits.Commands;

public class BetterKits extends JavaPlugin implements Listener{
	private static final Logger log = Logger.getLogger("Minecraft");
	public static Map<String, Integer> taskID = new HashMap<String, Integer>();
	static BetterKits plugin;
	
	BukkitScheduler updateScoreboard = this.getServer().getScheduler();
	
	@Override
	public void onEnable(){
		log.info("[Better Kits] Loading Files");
		this.saveDefaultConfig();
		log.info("[Better Kits] Registering Commands");
		Commands commands = new Commands();
		this.getCommand("kit").setExecutor(commands);
		log.info("[Better Kits] Registering Events");
		this.getServer().getPluginManager().registerEvents(new GUIHandler(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerHandler(), this);
		this.getServer().getPluginManager().registerEvents(this, this);
		plugin = this;
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent e){
		final Player p = e.getPlayer();
		GUIHandler.setScoreboard(p, true, 0, 0, 0);
	}
	
	public static ItemStack createItem(Material material, int amount, String name, String type){
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		if(name != null) meta.setDisplayName(name);
		if(type != null){
			List<String> lore = new ArrayList<String>();
			lore.add(type);
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public void giveKit(Player p, String kit){
		Location loc = p.getLocation();
		ItemStack[] kitItems = FileHandler.getKit(kit);
		Inventory inv = p.getInventory();
		for(int i = 0; i<kitItems.length; i++){
			if(kitItems[i] != null){
				if(inv.firstEmpty() == -1){
					loc.getWorld().dropItemNaturally(loc, kitItems[i]);
				}else{
					inv.setItem(inv.firstEmpty(), kitItems[i]);
				}
			}
		}
	}
	
	//schedule task
	public static void setTimeout(final Player p, String name){
		log.info("Timeout: " + FileHandler.getLong());
		GUIHandler.timeout = true;
		Long ticks = FileHandler.getLong();
		final int tid = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			public void run(){
				p.sendMessage(ChatColor.GREEN + "You can claim another kit!");
				GUIHandler.timeout = false;
				endTask(name);
			}
		}, ticks, ticks);
		taskID.put(name, tid);
	}
	
	//end task
	public static void endTask(String name){
		if(taskID.containsKey(name)){
			int tid = taskID.get(name); //get id
			plugin.getServer().getScheduler().cancelTask(tid); //cancel task
			taskID.remove(name); //remove player
		}
	}
	
	public static Boolean getList(String name){
		if(taskID.containsKey(name)){
			log.info("true");
			return true;
		}
		log.info("false");
		return false;
	}
}

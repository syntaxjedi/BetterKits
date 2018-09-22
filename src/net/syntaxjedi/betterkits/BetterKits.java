package net.syntaxjedi.betterkits;

import java.io.IOException;
import java.sql.SQLException;
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
import org.bukkit.configuration.InvalidConfigurationException;
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
		FileHandler.checkFiles();
		this.saveDefaultConfig();
		PlayerHandler.getConfig();
		try {
			FileHandler.loadKits();
		} catch (IOException | InvalidConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		log.info("[Better Kits] Registering Commands");
		Commands commands = new Commands();
		//this.getCommand("kit").setExecutor(commands);
		this.getCommand("adminkit").setExecutor(commands);
		log.info("[Better Kits] Registering Events");
		this.getServer().getPluginManager().registerEvents(new GUIHandler(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerHandler(), this);
		this.getServer().getPluginManager().registerEvents(new Commands(), this);
		this.getServer().getPluginManager().registerEvents(this, this);
		log.info("[Better Kits] Checking Dependencies");
		if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
			log.info("[Better Kits] Placeholder API Found");
		}else{
			log.info("[Better Kits] Download Placeholder API For Extra Functionalities");
		}
		if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
			new PlaceholderHandler(this).hook();
		}
		log.info("[Better Kits] Checking Database");
		try {
			SQLHandler.tryConnect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		plugin = this;
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
	
	public static Boolean getList(String name){
		if(taskID.containsKey(name)){
			return true;
		}
		return false;
	}
}

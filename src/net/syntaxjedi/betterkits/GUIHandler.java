package net.syntaxjedi.betterkits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import net.md_5.bungee.api.ChatColor;
import net.syntaxjedi.betterkits.FileHandler;

public class GUIHandler implements Listener {
	private static final Logger log = Logger.getLogger("Minecraft");
	static Plugin plugin = BetterKits.getPlugin(BetterKits.class);
	public static Inventory inv;
	public static Boolean timeout = false;
	
	public static void editKits(Player p){
		inv = Bukkit.createInventory(p, 18, "Edit Kits");
		inv.setItem(1, new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, "Level 2", "Level 2 cost: " + FileHandler.getKitCost(2))));
		inv.setItem(3, new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, "Level 4", "Level 4 cost: " + FileHandler.getKitCost(4))));
		inv.setItem(5, new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, "Level 6", "Level 6 cost: " + FileHandler.getKitCost(6))));
		inv.setItem(7, new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, "Level 8", "Level 8 cost: " + FileHandler.getKitCost(8))));
		inv.setItem(9, new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, "Level 1", "Level 1 cost: " + FileHandler.getKitCost(1))));
		inv.setItem(11, new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, "Level 3", "Level 3 cost: " + FileHandler.getKitCost(3))));
		inv.setItem(13, new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, "Level 5", "Level 5 cost: " + FileHandler.getKitCost(5))));
		inv.setItem(15, new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, "Level 7", "Level 7 cost: " + FileHandler.getKitCost(7))));
		inv.setItem(17, new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, "Level 9", "Level 9 cost: " + FileHandler.getKitCost(9))));
		p.openInventory(inv);
	}
	
	public static void showKits(Player p, int points, int level){
		inv = Bukkit.createInventory(p, 27, "Kits Overview");
		ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
		ItemMeta meta = glass.getItemMeta();
		meta.setDisplayName(" ");
		glass.setItemMeta(meta);
		//glass.getItemMeta().setDisplayName("1");
		for(int i = 0; i < 27; i++){
			inv.setItem(i, glass);
		}
		int nextLevel = level + 1;
		ItemStack current = new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, ChatColor.BLUE + "Current Kit", ChatColor.GRAY + "Kit Level: " + PlayerHandler.getKit(p)));
		ItemStack next = new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, ChatColor.YELLOW + "Next Kit", ChatColor.GRAY + "Kit Level: " + nextLevel));
		ItemMeta currentMeta = current.getItemMeta();
		ItemMeta nextMeta = next.getItemMeta();
		
		List<String> currentLore = current.getItemMeta().getLore();
		List<String> nextLore = next.getItemMeta().getLore();
		
		currentLore.add(ChatColor.GRAY + "Left Click: View");
		currentLore.add(ChatColor.GRAY + "Right Click: Take");
		nextLore.add(ChatColor.GRAY + "Kit Cost: " + ChatColor.GREEN + "$" + FileHandler.getKitCost(nextLevel));
		nextLore.add(ChatColor.GRAY + "Left Click: View");
		nextLore.add(ChatColor.GRAY + "Right Click: Buy");
		
		currentMeta.setLore(currentLore);
		nextMeta.setLore(nextLore);
		
		current.setItemMeta(currentMeta);
		next.setItemMeta(nextMeta);
		
		inv.setItem(12, current);
		inv.setItem(14, next);
		inv.setItem(13, new ItemStack(BetterKits.createItem(Material.EMERALD, 1, ChatColor.GREEN + "Balance:", ChatColor.YELLOW + "$" + points)));
		p.openInventory(inv);
	}
	
	@EventHandler
	public static void inventoryClick(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		if(e.getCurrentItem() == null || e.getCurrentItem().equals(Material.AIR)){
			return;
		}
		if(e.getInventory().getName().contains("Edit")){
			e.setCancelled(true);
			if(p.hasPermission("kit.admin")){
				if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Level")){
					editInv(p, e.getCurrentItem().getItemMeta().getDisplayName());
				}
			}
		}else if(e.getInventory().getName().contains("Overview")){
			e.setCancelled(true);
			if(e.getClick().isLeftClick()){
				if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Current")){
					//((BetterKits)plugin).giveKit(p, "fighter");
					openInv(p, e.getCurrentItem().getItemMeta().getDisplayName(), PlayerHandler.getKit(p));
				}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Next")) {
					openInv(p, e.getCurrentItem().getItemMeta().getDisplayName(), PlayerHandler.getKit(p));
				}
			}else if(e.getClick().isRightClick()){
				if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Current")){
					if(PlayerHandler.getKitBool(p) == true){
						String kit = "kit" + PlayerHandler.getKit(p);
						((BetterKits)plugin).giveKit(p, kit);
						PlayerHandler.setKitBool(p);
						p.closeInventory();
					}else{
						p.closeInventory();
						p.sendMessage(ChatColor.RED + "You can't claim this kit yet!");
					}
				}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Next")){
					int level = PlayerHandler.getKit(p);
					int cost = FileHandler.getKitCost(level + 1);
					int points = PlayerHandler.getPoints(p);
					if(points >= cost) {
						PlayerHandler.setPoints(p, points - cost);
						PlayerHandler.setKit(p, level + 1);
						p.closeInventory();
						p.sendMessage(ChatColor.GREEN + "You bought Kit" + (level + 1) + "!");
					}else{
						p.closeInventory();
						p.sendMessage(ChatColor.RED + "You need " + (cost - points) + " more points for this kit.");
					}
				}
			}
			
			
		}else if(e.getInventory().getName().contains("Current") || e.getInventory().getName().contains("Next")) {
			e.setCancelled(true);
			if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("back")) {
				showKits(p, PlayerHandler.getPoints(p), PlayerHandler.getKit(p));
			}else if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Upgrade")) {
				int level = PlayerHandler.getKit(p);
				int cost = FileHandler.getKitCost(level + 1);
				int points = PlayerHandler.getPoints(p);
				if(points >= cost) {
					PlayerHandler.setPoints(p, points - cost);
					PlayerHandler.setKit(p, level + 1);
					p.closeInventory();
					p.sendMessage(ChatColor.GREEN + "You bought Kit" + (level + 1) + "!");
				}else{
					p.closeInventory();
					p.sendMessage(ChatColor.RED + "You need " + (cost - points) + " more points for this kit.");
				}
			}
		}else if(e.getInventory().getName().contains("Level")){
			if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("back")){
				e.setCancelled(true);
				editKits(p);
			}else if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("clear")){
				e.setCancelled(true);
				return;
			}else if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("save")){
				e.setCancelled(true);
				saveInv(p, e.getInventory(), e.getInventory().getName().toLowerCase());
			}else if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("cancel")){
				e.setCancelled(true);
				editKits(p);
				return;
			}
		}
	}
	
	public static void saveInv(Player p, Inventory openInv, String name){
		ItemStack[] items = new ItemStack[27];
		inv = openInv;
		for(int i = 9; i < inv.getSize(); i++){
			items[(i-9)] = inv.getItem(i);
			if(inv.getItem(i) == null){
				items[(i-9)] = null;
			}
		}
		//FileHandler.setConfig(name.toLowerCase(), items);
		FileHandler.saveKit(name.substring(6,  7), items);
		try {
			FileHandler.loadKits();
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void openInv(Player p, String name, int level){
		//ItemStack[] kit = FileHandler.getKit(name.toLowerCase());
		int nextLevel = level + 1;
		ItemStack[] kit = FileHandler.getKit("kit" + level);
		inv = Bukkit.createInventory(p,  9*4, name);
		inv.setItem(1, new ItemStack(BetterKits.createItem(Material.ARROW, 1, "Back", null)));
		inv.setItem(7, new ItemStack(BetterKits.createItem(Material.EMERALD, 1, ChatColor.YELLOW + "Upgrade", ChatColor.GRAY + "Cost: " + ChatColor.GREEN + "$" + FileHandler.getKitCost(nextLevel))));

		for(int i = 0; i < inv.getSize(); i++){
			if(i >= 9){
				inv.setItem(i, kit[(i-9)]);
			}
		}
		
		p.openInventory(inv);
	}
	public static void editInv(Player p, String name){
		//ItemStack[] kit = FileHandler.getKit(name.toLowerCase());
		ItemStack[] kit = FileHandler.getKit("kit" + name.substring(6, 7));
		
		inv = Bukkit.createInventory(p, 9*4, name);
		inv.setItem(1, new ItemStack(BetterKits.createItem(Material.ARROW, 1, "Back", null)));
		inv.setItem(3, new ItemStack(BetterKits.createItem(Material.TNT, 1, "Clear", null)));
		inv.setItem(5, new ItemStack(BetterKits.createItem(Material.EMERALD, 1, "Save", null)));
		inv.setItem(7, new ItemStack(BetterKits.createItem(Material.BARRIER, 1, "Cancel", null)));
		
		for(int i = 0; i < inv.getSize(); i++){
			if(i >= 9){
				inv.setItem(i, kit[(i-9)]);
			}
		}
		p.openInventory(inv);
	}
	
	public static void setScoreboard(Player p, int currentKills, int currentDeaths, int currentStreak, double killRatio){
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard s = manager.getNewScoreboard();
		Objective o = s.registerNewObjective("test", "dummy");
		
		o.setDisplayName(ChatColor.GOLD + "Kit PVP");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		Score kills = o.getScore(ChatColor.GREEN + "Kills:");
		kills.setScore(10);
		
		String killsString = Integer.toString(currentKills);
		
		Score killsCount = o.getScore(ChatColor.GREEN + killsString);
		killsCount.setScore(9);
		
		Score deaths = o.getScore(ChatColor.GREEN + "Deaths:");
		deaths.setScore(8);
		
		String deathsString = Integer.toString(currentDeaths);
		
		Score deathsCount = o.getScore(ChatColor.RED + deathsString);
		deathsCount.setScore(7);
		
		Score streak = o.getScore(ChatColor.GREEN + "Streak:");
		streak.setScore(6);
		
		String streakString = Integer.toString(currentStreak);
		
		Score streakCount = o.getScore(ChatColor.AQUA + streakString);
		streakCount.setScore(5);
		
		Score killsDeaths = o.getScore(ChatColor.GREEN + "K/D Ratio:");
		killsDeaths.setScore(4);
		
		String stringRatio = Double.toString(killRatio);
		
		Score killsDeathsRatio = o.getScore(ChatColor.RED + stringRatio);
		killsDeathsRatio.setScore(3);
		
		p.setScoreboard(s);
		
	}
}

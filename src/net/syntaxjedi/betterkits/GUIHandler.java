package net.syntaxjedi.betterkits;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
	
	public static void showKits(Player p){
		inv = Bukkit.createInventory(p, 9, "Kits");
		inv.setItem(2, new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, "Fighter", "Fighters have equal strength and defense")));
		inv.setItem(4, new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, "Defender", "Defenders have lower strength but higher defense")));
		inv.setItem(6, new ItemStack(BetterKits.createItem(Material.IRON_CHESTPLATE, 1, "Berserker", "Berserkers have lower defense but higher strength")));
		p.openInventory(inv);
	}
	
	@EventHandler
	public static void inventoryClick(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		if(e.getCurrentItem() == null || e.getCurrentItem().equals(Material.AIR)){
			return;
		}else if(e.getInventory().getName().contains("Kits")){
			e.setCancelled(true);
			if(e.getClick().equals(ClickType.RIGHT) && p.hasPermission("kit.admin")){
				if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("fighter")){
					openInv(p, "Fighter");
				}else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Defender")){
					openInv(p, "Defender");
				}else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Berserker")){
					openInv(p, "Berserker");
				}
			}else if(e.getClick().equals(ClickType.LEFT)){
				if(BetterKits.getList(p.getName())){
					long time = FileHandler.getLong();
					long timeReadable = 0;
					String timeType = "";
					if(time < 1200){
						timeType = "seconds";
						timeReadable = time / 20;
					}else if(time >= 1200 && time < 72000){
						timeType = "minutes";
						timeReadable = (time / 20) / 60; 
					}else if(time >= 72000){
						timeType = "hours";
						timeReadable = ((time / 60) / 20) / 60; 
					}
					p.sendMessage(ChatColor.RED + "You have to wait " + timeReadable + " " + timeType + " to claim another kit.");
					return;
				}else if(BetterKits.getList(p.getName()) == false){
					if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("fighter")){
						((BetterKits)plugin).giveKit(p, "fighter");
						BetterKits.setTimeout(p, p.getName());
					}else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Defender")){
						((BetterKits)plugin).giveKit(p, "defender");
						BetterKits.setTimeout(p, p.getName());
					}else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Berserker")){
						((BetterKits)plugin).giveKit(p, "berserker");
						BetterKits.setTimeout(p, p.getName());
					}
				}
			}
		}else if(e.getInventory().getName().equalsIgnoreCase("fighter") || e.getInventory().getName().equalsIgnoreCase("defender") || e.getInventory().getName().equalsIgnoreCase("berserker")){
			if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("back")){
				e.setCancelled(true);
				showKits(p);
			}else if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("clear")){
				e.setCancelled(true);
				return;
			}else if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("save")){
				e.setCancelled(true);
				saveInv(p, e.getInventory(), e.getInventory().getName().toLowerCase());
			}else if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("cancel")){
				e.setCancelled(true);
				showKits(p);
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
		FileHandler.setConfig(name.toLowerCase(), items);
	}
	
	public static void openInv(Player p, String name){
		ItemStack[] kit = FileHandler.getKit(name.toLowerCase());
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
	
	public static void setScoreboard(Player p, Boolean init, int currentKills, int editKills, int editDeaths){
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard s = manager.getNewScoreboard();
		Objective o = s.registerNewObjective("test", "dummy");
		
		o.setDisplayName(ChatColor.GOLD + "Kit PVP");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		Score kills = o.getScore(ChatColor.GREEN + "Kills:");
		kills.setScore(10);
		
		Score killsCount = o.getScore(ChatColor.RED + "0");
		killsCount.setScore(9);
		
		Score deaths = o.getScore(ChatColor.GREEN + "Deaths:");
		deaths.setScore(8);
		
		Score deathsCount = o.getScore(ChatColor.GREEN + "0");
		deathsCount.setScore(7);
		
		Score killsDeaths = o.getScore(ChatColor.GREEN + "K/D Ratio:");
		killsDeaths.setScore(6);
		
		Score killsDeathsRatio = o.getScore(ChatColor.RED + "0.0");
		killsDeathsRatio.setScore(5);
		
		if(init){			
			p.setScoreboard(s);
		}else if(init == false){
			String killsString = Integer.toString(currentKills);
			
			killsCount = o.getScore(ChatColor.GREEN + killsString);
			killsCount.setScore(9);
			
			p.setScoreboard(s);
		}else{
			log.info("End of method");
		}
	}
}

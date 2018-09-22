package net.syntaxjedi.betterkits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor, Listener{
	private static final Logger log = Logger.getLogger("Minecraft");
	static Plugin plugin = BetterKits.getPlugin(BetterKits.class);
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandPreProcess(PlayerCommandPreprocessEvent e){
		if(e.getMessage().startsWith("/kit")){
			e.setCancelled(true);
			Player p = e.getPlayer();
			List<String> args = new ArrayList<String>(Arrays.asList(e.getMessage().split(" ")));
			if(args.size() == 1){
				GUIHandler.showKits(e.getPlayer(), PlayerHandler.getPoints(e.getPlayer()), PlayerHandler.getKit(p));
			}else if(args.size() == 2){
				switch(args.get(1)){
				case "help":
					p.sendMessage(ChatColor.BLUE + "+================+" + 
							ChatColor.GOLD + "\n/kit: " + ChatColor.WHITE + "Show your kit information and next upgrade." +
							ChatColor.GOLD + "\n/kit stats: " + ChatColor.WHITE + "Show current level, points, exp, and exp needed for next level.");
					break;
				case "test":
					p.sendMessage(ChatColor.GOLD + "This is the second command in the list!");
					break;
				case "stats":
					Map<String, Integer> playerMap = PlayerHandler.getInfo(p);
					if(playerMap == null){
						Map<String, Object> expMap = FileHandler.getExpConfig();
						p.sendMessage(ChatColor.BLUE + "+======" + ChatColor.DARK_GRAY + "Stats" + ChatColor.BLUE + "======+" +
								ChatColor.GOLD + "\nLevel: " + ChatColor.GREEN + "1" +
								ChatColor.GOLD + "\nCurrent EXP: " + ChatColor.GREEN + "0" +
								ChatColor.GOLD + "\nPoints: " + ChatColor.BLUE + "0" + 
								ChatColor.GOLD + "\nEXP Needed For Next Level: " + ChatColor.GREEN + expMap.get("base"));
					}
					int level = playerMap.get("level");
					int exp = playerMap.get("exp");
					int points = playerMap.get("points");
					int nextLevel = playerMap.get("nextLevel");
					p.sendMessage(ChatColor.BLUE + "+======" + ChatColor.DARK_GRAY + "Stats" + ChatColor.BLUE + "======+" +
							ChatColor.GOLD + "\nLevel: " + ChatColor.GREEN + level +
							ChatColor.GOLD + "\nCurrent EXP: " + ChatColor.GREEN + exp +
							ChatColor.GOLD + "\nPoints: " + ChatColor.BLUE + points + 
							ChatColor.GOLD + "\nEXP Needed For Next Level: " + ChatColor.GREEN + nextLevel);
					break;
				default:
					p.sendMessage(ChatColor.RED + "Invalid Argument: " + args.get(1));
				}
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "[Error] No Console Support");
			return true;
		}else if(sender instanceof Player){
			Player p = (Player) sender;
			
			if(command.getLabel().equalsIgnoreCase("adminkit")) {
				if(args.length == 0) {
					p.sendMessage(ChatColor.RED + "Use " + ChatColor.GOLD + "/adminkit help " + ChatColor.RED + "to see available commands");
					return true;
				}
				if(args.length == 1) {
					switch(args[0]) {
					case "help":
						p.sendMessage(ChatColor.BLUE + "+================+" + 
								ChatColor.GOLD + "\n/adminkit edit: " + ChatColor.WHITE + "Show edit kits GUI." +
								ChatColor.GOLD + "\n/adminkit points <edit|give> <player> <ammount>: " + ChatColor.WHITE + "Set or give player points." +
								ChatColor.GOLD + "\n/adminkit kit <player> <kit level>: " + ChatColor.WHITE + "Set a player's kit.");
						return true;
					case "edit":
						p.sendMessage(ChatColor.GOLD + "Loading kits...");
						GUIHandler.editKits(p);
						return true;
					case "reload":
						p.sendMessage(ChatColor.GOLD + "Reloading configs...");
						FileHandler.reloadConfig();
						return true;
					}
				}else if(args.length >= 2){
					switch(args[0]){
					case "kit":
						p.sendMessage(ChatColor.GOLD + "Setting kit of " + ChatColor.BLUE + args[1] + ChatColor.GOLD + " to " + ChatColor.GREEN + args[2]);
						PlayerHandler.setKit(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
						return true;
					case "points":
						if(args[1].equalsIgnoreCase("edit")){
							p.sendMessage(ChatColor.GOLD + "Setting points to " + ChatColor.GREEN + args[3] + ChatColor.GOLD + " for " + ChatColor.BLUE + args[2]);
							PlayerHandler.setPoints(Bukkit.getPlayer(args[2]), Integer.parseInt(args[3]));
							return true;
						}else if(args[1].equalsIgnoreCase("give")){
							p.sendMessage(ChatColor.GOLD + "Giving " + ChatColor.GREEN + args[3] + ChatColor.GOLD + " points to " + ChatColor.BLUE + args[2]);
							PlayerHandler.givePoints(Bukkit.getPlayer(args[2]), Integer.parseInt(args[3]));
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}

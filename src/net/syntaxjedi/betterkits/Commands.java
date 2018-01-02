package net.syntaxjedi.betterkits;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor{
	static Plugin plugin = BetterKits.getPlugin(BetterKits.class);
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "[Error] " + "No Console Support");
			return true;
		}else if(sender instanceof Player){
			Player p = (Player) sender;
			if(args.length == 0){
				GUIHandler.showKits(p);
				return true;
			}else if(args.length == 1){
				switch(args[0]){
				case "help":
					p.sendMessage(ChatColor.GOLD + "Helptext will go here.");
				}
			}
		}
		return false;
	}

}

package net.syntaxjedi.betterkits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class FileHandler {
	static Plugin plugin = BetterKits.getPlugin(BetterKits.class);
	
	public static ItemStack[] getKit(String kit){
		plugin.reloadConfig();
		ArrayList<?> stack = (ArrayList<?>) plugin.getConfig().getList("kits." + kit);
		ItemStack[] stacks = stack.toArray(new ItemStack[stack.size()]);
		return stacks;
	}
	
	public static void setConfig(String name, ItemStack[] stack){
		plugin.getConfig().set("kits." + name, stack);
		plugin.saveConfig();
	}
	
	public static Long getLong(){
		long length = plugin.getConfig().getLong("timeout.timeout");
		String interval = plugin.getConfig().getString("timeout.interval");
		if(interval.equalsIgnoreCase("seconds")){
			length = length * 20;
			return length;
		}else if(interval.equalsIgnoreCase("minutes")){
			length = (length*60)*20;
			return length;
		}else if(interval.equalsIgnoreCase("hours")){
			length = ((length*60)*60)*20;
			return length;
		}
		return length;
	}
	
	public static Map<String, Object> getExpConfig(){
		Map<String, Object> expRate = new HashMap<String, Object>();
		String type = plugin.getConfig().getString("experienceMod.type");
		double base = plugin.getConfig().getDouble("experienceMod.base");
		double mod = plugin.getConfig().getDouble("experienceMod.modifier");
		expRate.put("type", type);
		expRate.put("base", base);
		expRate.put("mod", mod);
		return expRate;
	}
}

package net.syntaxjedi.betterkits;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class FileHandler {
	private static final Logger log = Logger.getLogger("Minecraft");
	static Plugin plugin = BetterKits.getPlugin(BetterKits.class);
	private static File kitsPath = new File(plugin.getDataFolder().toString());
	private static File kitsFile = new File(kitsPath, "kits.yml");
	static FileConfiguration kits = YamlConfiguration.loadConfiguration(kitsFile);
	public static Map<String, ItemStack[]> kitsItems = new HashMap<String, ItemStack[]>();
	public static Map<String, Integer> kitCost = new HashMap<String, Integer>();
	
	public static void checkFiles(){
		
		if(!kitsPath.exists()){
			try{
				kitsPath.mkdirs();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(!kitsFile.exists()){
			try{
				plugin.saveResource("kits.yml", true);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void loadKits() throws FileNotFoundException, IOException, InvalidConfigurationException{
		kits.load(kitsFile);
		for(int i = 1; i < 10; i++){
			String kitName = "kit" + i;
			ArrayList<?> rawKit = (ArrayList<?>) kits.getList(kitName + ".items");
			ItemStack[] loadedKit = rawKit.toArray(new ItemStack[rawKit.size()]);
			kitsItems.put("kit" + i, loadedKit);
			kitCost.put(kitName, kits.getInt(kitName + ".cost"));
		}
	}
	
	public static void saveKit(String level, ItemStack[] stack){
		kits.set("kit" + level + ".items", stack);
		saveKitYML(kits, kitsFile);
	}
	
	
	
	public static ItemStack[] getKit(String kit){
		/*
		try {
			kits.load(kitsFile);
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info(kit);
		ArrayList<?> configKit = (ArrayList<?>) kits.getList(kit + ".items");
		ItemStack[] kitItems = configKit.toArray(new ItemStack[configKit.size()]);
		return kitItems;
		*/
		ItemStack[] items = kitsItems.get(kit);
		return items;
	}
	
	public static void saveKitYML(FileConfiguration ymlConfig, File ymlFile){
		try{
			ymlConfig.save(ymlFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static int getKitCost(int level){
		/*
		try {
			kits.load(kitsFile);
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int cost = kits.getInt("kit" + level + ".cost");
		return cost;
		*/
		String kitName = "kit" + level;
		int cost = kitCost.get(kitName);
		return cost;
	}
	
	/*
	public static ItemStack[] getKit(String kit){
		plugin.reloadConfig();
		ArrayList<?> stack = (ArrayList<?>) plugin.getConfig().getList("kits." + kit);
		ItemStack[] stacks = stack.toArray(new ItemStack[stack.size()]);
		return stacks;
	}
	*/
	
	public static void setConfig(String name, ItemStack[] stack){
		plugin.getConfig().set("kits." + name, stack);
		plugin.saveConfig();
	}
	
	public static void reloadConfig(){
		plugin.reloadConfig();
		plugin.saveConfig();
		try {
			loadKits();
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		Boolean pPerLevel = plugin.getConfig().getBoolean("points.perLevel");
		
		if(pPerLevel){
			expRate.put("perLevel", plugin.getConfig().getInt("points.atEach"));
		}
		
		expRate.put("type", plugin.getConfig().getString("experienceMod.type"));
		expRate.put("base", plugin.getConfig().getDouble("experienceMod.base"));
		expRate.put("mod", plugin.getConfig().getDouble("experienceMod.modifier"));
		expRate.put("perKill", plugin.getConfig().getInt("experienceMod.perKill"));
		expRate.put("pAmount", plugin.getConfig().getInt("points.amount"));
		expRate.put("pPerLevel", pPerLevel);
		return expRate;
	}
}

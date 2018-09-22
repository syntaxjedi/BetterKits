package net.syntaxjedi.betterkits;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SQLHandler {

private static final Logger log = Logger.getLogger("Minecraft");
	
	private static Connection connection;
	private static String host, database, username, password;
	private static int port;
	
	private static JavaPlugin plugin = JavaPlugin.getProvidingPlugin(BetterKits.class);
	
	public static void tryConnect() throws SQLException{
		
		host = plugin.getConfig().getString("database.host");
		port = plugin.getConfig().getInt("database.port");
		database = plugin.getConfig().getString("database.databaseName");
		username = plugin.getConfig().getString("database.username");
		password = plugin.getConfig().getString("database.password");
		
		if (host == null || port == 0 || database == null || username == null) {
			log.info("[Better Kits]: Problem loading database, make sure all info in the config is correct!");
		}
		
		BukkitRunnable r = new BukkitRunnable(){
			@Override
			public void run(){
				try{
					openConnection();
					buildTable();
				}catch(ClassNotFoundException e){
					e.printStackTrace();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		};
		r.runTaskAsynchronously(plugin);
	}
	
	public static void buildTable(){
		BukkitRunnable f = new BukkitRunnable(){
			public void run(){
				Statement statement = null;
				ResultSet players = null;
				ResultSet rel = null;
				DatabaseMetaData dbm = null;
				

				try {
					statement = connection.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				try {
					dbm = connection.getMetaData();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				try{
					players = dbm.getTables(null, null, "betterKits", null);
					if(!players.next()){
						log.info("[Better Kits] Creating Players Table In Database");
						statement.executeUpdate("CREATE TABLE `" + database + "`.`betterKits`(`UUID` VARCHAR(40) NOT NULL, "
								+ "`username` VARCHAR(20) NULL, "
								+ "`kills` VARCHAR(5) NULL, "
								+ "`deaths` VARCHAR(5) NULL, "
								+ "`experience` VARCHAR(5) NULL, "
								+ "`level` VARCHAR(5) NULL, "
								+ "`next_level` VARCHAR(5) NULL,"
								+ "`streak_1` VARCHAR(30) NULL, "
								+ "`streak_2` VARCHAR(30) NULL, "
								+ "`streak_3` VARCHAR(30) NULL, "
								+ "`kit_level` VARCHAR(30) NULL, "
								+ "`points` VARCHAR(30) NULL, "
								+ "PRIMARY KEY (`UUID`));");
					}else{
						log.info("[Better Kits] Players Table Already Exists");
					}
					players.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
				
			}
		};
		f.runTaskAsynchronously(plugin);
	}
	
	public static void saveStats(Player p, int kills, int deaths, int exp, double level, int nextLevel, String kit1, String kit2, String kit3, String kit_level, int points){
		BukkitRunnable s = new BukkitRunnable(){
			public void run(){
				Statement statement = null;
				ResultSet result = null;
				String uuid = p.getUniqueId().toString();
				String name = p.getName().toString();
				String query = "UPDATE `" + database + "`.`betterKits` SET username=?, kills=?, deaths=?, experience=?, level=?, next_level=?, streak_1=?, streak_2=?, streak_3=?, kit_level=?, points=? WHERE UUID=?";
				String firstQuery = "INSERT INTO betterKits (UUID) VALUES (?)";
				
				try{
					statement = connection.createStatement();
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				try{
					PreparedStatement pstmt = connection.prepareStatement(query);
					PreparedStatement firstStatement = connection.prepareStatement(firstQuery);
					
					pstmt.setString(1, name);
					pstmt.setInt(2, kills);
					pstmt.setInt(3, deaths);
					pstmt.setInt(4, exp);
					pstmt.setDouble(5, level);
					pstmt.setInt(6, nextLevel);
					pstmt.setString(7, kit1);
					pstmt.setString(8, kit2);
					pstmt.setString(9, kit3);
					pstmt.setString(10, kit_level);
					pstmt.setInt(11, points);
					pstmt.setString(12, uuid);
					
					firstStatement.setString(1, uuid);
					result = statement.executeQuery("SELECT * FROM betterKits WHERE UUID = \"" + uuid + "\";");
					
					if(result.next()){
						pstmt.execute();
					}else{
						firstStatement.execute();
						pstmt.execute();
					}
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		};
		s.runTaskAsynchronously(plugin);
	}
	
	public static void getStats(Player p){
		BukkitRunnable g = new BukkitRunnable(){
			public void run(){
				String uuid = p.getUniqueId().toString();
				String newPlayer = "INSERT INTO `" + database + "`.`betterKits` (UUID, username, kills, deaths, experience, level, next_level, streak_1, streak_2, streak_3, kit_level, points) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				try{
					Statement statement = connection.createStatement();
					ResultSet result = statement.executeQuery("SELECT * FROM betterKits WHERE UUID = \"" + uuid + "\";");
					if(!result.next()){
						PreparedStatement pstmt = connection.prepareStatement(newPlayer);
						pstmt.setString(1, p.getUniqueId().toString());
						pstmt.setString(2, p.getName());
						pstmt.setInt(3, 0);
						pstmt.setInt(4, 0);
						pstmt.setInt(5, 0);
						pstmt.setDouble(6, 1);
						pstmt.setInt(7, 0);
						pstmt.setString(8, "null");
						pstmt.setString(9, "null");
						pstmt.setString(10, "null");
						pstmt.setInt(11, 1);
						pstmt.setInt(12, 0);
						pstmt.execute();
						log.info("Creating User...");
						BukkitRunnable s = new BukkitRunnable(){
							public void run(){
								GUIHandler.setScoreboard(p, 0, 0, 0, 0);
								PlayerHandler.setStats(p, 0, 0, 1, 0, 0, 0, 1);
							}
						};
						s.runTaskLater(plugin, 0);
						
					}else{
						int kills = result.getInt("kills");
						int deaths = result.getInt("deaths");
						int exp = result.getInt("experience");
						int level = result.getInt("level");
						int nextLevel = result.getInt("next_level");
						int points = result.getInt("points");
						int kitLevel = result.getInt("kit_level");
						BukkitRunnable n = new BukkitRunnable(){
							public void run(){
								double kDRatio  = 0.0;
								if(deaths == 0) {
									kDRatio = kills + 0.0;
								}else if (kills >= 1) {
									kDRatio = Math.round(((double) kills / (double) deaths) * 100.0) / 100.0;
								}
								GUIHandler.setScoreboard(p, kills, deaths, 0, kDRatio);
								PlayerHandler.setStats(p, kills, deaths, level, nextLevel, exp, points, kitLevel);
							}
						};
						n.runTaskLater(plugin, 0);
					}
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		};
		g.runTaskAsynchronously(plugin);
	}

	public static void openConnection() throws SQLException, ClassNotFoundException{
		if(connection != null && !connection.isClosed()){
			return;
		}
		
		synchronized(plugin){
			if(connection != null && !connection.isClosed()){
				return;
			}
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false", username, password);
		}
	}
}

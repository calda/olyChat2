package com.olympuspvp.chat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class olyChat extends JavaPlugin{

	private final File configFile = new File("plugins" + File.separator + "olyChat" + File.separator + "config.yml");
	private static FileConfiguration config;
	private HashMap<String, HashMap<ChatItem, String>> chatData = new HashMap<String, HashMap<ChatItem, String>>();
	private final HashMap<ChatItem, String> defaults = new HashMap<ChatItem, String>();
	ChatListener chat = new ChatListener(this);
	LoginListener login = new LoginListener(this);
	CommandListener command = new CommandListener(this, chat);
	@Override
	public void onDisable(){

	}

	@Override
	public void onEnable(){
		config = getConfig();
		try{
			config.load(configFile);
		}catch(FileNotFoundException exe){
			System.out.println("[olyChat] The config file could not be found. Creating one.");
			config.set("Default.NameColor", "&7%name");
			config.set("Default.ChatColor", "&f");
			config.set("Default.SeparatorColor", "&8");
			config.set("Default.SeparatorType", "COLON");
			//an admin
			config.set("Users.MsAdminWoman.Name", "&d%name");
			config.set("Users.MsAdminWoman.ChatColor", "&b");
			config.set("Users.MsAdminWoman.SeparatorColor", "&5");
			config.set("Users.MsAdminWoman.SeparatorType", "CURLY_BRACKETS");
			//another admin
			config.set("Users.MrAdminMan.Tag", "[Importaint] ");
			config.set("Users.MrAdminMan.Name", "&cMr&aAdmin&bMan");
			config.set("Users.MrAdminMan.ChatColor", "&3");
			config.set("Users.MrAdminMan.SeparatorColor", "&4");
			config.set("Users.MrAdminMan.SeparatorType", "ANGLE_BRACKETS");
			//mod preset
			config.set("Presets.Mod.Tag", "[MOD] ");
			config.set("Presets.Mod.Name", "&c%name");
			config.set("Presets.Mod.SeparatorColor", "&a");
			config.set("Presets.Mod.SeparatorType", "COLON");
			config.set("Presets.Mod.ChatColor", "&b");
			//a mod
			config.set("Users.LittleModPerson.Preset", "Mod");
			saveConfig();
			System.out.println("[olyChat] Default Configuration set at location " + configFile.getPath());
		}catch(IOException exe){
			System.out.println("[olyChat] An unknown I/O error occured. Is the config file set to be unable to be read...? Stack Trace follows:");
			exe.printStackTrace();
		}catch(InvalidConfigurationException exe){
			System.out.println("[olyChat] The config file was formatted incorrectly. Stack Trace follows:");
			exe.printStackTrace();
		}getDefaults();
		Bukkit.getPluginManager().registerEvents(chat, this);
		Bukkit.getPluginManager().registerEvents(login, this);
		reloadData();
	}

	public boolean onCommand(CommandSender s, Command cc, String c, String[] args){
		if(cc.getLabel().equalsIgnoreCase("cset")) command.onCommandCSet(s, args);
		if(cc.getLabel().equalsIgnoreCase("colors")) command.onCommandColors(s);
		return true;
	}
	
	/**
	 * loads a player's data from config
	 * sets a player's information to Default if it cannot be found/read
	 * @param player The Player's name
	 */
	public void loadData(String player){
		String preset = config.getString("Users." + player + ".Preset");
		String name = null;
		String tag = null;
		String chatColor = null;
		String separatorColor = null;
		String separatorType = null;
		if(preset != null){
			name = config.getString("Presets." + preset + ".Name");
			tag = config.getString("Presets." + preset + ".Tag");
			chatColor = config.getString("Presets." + preset + ".ChatColor");
			separatorColor = config.getString("Presets." + preset + ".SeparatorColor");
			separatorType = config.getString("Presets." + preset + ".SeparatorType");
		}else{
			name = config.getString("Users." + player + ".Name");
			tag = config.getString("Users." + player + ".Tag");
			separatorColor = config.getString("Users." + player + ".SeparatorColor");
			separatorType = config.getString("Users." + player + ".SeparatorType");
			chatColor = config.getString("Users." + player + ".ChatColor");
		}
		if(tag == null) tag = defaults.get(ChatItem.Tag);
		if(name == null) name = defaults.get(ChatItem.Name);
		if(chatColor == null) chatColor = defaults.get(ChatItem.ChatColor);
		if(separatorType == null) separatorType = defaults.get(ChatItem.SeparatorType);
		if(separatorColor == null) separatorColor = defaults.get(ChatItem.SeparatorColor);	
		setPlayerSettings(player, tag, name, separatorType, separatorColor, chatColor);
	}
	
	public void reloadPlayer(String player){
		if(chatData.containsKey(player)) chatData.remove(player);
		loadData(player);
	}
	
	/**
	 * sets a player's data in the runtime. Does not affect the config.
	 * @param pname The name of the player (p.getName())
	 * @param tag The player's tag    EXAMPLE: [mod]
	 * @param name How the player's name will be formatted. Allows the name to be anything. (%name will be replaced by the player's name)
	 * @param separatorType Type of separator, which goes between name and message. EX: COLON, DASH, etc
	 * @param separatorColor Color of the separator, in &x format
	 * @param chatColor Color of the message, in &x format
	 */
	public void setPlayerSettings(final String pname, final String tag, final String name, final String separatorType, final String separatorColor, final String chatColor){
		HashMap<ChatItem, String> data = new HashMap<ChatItem, String>();
		data.put(ChatItem.Tag, tag);
		data.put(ChatItem.Name, name);
		data.put(ChatItem.SeparatorType, separatorType);
		data.put(ChatItem.SeparatorColor, separatorColor);
		data.put(ChatItem.ChatColor, chatColor);
		chatData.put(pname, data);
	}
	
	protected void setPlayerChatItem(final String pname, final ChatItem item, final String tag){
		setPlayerChatItem(pname, item, tag, true);
	}
	
	public void setPlayerChatItem(final String pname, final ChatItem item, final String tag, final boolean writeToConfig){
		if(!chatData.containsKey(pname)) loadData(pname);
		HashMap<ChatItem, String> data = chatData.get(pname);
		data.put(item, tag);
		chatData.put(pname, data);
		if(writeToConfig){
			config.set("Users." + pname + "." + item.toString(), tag);
			saveConfig();
		}
	}
	
	protected void clearPlayerChatItem(final String pname, final ChatItem item){
		config.set("Users." + pname + "." + item.toString(), null);
		saveConfig();
		reloadPlayer(pname);
	}
	
	protected void setPlayerPreset(final String pname, final String preset){
		config.set("Users." + pname + ".Preset", preset);
		saveConfig();
		reloadPlayer(pname);
	}
	
	protected void clearPlayerPreset(final String pname){
		config.set("Users." + pname + ".Preset", null);
		saveConfig();
		reloadPlayer(pname);
	}
	
	protected void setPresetChatItem(final String pname, final ChatItem item, final String tag){
		config.set("Presets." + pname + "." + item.toString(), tag);
		saveConfig();
		reloadData();
	}
	
	private void getDefaults(){
		String tag = config.getString("Default.Tag");
		if(tag == null) tag = "";
		String name = config.getString("Default.Name");
		if(name== null) name = "%name";
		String separatorType = config.getString("Default.SeparatorType");
		if(separatorType == null) name = "COLON";
		String separatorColor = config.getString("Default.SeparatorColor");
		if(separatorColor == null) name = "";
		String chatColor = config.getString("Default.ChatColor");
		if(chatColor== null) name = "";
		
		defaults.put(ChatItem.Tag, tag);
		defaults.put(ChatItem.Name, name);
		defaults.put(ChatItem.SeparatorType, separatorType);
		defaults.put(ChatItem.SeparatorColor, separatorColor);
		defaults.put(ChatItem.ChatColor, chatColor);
	}
	
	public String getTag(String playerName){
		String tag = chatData.get(playerName).get(ChatItem.Tag);
		if(tag == null) return playerName;
		return tag;
	}
	
	public String getName(String playerName){
		String name = chatData.get(playerName).get(ChatItem.Name);
		if(name == null) return playerName;
		return name;
	}
	
	public String getSeparatorType(String playerName){
		String type = chatData.get(playerName).get(ChatItem.SeparatorType);
		if(type == null) return "COLON";
		return type;
	}

	public String getSeparatorColor(String playerName){
		String color = chatData.get(playerName).get(ChatItem.SeparatorColor);
		if(color == null) return "";
		return color;
	}
	
	public String getChatColor(String playerName){
		String color = chatData.get(playerName).get(ChatItem.ChatColor);
		if(color == null) return "";
		return color;
	}
	
	public boolean hasPermission(Player p, String permission){
		if(p.isOp()) return true;
		if(p.hasPermission("OlympusPVP.*")) return true;
		if(p.hasPermission("olyChat.*")) return true;
		return p.hasPermission(permission);
	}
	
	public void reloadData(){
		for(Player p : Bukkit.getOnlinePlayers()){
			String name = p.getName();
			if(chatData.containsKey(name)) chatData.remove(name);
			loadData(name);
		}
	}
	
}
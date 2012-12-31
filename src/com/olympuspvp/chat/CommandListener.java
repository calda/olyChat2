package com.olympuspvp.chat;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//    /cset [name] [ChatItem] [new]
public class CommandListener{

	olyChat chat;
	ChatListener listener;

	protected CommandListener(final olyChat olychat, final ChatListener chatlistener){
		chat = olychat;
		listener = chatlistener;
	}

	protected void onCommandCSet(final CommandSender s, final String[] args){
		if(!(s instanceof Player) || chat.hasPermission((Player)s, "olyChat.admin")){
			if(args.length != 3){
				giveCsetUsage(s);
				return;
			}

			String name = args[0];
			boolean preset = false;
			if(name.startsWith("$")){
				preset = true;
				name = name.substring(1);
			}else{
				final List<Player> matches = Bukkit.matchPlayer(name);
				if(matches.size() == 1) name = matches.get(0).getName();
				else if(matches.size() == 0){
					s.sendMessage(ChatColor.DARK_RED + "No players match the name " + ChatColor.RED + name);
					return;
				}else{
					s.sendMessage(ChatColor.DARK_RED + "" + matches.size() + " Players match the name you gave. Try to be more specific.");
					return;
				}
			}

			final String chatItem = args[1];
			ChatItem item;
			final String entry = args[2];
			if(chatItem.equalsIgnoreCase("SepType")) item = ChatItem.SeparatorType;
			else if(chatItem.equalsIgnoreCase("SepColor")) item = ChatItem.SeparatorColor;
			else if(chatItem.equalsIgnoreCase("SeparatorType")) item = ChatItem.SeparatorType;
			else if(chatItem.equalsIgnoreCase("SeparatorColor")) item = ChatItem.SeparatorColor;
			else if(chatItem.equalsIgnoreCase("ChatColor")) item = ChatItem.ChatColor;
			else if(chatItem.equalsIgnoreCase("Tag")) item = ChatItem.Tag;
			else if(chatItem.equalsIgnoreCase("Name")) item = ChatItem.Name;
			else if(chatItem.equalsIgnoreCase("Preset") && !preset){
				if(entry.equals("#")) chat.clearPlayerPreset(name);
				else chat.setPlayerPreset(name, entry);
				s.sendMessage(listener.formatMessage(name, "This is how " + name + "'s chat messages look!"));
				return;
			}else{
				giveCsetUsage(s);
				return;
			}

			if(!preset){
				if(entry.equals("#")) chat.clearPlayerChatItem(name, item);
				else chat.setPlayerChatItem(name, item, entry);
				s.sendMessage(listener.formatMessage(name, "This is how " + name + "'s chat messages look!"));
			}else{
				if(entry.equals("#")) chat.setPresetChatItem(name, item, null);
				else chat.setPresetChatItem(name, item, entry);
				chat.setPlayerPreset("preset", name);
				s.sendMessage(listener.formatMessage("preset", "This is how " + name + "'s chat messages look!"));
			}

		}else s.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
	}

	private void giveCsetUsage(final CommandSender s){
		s.sendMessage(ChatColor.GRAY + "Incorrect Usage:");
		s.sendMessage(ChatColor.GRAY + "/cset [name] [ChatItem] [new]");
		s.sendMessage(ChatColor.GRAY + "ChatItems: " + ChatColor.DARK_GRAY + "Tag, Name, SeparatorType(SepType), SeparatorColor(SepColor), ChatColor");
		s.sendMessage(ChatColor.GRAY + "Separator Types: " + ChatColor.DARK_GRAY + "COLON, DASH, BRACKETS, ANGLE_BACKETS, CURLY_BRACKETS, ROUND_BRACKETS");
		s.sendMessage(ChatColor.GRAY + "To set a preset add $ to the name. EX: $mod, $admin, $donor");
		s.sendMessage(ChatColor.GRAY + "Set '#' as the entry to clear it.");
		s.sendMessage(ChatColor.GRAY + "Use the command /colors for a list of Minecraft-compatable color codes.");
	}
	protected void onCommandColors(final CommandSender s){
		s.sendMessage(ChatColor.GRAY + "===== Minecraft Color Codes =====");
		s.sendMessage(ChatColor.BLACK + "Black - &0    " + ChatColor.DARK_BLUE + "Dark Blue - &1");
		s.sendMessage(ChatColor.DARK_GREEN + "Dark Green - &2    " + ChatColor.DARK_AQUA + "Dark Aqua - &3");
		s.sendMessage(ChatColor.DARK_RED + "Dark Red - &4    " + ChatColor.DARK_PURPLE + "Purple - &5");
		s.sendMessage(ChatColor.GOLD + "Gold - &6    " + ChatColor.GRAY + "Gray - &7");
		s.sendMessage(ChatColor.DARK_GRAY + "Dark Gray - &8    " + ChatColor.BLUE + "Blue - &9");
		s.sendMessage(ChatColor.GREEN + "Green - &a    " + ChatColor.AQUA + "Aqua - &b");
		s.sendMessage(ChatColor.RED + "Red - &c    " + ChatColor.LIGHT_PURPLE + "Pink - &d");
		s.sendMessage(ChatColor.YELLOW + "Yellow - &e    " + ChatColor.WHITE + "White - &f\n");
		s.sendMessage(ChatColor.MAGIC + "Magic" + ChatColor.WHITE + " - &k    " + ChatColor.BOLD + "Bold - &l");
		s.sendMessage(ChatColor.UNDERLINE + "Underlined - &n" + ChatColor.RESET  + "    " + ChatColor.ITALIC + "Italic - &o");
		s.sendMessage(ChatColor.RESET + "Reset - &r");
	}

	protected void sendWhoCommand(final CommandSender s){
		final StringBuilder sb = new StringBuilder();
		final List<String> adminNames = new ArrayList<String>();
		final List<String> modNames = new ArrayList<String>();
		final List<String> donatorNames = new ArrayList<String>();
		final List<String> userNames = new ArrayList<String>();
		for(final Player p : Bukkit.getOnlinePlayers()){
			if(chat.hasPermission(p, "olyChatSuite.admin")){
				final String name = p.getName();
				adminNames.add(name);
			}else if(chat.hasPermission(p, "olyChatSuite.mod")){
				final String name = p.getName();
				modNames.add(name);
			}else if(chat.hasPermission(p, "olyChatSuite.donator")){
				final String name = p.getName();
				donatorNames.add(name);
			}else{
				final String name = p.getName();
				userNames.add(name);
			}
		}java.util.Collections.sort(adminNames);
		java.util.Collections.sort(modNames);
		java.util.Collections.sort(donatorNames);
		java.util.Collections.sort(userNames);

		for(final String str : adminNames){
			sb.append(chat.getName(str) + ChatColor.DARK_RED + ", ");
		}for(final String str : modNames){
			sb.append(chat.getName(str) + ChatColor.GREEN + ", ");
		}for(final String str : donatorNames){
			sb.append(chat.getName(str) + ChatColor.GOLD + ", ");
		}for(final String str : userNames){
			sb.append(chat.getName(str) + ChatColor.DARK_GRAY + ", ");
		}s.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.RED + "A:" + adminNames.size() + ChatColor.DARK_RED + "] " + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "M:" + modNames.size() + ChatColor.DARK_GREEN + "] " + ChatColor.GOLD + "[" + ChatColor.YELLOW + "D:" + donatorNames.size() + ChatColor.GOLD + "] " + ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "U:" + userNames.size() + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Total Online:" + ChatColor.WHITE + Bukkit.getOnlinePlayers().length);
		s.sendMessage(ChatColor.translateAlternateColorCodes('&', sb.toString()));
		s.sendMessage(ChatColor.DARK_GRAY + "=====================================================");
	}


}
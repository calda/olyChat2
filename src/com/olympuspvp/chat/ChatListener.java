package com.olympuspvp.chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

@SuppressWarnings("deprecation")
public class ChatListener implements Listener{

	olyChat chat;

	protected ChatListener(final olyChat olychat){
		this.chat = olychat;
	}

	@EventHandler
	public void onPlayerChat(final PlayerChatEvent e){
		e.setFormat(formatMessage(e.getPlayer(), e.getMessage()));
	}
	
	protected String formatMessage(Player p, String message){
		if(chat.hasPermission(p, "olyChat.coloredChat")){
			//fixed bug in-which server crashes if a message trails in the chat code character
			while(message.endsWith("&")) message = message.substring(0, message.length()-1);
			message = ChatColor.translateAlternateColorCodes('&', message);
		}return formatMessage(p.getName(), message);
	}
	
	protected String formatMessage(String playerName, String message){
		String name = chat.getName(playerName);
		String tag = chat.getTag(playerName);
		String sepColor = chat.getSeparatorColor(playerName);
		final String sepType = chat.getSeparatorType(playerName);
		String chatColor = chat.getChatColor(playerName);

		name = ChatColor.translateAlternateColorCodes('&', name);
		tag = ChatColor.translateAlternateColorCodes('&', tag);
		sepColor = ChatColor.translateAlternateColorCodes('&', sepColor);
		chatColor = ChatColor.translateAlternateColorCodes('&', chatColor);
		name = name.replaceAll("%name", playerName);
		message = chatColor + message;
		
		final String cName;
		if(sepType.equalsIgnoreCase("SINGLE_ANGLE_BRACKET")) cName = tag + name + sepColor + "> ";
		else if(sepType.equalsIgnoreCase("DASH")) cName = tag + name + sepColor + "- ";
		else if(sepType.equalsIgnoreCase("ANGLE_BRACKETS")) cName = sepColor + "<" + tag + name + sepColor + "> ";
		else if(sepType.equalsIgnoreCase("BRACKETS")) cName = sepColor + "[" + tag + name + sepColor + "] ";
		else if(sepType.equalsIgnoreCase("CURLY_BRACKETS")) cName = sepColor + "{" + tag + name + sepColor + "} ";
		else if(sepType.equalsIgnoreCase("ROUND_BRACKETS")) cName = sepColor + "(" + tag + name + sepColor + ") ";
		else if(sepType.equalsIgnoreCase("ROUND_BRACKETS")) cName = sepColor + "(" + tag + name + sepColor + ") ";
		else cName = tag + name + sepColor + ": "; //colon is the program default if it can't find a match otherwise
		
		final String complete = cName + message;
		return complete;
	}
	
}
package com.olympuspvp.chat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class LoginListener implements Listener{

	final olyChat chat;
	
	protected LoginListener(olyChat olychat){
		chat = olychat;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerLoginEvent e){
		String name = e.getPlayer().getName();
		chat.loadData(name);
		System.out.println("[olyChat] Loaded olyChat configuration for user " + name);
	}
	
}

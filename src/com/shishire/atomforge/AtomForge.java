package com.shishire.atomforge;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class AtomForge extends JavaPlugin {
	Logger log;
	
	public void onEnable()
	{
		log = this.getLogger();
		getServer().getPluginManager().registerEvents(new AtomForgeListener(), this);
	}
}

package com.rpgskills.listeners;

import com.rpgskills.RPGSkillsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player join/quit events for data loading/saving.
 */
public class PlayerListener implements Listener {

    private final RPGSkillsPlugin plugin;

    public PlayerListener(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getPlayerDataManager().loadPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager().unloadPlayerData(event.getPlayer().getUniqueId());
        plugin.getCooldownManager().clearCooldowns(event.getPlayer().getUniqueId());
    }
}

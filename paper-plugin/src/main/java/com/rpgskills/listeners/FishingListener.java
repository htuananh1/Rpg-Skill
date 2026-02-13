package com.rpgskills.listeners;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.SkillCategory;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * Handles fishing events for XP gain.
 */
public class FishingListener implements Listener {

    private final RPGSkillsPlugin plugin;

    public FishingListener(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        Player player = event.getPlayer();

        if (!(event.getCaught() instanceof Item caughtItem)) return;

        // Determine XP based on catch type
        String xpKey;
        String itemType = caughtItem.getItemStack().getType().name().toLowerCase();

        if (itemType.contains("fish") || itemType.contains("salmon") ||
                itemType.contains("cod") || itemType.contains("tropical") ||
                itemType.contains("pufferfish")) {
            xpKey = "catch-fish";
        } else if (itemType.contains("enchanted") || itemType.contains("saddle") ||
                itemType.contains("name_tag") || itemType.contains("nautilus") ||
                itemType.contains("bow") || itemType.contains("fishing_rod")) {
            xpKey = "catch-treasure";
        } else {
            xpKey = "catch-junk";
        }

        int xp = plugin.getConfigManager().getXpRate(xpKey);
        if (xp > 0) {
            plugin.getSkillManager().addXp(player, xp, SkillCategory.SURVIVAL);
        }
    }
}

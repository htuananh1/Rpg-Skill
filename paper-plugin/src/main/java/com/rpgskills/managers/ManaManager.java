package com.rpgskills.managers;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Manages mana regeneration for all online players.
 */
public class ManaManager {

    private final RPGSkillsPlugin plugin;
    private BukkitTask regenTask;

    public ManaManager(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Start the mana regeneration task.
     */
    public void startRegenTask() {
        int interval = plugin.getConfigManager().getManaRegenIntervalTicks();
        double regenAmount = plugin.getConfigManager().getManaRegenPerSecond();

        regenTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
                if (data == null) continue;

                if (data.getCurrentMana() < data.getMaxMana()) {
                    data.regenerateMana(regenAmount);
                }
            }
        }, interval, interval);
    }

    /**
     * Stop the mana regeneration task.
     */
    public void stopRegenTask() {
        if (regenTask != null) {
            regenTask.cancel();
            regenTask = null;
        }
    }

    /**
     * Get a formatted mana bar string for display.
     */
    public String getManaBar(PlayerData data) {
        double current = data.getCurrentMana();
        double max = data.getMaxMana();
        int barLength = 20;
        int filled = (int) ((current / max) * barLength);

        StringBuilder bar = new StringBuilder("§b✦ Mana: §f");
        bar.append(String.format("%.0f/%.0f ", current, max));
        bar.append("§b[");
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                bar.append("§b|");
            } else {
                bar.append("§7|");
            }
        }
        bar.append("§b]");

        return bar.toString();
    }
}

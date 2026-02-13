package com.rpgskills.managers;

import com.rpgskills.RPGSkillsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages skill cooldowns for players.
 */
public class CooldownManager {

    private final RPGSkillsPlugin plugin;
    // Map of player UUID -> (skill ID -> expiry timestamp in millis)
    private final Map<UUID, Map<String, Long>> cooldowns;

    public CooldownManager(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
    }

    /**
     * Set a cooldown for a player's skill.
     *
     * @param uuid     Player UUID
     * @param skillId  Skill identifier
     * @param seconds  Cooldown duration in seconds
     */
    public void setCooldown(UUID uuid, String skillId, int seconds) {
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(skillId, System.currentTimeMillis() + (seconds * 1000L));
    }

    /**
     * Check if a skill is on cooldown for a player.
     */
    public boolean isOnCooldown(UUID uuid, String skillId) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) return false;

        Long expiry = playerCooldowns.get(skillId);
        if (expiry == null) return false;

        if (System.currentTimeMillis() >= expiry) {
            playerCooldowns.remove(skillId);
            return false;
        }

        return true;
    }

    /**
     * Get remaining cooldown time in seconds.
     */
    public double getRemainingCooldown(UUID uuid, String skillId) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) return 0;

        Long expiry = playerCooldowns.get(skillId);
        if (expiry == null) return 0;

        long remaining = expiry - System.currentTimeMillis();
        return remaining > 0 ? remaining / 1000.0 : 0;
    }

    /**
     * Clear all cooldowns for a player.
     */
    public void clearCooldowns(UUID uuid) {
        cooldowns.remove(uuid);
    }
}

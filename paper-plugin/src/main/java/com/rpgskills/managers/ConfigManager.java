package com.rpgskills.managers;

import com.rpgskills.RPGSkillsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Manages plugin configuration values.
 */
public class ConfigManager {

    private final RPGSkillsPlugin plugin;

    public ConfigManager(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    // General settings
    public int getMaxLevel() {
        return getConfig().getInt("general.max-level", 100);
    }

    public int getBaseXpPerLevel() {
        return getConfig().getInt("general.base-xp-per-level", 100);
    }

    public double getXpScalingFactor() {
        return getConfig().getDouble("general.xp-scaling-factor", 1.15);
    }

    public int getSkillPointsPerLevel() {
        return getConfig().getInt("general.skill-points-per-level", 1);
    }

    public int getStartingSkillPoints() {
        return getConfig().getInt("general.starting-skill-points", 0);
    }

    /**
     * Calculate XP required for a given level.
     */
    public int getXpForLevel(int level) {
        return (int) (getBaseXpPerLevel() * Math.pow(getXpScalingFactor(), level - 1));
    }

    // Mana settings
    public int getBaseMaxMana() {
        return getConfig().getInt("mana.base-max-mana", 100);
    }

    public int getManaPerLevel() {
        return getConfig().getInt("mana.mana-per-level", 5);
    }

    public double getManaRegenPerSecond() {
        return getConfig().getDouble("mana.regen-per-second", 2.0);
    }

    public int getManaRegenIntervalTicks() {
        return getConfig().getInt("mana.regen-interval-ticks", 20);
    }

    /**
     * Calculate max mana for a given player level.
     */
    public double getMaxManaForLevel(int level) {
        return getBaseMaxMana() + (getManaPerLevel() * (level - 1));
    }

    // Skill settings
    public String getSkillDisplayName(String skillId) {
        return getConfig().getString("skills." + skillId + ".display-name", skillId);
    }

    public String getSkillDescription(String skillId) {
        return getConfig().getString("skills." + skillId + ".description", "No description");
    }

    public int getSkillMaxLevel(String skillId) {
        return getConfig().getInt("skills." + skillId + ".max-level", 10);
    }

    public String getSkillIcon(String skillId) {
        return getConfig().getString("skills." + skillId + ".icon", "STONE");
    }

    public int getSkillPointCost(String skillId) {
        return getConfig().getInt("skills." + skillId + ".skill-point-cost", 1);
    }

    public int getSkillManaCost(String skillId) {
        return getConfig().getInt("skills." + skillId + ".mana-cost", 0);
    }

    public int getSkillCooldown(String skillId) {
        return getConfig().getInt("skills." + skillId + ".cooldown", 0);
    }

    public double getSkillDouble(String skillId, String key, double defaultValue) {
        return getConfig().getDouble("skills." + skillId + "." + key, defaultValue);
    }

    public int getSkillInt(String skillId, String key, int defaultValue) {
        return getConfig().getInt("skills." + skillId + "." + key, defaultValue);
    }

    // XP rates
    public int getXpRate(String action) {
        return getConfig().getInt("xp-rates." + action, 0);
    }

    // Messages
    public String getMessage(String key) {
        String prefix = getConfig().getString("messages.prefix", "&6[RPGSkills] &r");
        String message = getConfig().getString("messages." + key, "Missing message: " + key);
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    public String getRawMessage(String key) {
        String message = getConfig().getString("messages." + key, "Missing message: " + key);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}

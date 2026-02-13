package com.rpgskills.models;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Base interface for all skills in the RPG system.
 */
public interface Skill {

    /**
     * @return The unique identifier for this skill (e.g., "sword-mastery")
     */
    String getId();

    /**
     * @return The display name shown to players
     */
    String getDisplayName();

    /**
     * @return The skill category (COMBAT, MAGIC, SURVIVAL)
     */
    SkillCategory getCategory();

    /**
     * @return Description of what this skill does
     */
    String getDescription();

    /**
     * @return Maximum level this skill can reach
     */
    int getMaxLevel();

    /**
     * @return The Material used as icon in the GUI
     */
    Material getIcon();

    /**
     * @return Skill points required to level up this skill
     */
    int getSkillPointCost();

    /**
     * @return Whether this skill can be actively used (magic skills)
     */
    boolean isActivatable();

    /**
     * @return Mana cost to activate this skill (0 for passive skills)
     */
    int getManaCost();

    /**
     * @return Cooldown in seconds (0 for passive skills)
     */
    int getCooldown();

    /**
     * Get a description of the skill's effect at a given level.
     *
     * @param level The skill level
     * @return Formatted description of the effect
     */
    String getEffectDescription(int level);

    /**
     * Activate this skill for a player (only for activatable skills).
     *
     * @param player The player activating the skill
     * @param level  The player's current level in this skill
     */
    void activate(Player player, int level);
}

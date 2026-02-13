package com.rpgskills.models;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Abstract base class for skills providing common functionality.
 */
public abstract class AbstractSkill implements Skill {

    protected final String id;
    protected final String displayName;
    protected final SkillCategory category;
    protected final String description;
    protected final int maxLevel;
    protected final Material icon;
    protected final int skillPointCost;
    protected final int manaCost;
    protected final int cooldown;

    protected AbstractSkill(String id, String displayName, SkillCategory category,
                            String description, int maxLevel, Material icon,
                            int skillPointCost, int manaCost, int cooldown) {
        this.id = id;
        this.displayName = displayName;
        this.category = category;
        this.description = description;
        this.maxLevel = maxLevel;
        this.icon = icon;
        this.skillPointCost = skillPointCost;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public SkillCategory getCategory() {
        return category;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public Material getIcon() {
        return icon;
    }

    @Override
    public int getSkillPointCost() {
        return skillPointCost;
    }

    @Override
    public int getManaCost() {
        return manaCost;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public boolean isActivatable() {
        return false;
    }

    @Override
    public void activate(Player player, int level) {
        // Default: do nothing (passive skills)
    }
}

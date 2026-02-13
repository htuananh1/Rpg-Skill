package com.rpgskills.models;

import org.bukkit.ChatColor;

public enum SkillCategory {
    COMBAT("Combat", ChatColor.RED, "⚔"),
    MAGIC("Magic", ChatColor.LIGHT_PURPLE, "✦"),
    SURVIVAL("Survival", ChatColor.GREEN, "♦");

    private final String displayName;
    private final ChatColor color;
    private final String icon;

    SkillCategory(String displayName, ChatColor color, String icon) {
        this.displayName = displayName;
        this.color = color;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }

    public String getFormattedName() {
        return color + icon + " " + displayName;
    }
}

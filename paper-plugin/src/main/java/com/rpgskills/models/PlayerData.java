package com.rpgskills.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Holds all RPG data for a single player.
 */
public class PlayerData {

    private final UUID uuid;
    private String playerName;
    private int level;
    private int experience;
    private int skillPoints;
    private double currentMana;
    private double maxMana;
    private final Map<String, Integer> skillLevels;
    private final Map<String, Integer> categoryXp;

    public PlayerData(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.level = 1;
        this.experience = 0;
        this.skillPoints = 0;
        this.currentMana = 100;
        this.maxMana = 100;
        this.skillLevels = new HashMap<>();
        this.categoryXp = new HashMap<>();

        // Initialize category XP
        for (SkillCategory cat : SkillCategory.values()) {
            categoryXp.put(cat.name(), 0);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    public double getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(double currentMana) {
        this.currentMana = Math.max(0, Math.min(currentMana, maxMana));
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
    }

    public int getSkillLevel(String skillId) {
        return skillLevels.getOrDefault(skillId, 0);
    }

    public void setSkillLevel(String skillId, int level) {
        skillLevels.put(skillId, level);
    }

    public Map<String, Integer> getSkillLevels() {
        return new HashMap<>(skillLevels);
    }

    public int getCategoryXp(SkillCategory category) {
        return categoryXp.getOrDefault(category.name(), 0);
    }

    public void setCategoryXp(SkillCategory category, int xp) {
        categoryXp.put(category.name(), xp);
    }

    public void addCategoryXp(SkillCategory category, int xp) {
        int current = getCategoryXp(category);
        setCategoryXp(category, current + xp);
    }

    public Map<String, Integer> getCategoryXpMap() {
        return new HashMap<>(categoryXp);
    }

    /**
     * Add experience and check for level up.
     *
     * @param xp Amount of XP to add
     * @param xpForNextLevel XP required for next level
     * @param maxLevel Maximum level
     * @param pointsPerLevel Skill points per level
     * @return true if the player leveled up
     */
    public boolean addExperience(int xp, int xpForNextLevel, int maxLevel, int pointsPerLevel) {
        if (level >= maxLevel) {
            return false;
        }

        experience += xp;
        boolean leveledUp = false;

        while (experience >= xpForNextLevel && level < maxLevel) {
            experience -= xpForNextLevel;
            level++;
            skillPoints += pointsPerLevel;
            leveledUp = true;
            // Recalculate for next level
            xpForNextLevel = calculateXpForLevel(level);
        }

        return leveledUp;
    }

    private int calculateXpForLevel(int level) {
        // This is a placeholder; actual calculation is done in ConfigManager
        return (int) (100 * Math.pow(1.15, level - 1));
    }

    public boolean consumeMana(double amount) {
        if (currentMana >= amount) {
            currentMana -= amount;
            return true;
        }
        return false;
    }

    public void regenerateMana(double amount) {
        currentMana = Math.min(maxMana, currentMana + amount);
    }
}

package com.rpgskills.managers;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.PlayerData;
import com.rpgskills.models.Skill;
import com.rpgskills.models.SkillCategory;
import com.rpgskills.skills.combat.ArcherySkill;
import com.rpgskills.skills.combat.CriticalStrikeSkill;
import com.rpgskills.skills.combat.SwordMasterySkill;
import com.rpgskills.skills.magic.FireballSkill;
import com.rpgskills.skills.magic.HealSkill;
import com.rpgskills.skills.magic.LightningSkill;
import com.rpgskills.skills.survival.FarmingSkill;
import com.rpgskills.skills.survival.FishingSkill;
import com.rpgskills.skills.survival.MiningSkill;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages all skills and skill-related operations.
 */
public class SkillManager {

    private final RPGSkillsPlugin plugin;
    private final Map<String, Skill> skills;

    public SkillManager(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
        this.skills = new LinkedHashMap<>();
        registerSkills();
    }

    private void registerSkills() {
        // Combat skills
        registerSkill(new SwordMasterySkill(plugin));
        registerSkill(new ArcherySkill(plugin));
        registerSkill(new CriticalStrikeSkill(plugin));

        // Magic skills
        registerSkill(new FireballSkill(plugin));
        registerSkill(new HealSkill(plugin));
        registerSkill(new LightningSkill(plugin));

        // Survival skills
        registerSkill(new MiningSkill(plugin));
        registerSkill(new FarmingSkill(plugin));
        registerSkill(new FishingSkill(plugin));
    }

    private void registerSkill(Skill skill) {
        skills.put(skill.getId(), skill);
    }

    public Skill getSkill(String id) {
        return skills.get(id);
    }

    public Collection<Skill> getAllSkills() {
        return skills.values();
    }

    public List<Skill> getSkillsByCategory(SkillCategory category) {
        return skills.values().stream()
                .filter(s -> s.getCategory() == category)
                .collect(Collectors.toList());
    }

    public Set<String> getSkillIds() {
        return skills.keySet();
    }

    /**
     * Attempt to level up a skill for a player.
     *
     * @return true if successful
     */
    public boolean levelUpSkill(Player player, String skillId) {
        Skill skill = getSkill(skillId);
        if (skill == null) return false;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return false;

        int currentLevel = data.getSkillLevel(skillId);
        if (currentLevel >= skill.getMaxLevel()) return false;

        int cost = skill.getSkillPointCost();
        if (data.getSkillPoints() < cost) return false;

        data.setSkillPoints(data.getSkillPoints() - cost);
        data.setSkillLevel(skillId, currentLevel + 1);

        plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
        return true;
    }

    /**
     * Attempt to activate a skill for a player.
     *
     * @return true if successful
     */
    public boolean activateSkill(Player player, String skillId) {
        Skill skill = getSkill(skillId);
        if (skill == null || !skill.isActivatable()) return false;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return false;

        int level = data.getSkillLevel(skillId);
        if (level <= 0) {
            player.sendMessage(plugin.getConfigManager().getMessage("skill-not-found")
                    .replace("{skill}", skill.getDisplayName()));
            return false;
        }

        // Check cooldown
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), skillId)) {
            double remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), skillId);
            player.sendMessage(plugin.getConfigManager().getMessage("skill-on-cooldown")
                    .replace("{remaining}", String.format("%.1f", remaining)));
            return false;
        }

        // Check mana
        int manaCost = skill.getManaCost();
        if (manaCost > 0 && data.getCurrentMana() < manaCost) {
            player.sendMessage(plugin.getConfigManager().getMessage("not-enough-mana")
                    .replace("{cost}", String.valueOf(manaCost))
                    .replace("{current}", String.format("%.0f", data.getCurrentMana())));
            return false;
        }

        // Consume mana
        if (manaCost > 0) {
            data.consumeMana(manaCost);
        }

        // Set cooldown
        if (skill.getCooldown() > 0) {
            plugin.getCooldownManager().setCooldown(player.getUniqueId(), skillId, skill.getCooldown());
        }

        // Activate the skill
        skill.activate(player, level);

        player.sendMessage(plugin.getConfigManager().getMessage("skill-activated")
                .replace("{skill}", skill.getDisplayName()));

        return true;
    }

    /**
     * Add XP to a player and handle level ups.
     */
    public void addXp(Player player, int xp, SkillCategory category) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        // Add category-specific XP
        data.addCategoryXp(category, xp);

        // Add to overall XP and check for level up
        ConfigManager config = plugin.getConfigManager();
        int xpForNextLevel = config.getXpForLevel(data.getLevel());
        boolean leveledUp = data.addExperience(xp, xpForNextLevel, config.getMaxLevel(), config.getSkillPointsPerLevel());

        if (leveledUp) {
            // Update max mana
            double newMaxMana = config.getMaxManaForLevel(data.getLevel());
            data.setMaxMana(newMaxMana);

            player.sendMessage(config.getMessage("level-up")
                    .replace("{level}", String.valueOf(data.getLevel()))
                    .replace("{points}", String.valueOf(config.getSkillPointsPerLevel())));

            // Play level up effects
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        // Show XP in action bar
        net.kyori.adventure.text.Component actionBar = net.kyori.adventure.text.Component.text(
                category.getColor() + "+" + xp + " " + category.getDisplayName() + " XP"
        );
        player.sendActionBar(actionBar);
    }
}

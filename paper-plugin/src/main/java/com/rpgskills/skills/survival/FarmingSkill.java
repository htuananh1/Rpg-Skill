package com.rpgskills.skills.survival;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.AbstractSkill;
import com.rpgskills.models.SkillCategory;
import org.bukkit.Material;

import java.util.Random;

/**
 * Passive survival skill that gives a chance for extra crop drops.
 */
public class FarmingSkill extends AbstractSkill {

    private final double extraDropChancePerLevel;
    private final Random random;

    public FarmingSkill(RPGSkillsPlugin plugin) {
        super(
                "farming",
                plugin.getConfigManager().getSkillDisplayName("farming"),
                SkillCategory.SURVIVAL,
                plugin.getConfigManager().getSkillDescription("farming"),
                plugin.getConfigManager().getSkillMaxLevel("farming"),
                Material.valueOf(plugin.getConfigManager().getSkillIcon("farming")),
                plugin.getConfigManager().getSkillPointCost("farming"),
                0, 0
        );
        this.extraDropChancePerLevel = plugin.getConfigManager().getSkillDouble("farming", "extra-drop-chance-per-level", 5.0);
        this.random = new Random();
    }

    @Override
    public String getEffectDescription(int level) {
        return (int) (extraDropChancePerLevel * level) + "% chance for extra crop drops";
    }

    public boolean rollExtraDrop(int level) {
        double chance = extraDropChancePerLevel * level / 100.0;
        return random.nextDouble() < chance;
    }
}

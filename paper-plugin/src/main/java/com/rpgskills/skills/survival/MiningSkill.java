package com.rpgskills.skills.survival;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.AbstractSkill;
import com.rpgskills.models.SkillCategory;
import org.bukkit.Material;

import java.util.Random;

/**
 * Passive survival skill that gives a chance for double drops when mining ores.
 */
public class MiningSkill extends AbstractSkill {

    private final double doubleDropChancePerLevel;
    private final Random random;

    public MiningSkill(RPGSkillsPlugin plugin) {
        super(
                "mining",
                plugin.getConfigManager().getSkillDisplayName("mining"),
                SkillCategory.SURVIVAL,
                plugin.getConfigManager().getSkillDescription("mining"),
                plugin.getConfigManager().getSkillMaxLevel("mining"),
                Material.valueOf(plugin.getConfigManager().getSkillIcon("mining")),
                plugin.getConfigManager().getSkillPointCost("mining"),
                0, 0
        );
        this.doubleDropChancePerLevel = plugin.getConfigManager().getSkillDouble("mining", "double-drop-chance-per-level", 5.0);
        this.random = new Random();
    }

    @Override
    public String getEffectDescription(int level) {
        return (int) (doubleDropChancePerLevel * level) + "% chance for double ore drops";
    }

    /**
     * Roll for double drops.
     *
     * @param level The player's mining skill level
     * @return true if drops should be doubled
     */
    public boolean rollDoubleDrop(int level) {
        double chance = doubleDropChancePerLevel * level / 100.0;
        return random.nextDouble() < chance;
    }
}

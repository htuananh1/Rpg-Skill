package com.rpgskills.skills.combat;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.AbstractSkill;
import com.rpgskills.models.SkillCategory;
import org.bukkit.Material;

import java.util.Random;

/**
 * Passive skill that gives a chance to deal critical (double) damage.
 */
public class CriticalStrikeSkill extends AbstractSkill {

    private final double critChancePerLevel;
    private final double critDamageMultiplier;
    private final Random random;

    public CriticalStrikeSkill(RPGSkillsPlugin plugin) {
        super(
                "critical-strike",
                plugin.getConfigManager().getSkillDisplayName("critical-strike"),
                SkillCategory.COMBAT,
                plugin.getConfigManager().getSkillDescription("critical-strike"),
                plugin.getConfigManager().getSkillMaxLevel("critical-strike"),
                Material.valueOf(plugin.getConfigManager().getSkillIcon("critical-strike")),
                plugin.getConfigManager().getSkillPointCost("critical-strike"),
                0, 0
        );
        this.critChancePerLevel = plugin.getConfigManager().getSkillDouble("critical-strike", "crit-chance-per-level", 5.0);
        this.critDamageMultiplier = plugin.getConfigManager().getSkillDouble("critical-strike", "crit-damage-multiplier", 2.0);
        this.random = new Random();
    }

    @Override
    public String getEffectDescription(int level) {
        return (int) (critChancePerLevel * level) + "% chance for " + critDamageMultiplier + "x damage";
    }

    /**
     * Roll for a critical strike.
     *
     * @param level The player's skill level
     * @return true if the hit is critical
     */
    public boolean rollCritical(int level) {
        double chance = critChancePerLevel * level / 100.0;
        return random.nextDouble() < chance;
    }

    public double getCritDamageMultiplier() {
        return critDamageMultiplier;
    }
}

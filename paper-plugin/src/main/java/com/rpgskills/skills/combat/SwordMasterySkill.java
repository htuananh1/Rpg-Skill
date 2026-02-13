package com.rpgskills.skills.combat;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.AbstractSkill;
import com.rpgskills.models.SkillCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Passive skill that increases melee damage with swords.
 */
public class SwordMasterySkill extends AbstractSkill {

    private final double damageBonusPerLevel;

    public SwordMasterySkill(RPGSkillsPlugin plugin) {
        super(
                "sword-mastery",
                plugin.getConfigManager().getSkillDisplayName("sword-mastery"),
                SkillCategory.COMBAT,
                plugin.getConfigManager().getSkillDescription("sword-mastery"),
                plugin.getConfigManager().getSkillMaxLevel("sword-mastery"),
                Material.valueOf(plugin.getConfigManager().getSkillIcon("sword-mastery")),
                plugin.getConfigManager().getSkillPointCost("sword-mastery"),
                0, 0
        );
        this.damageBonusPerLevel = plugin.getConfigManager().getSkillDouble("sword-mastery", "damage-bonus-per-level", 10.0);
    }

    @Override
    public String getEffectDescription(int level) {
        return "+" + (int) (damageBonusPerLevel * level) + "% sword damage";
    }

    /**
     * Get the damage multiplier for a given skill level.
     */
    public double getDamageMultiplier(int level) {
        return 1.0 + (damageBonusPerLevel * level / 100.0);
    }
}

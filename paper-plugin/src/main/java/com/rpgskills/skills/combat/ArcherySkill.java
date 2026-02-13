package com.rpgskills.skills.combat;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.AbstractSkill;
import com.rpgskills.models.SkillCategory;
import org.bukkit.Material;

/**
 * Passive skill that increases arrow damage.
 */
public class ArcherySkill extends AbstractSkill {

    private final double damageBonusPerLevel;

    public ArcherySkill(RPGSkillsPlugin plugin) {
        super(
                "archery",
                plugin.getConfigManager().getSkillDisplayName("archery"),
                SkillCategory.COMBAT,
                plugin.getConfigManager().getSkillDescription("archery"),
                plugin.getConfigManager().getSkillMaxLevel("archery"),
                Material.valueOf(plugin.getConfigManager().getSkillIcon("archery")),
                plugin.getConfigManager().getSkillPointCost("archery"),
                0, 0
        );
        this.damageBonusPerLevel = plugin.getConfigManager().getSkillDouble("archery", "damage-bonus-per-level", 8.0);
    }

    @Override
    public String getEffectDescription(int level) {
        return "+" + (int) (damageBonusPerLevel * level) + "% arrow damage";
    }

    public double getDamageMultiplier(int level) {
        return 1.0 + (damageBonusPerLevel * level / 100.0);
    }
}

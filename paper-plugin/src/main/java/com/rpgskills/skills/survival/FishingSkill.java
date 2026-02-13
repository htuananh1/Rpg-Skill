package com.rpgskills.skills.survival;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.AbstractSkill;
import com.rpgskills.models.SkillCategory;
import org.bukkit.Material;

/**
 * Passive survival skill that improves fishing speed and treasure chance.
 */
public class FishingSkill extends AbstractSkill {

    private final double speedBonusPerLevel;
    private final double treasureChancePerLevel;

    public FishingSkill(RPGSkillsPlugin plugin) {
        super(
                "fishing",
                plugin.getConfigManager().getSkillDisplayName("fishing"),
                SkillCategory.SURVIVAL,
                plugin.getConfigManager().getSkillDescription("fishing"),
                plugin.getConfigManager().getSkillMaxLevel("fishing"),
                Material.valueOf(plugin.getConfigManager().getSkillIcon("fishing")),
                plugin.getConfigManager().getSkillPointCost("fishing"),
                0, 0
        );
        this.speedBonusPerLevel = plugin.getConfigManager().getSkillDouble("fishing", "speed-bonus-per-level", 5.0);
        this.treasureChancePerLevel = plugin.getConfigManager().getSkillDouble("fishing", "treasure-chance-per-level", 3.0);
    }

    @Override
    public String getEffectDescription(int level) {
        return (int) (speedBonusPerLevel * level) + "% faster catches, +" +
                (int) (treasureChancePerLevel * level) + "% treasure chance";
    }

    public double getSpeedBonus(int level) {
        return speedBonusPerLevel * level;
    }

    public double getTreasureChanceBonus(int level) {
        return treasureChancePerLevel * level;
    }
}

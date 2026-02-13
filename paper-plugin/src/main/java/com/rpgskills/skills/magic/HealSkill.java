package com.rpgskills.skills.magic;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.AbstractSkill;
import com.rpgskills.models.SkillCategory;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Active magic skill that heals the player.
 */
public class HealSkill extends AbstractSkill {

    private final double baseHeal;
    private final double healPerLevel;

    public HealSkill(RPGSkillsPlugin plugin) {
        super(
                "heal",
                plugin.getConfigManager().getSkillDisplayName("heal"),
                SkillCategory.MAGIC,
                plugin.getConfigManager().getSkillDescription("heal"),
                plugin.getConfigManager().getSkillMaxLevel("heal"),
                Material.valueOf(plugin.getConfigManager().getSkillIcon("heal")),
                plugin.getConfigManager().getSkillPointCost("heal"),
                plugin.getConfigManager().getSkillManaCost("heal"),
                plugin.getConfigManager().getSkillCooldown("heal")
        );
        this.baseHeal = plugin.getConfigManager().getSkillDouble("heal", "base-heal", 4.0);
        this.healPerLevel = plugin.getConfigManager().getSkillDouble("heal", "heal-per-level", 1.0);
    }

    @Override
    public boolean isActivatable() {
        return true;
    }

    @Override
    public String getEffectDescription(int level) {
        double heal = baseHeal + (healPerLevel * level);
        return String.format("Restore %.1f health", heal);
    }

    @Override
    public void activate(Player player, int level) {
        double healAmount = baseHeal + (healPerLevel * level);
        double maxHealth = player.getMaxHealth();
        double newHealth = Math.min(player.getHealth() + healAmount, maxHealth);

        player.setHealth(newHealth);

        // Visual effects
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);
    }
}

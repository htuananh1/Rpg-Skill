package com.rpgskills.skills.magic;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.AbstractSkill;
import com.rpgskills.models.SkillCategory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Active magic skill that strikes lightning at the target location.
 */
public class LightningSkill extends AbstractSkill {

    private final RPGSkillsPlugin plugin;
    private final double baseDamage;
    private final double damagePerLevel;
    private final int maxRange;

    public LightningSkill(RPGSkillsPlugin plugin) {
        super(
                "lightning",
                plugin.getConfigManager().getSkillDisplayName("lightning"),
                SkillCategory.MAGIC,
                plugin.getConfigManager().getSkillDescription("lightning"),
                plugin.getConfigManager().getSkillMaxLevel("lightning"),
                Material.valueOf(plugin.getConfigManager().getSkillIcon("lightning")),
                plugin.getConfigManager().getSkillPointCost("lightning"),
                plugin.getConfigManager().getSkillManaCost("lightning"),
                plugin.getConfigManager().getSkillCooldown("lightning")
        );
        this.plugin = plugin;
        this.baseDamage = plugin.getConfigManager().getSkillDouble("lightning", "base-damage", 6.0);
        this.damagePerLevel = plugin.getConfigManager().getSkillDouble("lightning", "damage-per-level", 2.0);
        this.maxRange = plugin.getConfigManager().getSkillInt("lightning", "max-range", 30);
    }

    @Override
    public boolean isActivatable() {
        return true;
    }

    @Override
    public String getEffectDescription(int level) {
        double damage = baseDamage + (damagePerLevel * level);
        return String.format("%.1f damage lightning strike (range: %d blocks)", damage, maxRange);
    }

    @Override
    public void activate(Player player, int level) {
        double damage = baseDamage + (damagePerLevel * level);

        // Get target block the player is looking at
        Block targetBlock = player.getTargetBlockExact(maxRange);
        Location targetLoc;

        if (targetBlock != null) {
            targetLoc = targetBlock.getLocation();
        } else {
            // Strike at max range in the direction the player is looking
            targetLoc = player.getLocation().add(player.getLocation().getDirection().multiply(maxRange));
        }

        // Strike lightning (visual only, we handle damage ourselves)
        player.getWorld().strikeLightningEffect(targetLoc);

        // Damage nearby entities
        for (Entity entity : targetLoc.getWorld().getNearbyEntities(targetLoc, 2, 2, 2)) {
            if (entity instanceof LivingEntity livingEntity && entity != player) {
                livingEntity.damage(damage, player);
            }
        }

        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
    }
}

package com.rpgskills.skills.magic;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.AbstractSkill;
import com.rpgskills.models.SkillCategory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Active magic skill that launches a fireball projectile.
 */
public class FireballSkill extends AbstractSkill {

    private final RPGSkillsPlugin plugin;
    private final double baseDamage;
    private final double damagePerLevel;

    public FireballSkill(RPGSkillsPlugin plugin) {
        super(
                "fireball",
                plugin.getConfigManager().getSkillDisplayName("fireball"),
                SkillCategory.MAGIC,
                plugin.getConfigManager().getSkillDescription("fireball"),
                plugin.getConfigManager().getSkillMaxLevel("fireball"),
                Material.valueOf(plugin.getConfigManager().getSkillIcon("fireball")),
                plugin.getConfigManager().getSkillPointCost("fireball"),
                plugin.getConfigManager().getSkillManaCost("fireball"),
                plugin.getConfigManager().getSkillCooldown("fireball")
        );
        this.plugin = plugin;
        this.baseDamage = plugin.getConfigManager().getSkillDouble("fireball", "base-damage", 4.0);
        this.damagePerLevel = plugin.getConfigManager().getSkillDouble("fireball", "damage-per-level", 1.5);
    }

    @Override
    public boolean isActivatable() {
        return true;
    }

    @Override
    public String getEffectDescription(int level) {
        double damage = baseDamage + (damagePerLevel * level);
        return String.format("%.1f damage fireball", damage);
    }

    @Override
    public void activate(Player player, int level) {
        double damage = baseDamage + (damagePerLevel * level);

        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setDirection(player.getLocation().getDirection().multiply(1.5));
        fireball.setYield(0); // No block damage
        fireball.setIsIncendiary(false);
        fireball.setMetadata("rpgskills_damage", new FixedMetadataValue(plugin, damage));
        fireball.setMetadata("rpgskills_caster", new FixedMetadataValue(plugin, player.getUniqueId().toString()));

        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);
    }

    public double getDamage(int level) {
        return baseDamage + (damagePerLevel * level);
    }
}

package com.rpgskills.listeners;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.PlayerData;
import com.rpgskills.models.Skill;
import com.rpgskills.models.SkillCategory;
import com.rpgskills.skills.combat.ArcherySkill;
import com.rpgskills.skills.combat.CriticalStrikeSkill;
import com.rpgskills.skills.combat.SwordMasterySkill;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles combat-related events for XP gain and skill effects.
 */
public class CombatListener implements Listener {

    private final RPGSkillsPlugin plugin;

    public CombatListener(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player player = null;
        boolean isArrow = false;

        // Direct melee hit
        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        }
        // Arrow hit
        else if (event.getDamager() instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Player) {
                player = (Player) arrow.getShooter();
                isArrow = true;
            }
        }

        if (player == null) return;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        double damage = event.getDamage();

        if (isArrow) {
            // Apply Archery bonus
            Skill archerySkill = plugin.getSkillManager().getSkill("archery");
            int archeryLevel = data.getSkillLevel("archery");
            if (archeryLevel > 0 && archerySkill instanceof ArcherySkill archery) {
                damage *= archery.getDamageMultiplier(archeryLevel);
            }

            // Give archery XP
            int xp = plugin.getConfigManager().getXpRate("arrow-hit");
            if (xp > 0) {
                plugin.getSkillManager().addXp(player, xp, SkillCategory.COMBAT);
            }
        } else {
            // Check if holding a sword
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (isSword(mainHand.getType())) {
                // Apply Sword Mastery bonus
                Skill swordSkill = plugin.getSkillManager().getSkill("sword-mastery");
                int swordLevel = data.getSkillLevel("sword-mastery");
                if (swordLevel > 0 && swordSkill instanceof SwordMasterySkill swordMastery) {
                    damage *= swordMastery.getDamageMultiplier(swordLevel);
                }
            }

            // Give melee XP
            int xp = plugin.getConfigManager().getXpRate("melee-hit");
            if (xp > 0) {
                plugin.getSkillManager().addXp(player, xp, SkillCategory.COMBAT);
            }
        }

        // Apply Critical Strike
        Skill critSkill = plugin.getSkillManager().getSkill("critical-strike");
        int critLevel = data.getSkillLevel("critical-strike");
        if (critLevel > 0 && critSkill instanceof CriticalStrikeSkill criticalStrike) {
            if (criticalStrike.rollCritical(critLevel)) {
                damage *= criticalStrike.getCritDamageMultiplier();
                player.sendMessage(ChatColor.RED + "§l✦ CRITICAL HIT! §r§c" +
                        String.format("%.1f", damage) + " damage!");
            }
        }

        event.setDamage(damage);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        Player killer = event.getEntity().getKiller();
        Entity victim = event.getEntity();

        int xp;
        if (victim instanceof Player) {
            xp = plugin.getConfigManager().getXpRate("kill-player");
        } else if (victim instanceof Monster) {
            xp = plugin.getConfigManager().getXpRate("kill-hostile-mob");
        } else {
            return;
        }

        if (xp > 0) {
            plugin.getSkillManager().addXp(killer, xp, SkillCategory.COMBAT);
        }
    }

    private boolean isSword(Material material) {
        return material == Material.WOODEN_SWORD ||
                material == Material.STONE_SWORD ||
                material == Material.IRON_SWORD ||
                material == Material.GOLDEN_SWORD ||
                material == Material.DIAMOND_SWORD ||
                material == Material.NETHERITE_SWORD;
    }
}

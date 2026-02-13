package com.rpgskills.listeners;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.PlayerData;
import com.rpgskills.models.Skill;
import com.rpgskills.models.SkillCategory;
import com.rpgskills.skills.survival.FarmingSkill;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles farming events for XP gain and extra drop effects.
 */
public class FarmingListener implements Listener {

    private final RPGSkillsPlugin plugin;
    private final Map<Material, String> cropXpMap;

    public FarmingListener(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
        this.cropXpMap = new HashMap<>();

        cropXpMap.put(Material.WHEAT, "harvest-wheat");
        cropXpMap.put(Material.CARROTS, "harvest-carrot");
        cropXpMap.put(Material.POTATOES, "harvest-potato");
        cropXpMap.put(Material.BEETROOTS, "harvest-beetroot");
        cropXpMap.put(Material.NETHER_WART, "harvest-nether-wart");
        cropXpMap.put(Material.MELON, "harvest-melon");
        cropXpMap.put(Material.PUMPKIN, "harvest-pumpkin");
        cropXpMap.put(Material.SUGAR_CANE, "harvest-sugar-cane");
        cropXpMap.put(Material.COCOA, "harvest-cocoa");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material type = block.getType();

        String xpKey = cropXpMap.get(type);
        if (xpKey == null) return;

        // Check if crop is fully grown (for ageable crops)
        if (block.getBlockData() instanceof Ageable ageable) {
            if (ageable.getAge() < ageable.getMaximumAge()) {
                return; // Not fully grown
            }
        }

        int xp = plugin.getConfigManager().getXpRate(xpKey);
        if (xp <= 0) return;

        // Give XP
        plugin.getSkillManager().addXp(player, xp, SkillCategory.SURVIVAL);

        // Check for extra drops (Farming skill)
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        int farmingLevel = data.getSkillLevel("farming");
        if (farmingLevel > 0) {
            Skill skill = plugin.getSkillManager().getSkill("farming");
            if (skill instanceof FarmingSkill farmingSkill && farmingSkill.rollExtraDrop(farmingLevel)) {
                Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
                for (ItemStack drop : drops) {
                    block.getWorld().dropItemNaturally(block.getLocation(), drop.clone());
                }
                player.sendMessage("§a§l✦ Extra Harvest! §r§aFarming skill activated!");
            }
        }
    }
}

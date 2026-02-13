package com.rpgskills.listeners;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.PlayerData;
import com.rpgskills.models.Skill;
import com.rpgskills.models.SkillCategory;
import com.rpgskills.skills.survival.MiningSkill;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
 * Handles mining events for XP gain and double drop effects.
 */
public class MiningListener implements Listener {

    private final RPGSkillsPlugin plugin;
    private final Map<Material, String> oreXpMap;

    public MiningListener(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
        this.oreXpMap = new HashMap<>();

        // Map ore blocks to their XP config keys
        oreXpMap.put(Material.COAL_ORE, "mine-coal");
        oreXpMap.put(Material.DEEPSLATE_COAL_ORE, "mine-coal");
        oreXpMap.put(Material.IRON_ORE, "mine-iron");
        oreXpMap.put(Material.DEEPSLATE_IRON_ORE, "mine-iron");
        oreXpMap.put(Material.GOLD_ORE, "mine-gold");
        oreXpMap.put(Material.DEEPSLATE_GOLD_ORE, "mine-gold");
        oreXpMap.put(Material.DIAMOND_ORE, "mine-diamond");
        oreXpMap.put(Material.DEEPSLATE_DIAMOND_ORE, "mine-diamond");
        oreXpMap.put(Material.EMERALD_ORE, "mine-emerald");
        oreXpMap.put(Material.DEEPSLATE_EMERALD_ORE, "mine-emerald");
        oreXpMap.put(Material.LAPIS_ORE, "mine-lapis");
        oreXpMap.put(Material.DEEPSLATE_LAPIS_ORE, "mine-lapis");
        oreXpMap.put(Material.REDSTONE_ORE, "mine-redstone");
        oreXpMap.put(Material.DEEPSLATE_REDSTONE_ORE, "mine-redstone");
        oreXpMap.put(Material.COPPER_ORE, "mine-copper");
        oreXpMap.put(Material.DEEPSLATE_COPPER_ORE, "mine-copper");
        oreXpMap.put(Material.NETHER_QUARTZ_ORE, "mine-nether-quartz");
        oreXpMap.put(Material.ANCIENT_DEBRIS, "mine-ancient-debris");
        oreXpMap.put(Material.STONE, "mine-stone");
        oreXpMap.put(Material.DEEPSLATE, "mine-stone");
        oreXpMap.put(Material.COBBLESTONE, "mine-stone");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material type = block.getType();

        // Check if this block gives mining XP
        String xpKey = oreXpMap.get(type);
        if (xpKey == null) return;

        int xp = plugin.getConfigManager().getXpRate(xpKey);
        if (xp <= 0) return;

        // Give XP
        plugin.getSkillManager().addXp(player, xp, SkillCategory.SURVIVAL);

        // Check for double drops (Mining skill)
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        int miningLevel = data.getSkillLevel("mining");
        if (miningLevel > 0) {
            Skill skill = plugin.getSkillManager().getSkill("mining");
            if (skill instanceof MiningSkill miningSkill && miningSkill.rollDoubleDrop(miningLevel)) {
                // Double the drops
                Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
                for (ItemStack drop : drops) {
                    block.getWorld().dropItemNaturally(block.getLocation(), drop.clone());
                }
                player.sendMessage("§a§l✦ Double Drop! §r§aMining skill activated!");
            }
        }
    }
}

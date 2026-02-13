package com.rpgskills.managers;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.PlayerData;
import com.rpgskills.models.Skill;
import com.rpgskills.models.SkillCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the skill GUI for players.
 */
public class GUIManager {

    public static final String MAIN_MENU_TITLE = "§6§l✦ RPG Skills ✦";
    public static final String COMBAT_MENU_TITLE = "§c§l⚔ Combat Skills";
    public static final String MAGIC_MENU_TITLE = "§d§l✦ Magic Skills";
    public static final String SURVIVAL_MENU_TITLE = "§a§l♦ Survival Skills";

    private final RPGSkillsPlugin plugin;

    public GUIManager(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Open the main skills menu for a player.
     */
    public void openMainMenu(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        Inventory gui = Bukkit.createInventory(null, 54, MAIN_MENU_TITLE);

        // Fill border with glass panes
        fillBorder(gui, Material.BLACK_STAINED_GLASS_PANE);

        // Player info item (center top)
        gui.setItem(4, createPlayerInfoItem(data));

        // Category items
        gui.setItem(20, createCategoryItem(SkillCategory.COMBAT, Material.DIAMOND_SWORD, data));
        gui.setItem(22, createCategoryItem(SkillCategory.MAGIC, Material.ENCHANTED_BOOK, data));
        gui.setItem(24, createCategoryItem(SkillCategory.SURVIVAL, Material.DIAMOND_PICKAXE, data));

        // Mana display
        gui.setItem(49, createManaItem(data));

        player.openInventory(gui);
    }

    /**
     * Open a category-specific skill menu.
     */
    public void openCategoryMenu(Player player, SkillCategory category) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        String title;
        switch (category) {
            case COMBAT -> title = COMBAT_MENU_TITLE;
            case MAGIC -> title = MAGIC_MENU_TITLE;
            case SURVIVAL -> title = SURVIVAL_MENU_TITLE;
            default -> title = MAIN_MENU_TITLE;
        }

        Inventory gui = Bukkit.createInventory(null, 54, title);

        // Fill border
        Material paneColor = switch (category) {
            case COMBAT -> Material.RED_STAINED_GLASS_PANE;
            case MAGIC -> Material.PURPLE_STAINED_GLASS_PANE;
            case SURVIVAL -> Material.GREEN_STAINED_GLASS_PANE;
        };
        fillBorder(gui, paneColor);

        // Add skill items
        List<Skill> categorySkills = plugin.getSkillManager().getSkillsByCategory(category);
        int[] slots = {20, 22, 24}; // Center row slots

        for (int i = 0; i < categorySkills.size() && i < slots.length; i++) {
            Skill skill = categorySkills.get(i);
            gui.setItem(slots[i], createSkillItem(skill, data));
        }

        // Back button
        gui.setItem(49, createBackButton());

        // Player info
        gui.setItem(4, createPlayerInfoItem(data));

        player.openInventory(gui);
    }

    private ItemStack createPlayerInfoItem(PlayerData data) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§l" + data.getPlayerName());

        List<String> lore = new ArrayList<>();
        lore.add("§7");
        lore.add("§eLevel: §f" + data.getLevel());
        lore.add("§eXP: §f" + data.getExperience() + "§7/§f" +
                plugin.getConfigManager().getXpForLevel(data.getLevel()));
        lore.add("§eSkill Points: §a" + data.getSkillPoints());
        lore.add("§7");
        lore.add("§bMana: §f" + String.format("%.0f", data.getCurrentMana()) +
                "§7/§f" + String.format("%.0f", data.getMaxMana()));
        lore.add("§7");

        // Category XP
        for (SkillCategory cat : SkillCategory.values()) {
            lore.add(cat.getColor() + cat.getIcon() + " " + cat.getDisplayName() +
                    " XP: §f" + data.getCategoryXp(cat));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCategoryItem(SkillCategory category, Material material, PlayerData data) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(category.getFormattedName() + " §lSkills");

        List<String> lore = new ArrayList<>();
        lore.add("§7");
        lore.add("§7Category XP: §f" + data.getCategoryXp(category));
        lore.add("§7");

        List<Skill> skills = plugin.getSkillManager().getSkillsByCategory(category);
        for (Skill skill : skills) {
            int level = data.getSkillLevel(skill.getId());
            String status = level > 0 ? "§a✔ " : "§c✘ ";
            lore.add(status + "§f" + skill.getDisplayName() + " §7[Lv." + level + "/" + skill.getMaxLevel() + "]");
        }

        lore.add("§7");
        lore.add("§eClick to view skills!");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSkillItem(Skill skill, PlayerData data) {
        int level = data.getSkillLevel(skill.getId());
        ItemStack item = new ItemStack(skill.getIcon());
        ItemMeta meta = item.getItemMeta();

        String levelColor = level > 0 ? "§a" : "§c";
        meta.setDisplayName(skill.getCategory().getColor() + "§l" + skill.getDisplayName() +
                " " + levelColor + "[Lv." + level + "/" + skill.getMaxLevel() + "]");

        List<String> lore = new ArrayList<>();
        lore.add("§7" + skill.getDescription());
        lore.add("§7");

        if (level > 0) {
            lore.add("§eCurrent Effect:");
            lore.add("§f  " + skill.getEffectDescription(level));
        }

        if (level < skill.getMaxLevel()) {
            lore.add("§7");
            lore.add("§eNext Level Effect:");
            lore.add("§f  " + skill.getEffectDescription(level + 1));
        }

        lore.add("§7");

        if (skill.isActivatable()) {
            lore.add("§bMana Cost: §f" + skill.getManaCost());
            lore.add("§bCooldown: §f" + skill.getCooldown() + "s");
            lore.add("§7");
        }

        if (level < skill.getMaxLevel()) {
            lore.add("§eCost: §f" + skill.getSkillPointCost() + " Skill Point(s)");
            boolean canAfford = data.getSkillPoints() >= skill.getSkillPointCost();
            if (canAfford) {
                lore.add("§a▶ Click to level up!");
            } else {
                lore.add("§c✘ Not enough skill points");
            }
        } else {
            lore.add("§a§l✔ MAX LEVEL");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createManaItem(PlayerData data) {
        ItemStack item = new ItemStack(Material.LAPIS_LAZULI);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b§l✦ Mana");

        List<String> lore = new ArrayList<>();
        lore.add("§7");
        lore.add("§bCurrent: §f" + String.format("%.0f", data.getCurrentMana()) +
                "§7/§f" + String.format("%.0f", data.getMaxMana()));
        lore.add("§bRegen: §f" + plugin.getConfigManager().getManaRegenPerSecond() + "/s");
        lore.add("§7");

        // Mana bar
        double ratio = data.getCurrentMana() / data.getMaxMana();
        int barLen = 20;
        int filled = (int) (ratio * barLen);
        StringBuilder bar = new StringBuilder("§b");
        for (int i = 0; i < barLen; i++) {
            bar.append(i < filled ? "§b█" : "§7█");
        }
        lore.add(bar.toString());

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBackButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§l← Back to Main Menu");
        List<String> lore = new ArrayList<>();
        lore.add("§7Click to go back");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void fillBorder(Inventory gui, Material pane) {
        ItemStack filler = new ItemStack(pane);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);

        int size = gui.getSize();
        for (int i = 0; i < 9; i++) gui.setItem(i, filler);
        for (int i = size - 9; i < size; i++) gui.setItem(i, filler);
        for (int i = 9; i < size - 9; i += 9) gui.setItem(i, filler);
        for (int i = 17; i < size - 9; i += 9) gui.setItem(i, filler);
    }
}

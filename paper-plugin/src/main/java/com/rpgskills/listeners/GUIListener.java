package com.rpgskills.listeners;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.managers.GUIManager;
import com.rpgskills.models.PlayerData;
import com.rpgskills.models.Skill;
import com.rpgskills.models.SkillCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles GUI click events for the skill menus.
 */
public class GUIListener implements Listener {

    private final RPGSkillsPlugin plugin;

    public GUIListener(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();

        // Check if this is one of our GUIs
        if (!title.equals(GUIManager.MAIN_MENU_TITLE) &&
                !title.equals(GUIManager.COMBAT_MENU_TITLE) &&
                !title.equals(GUIManager.MAGIC_MENU_TITLE) &&
                !title.equals(GUIManager.SURVIVAL_MENU_TITLE)) {
            return;
        }

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // Ignore glass pane clicks
        if (clicked.getType().name().contains("STAINED_GLASS_PANE")) return;

        if (title.equals(GUIManager.MAIN_MENU_TITLE)) {
            handleMainMenuClick(player, event.getSlot(), clicked);
        } else if (title.equals(GUIManager.COMBAT_MENU_TITLE)) {
            handleCategoryMenuClick(player, event.getSlot(), clicked, SkillCategory.COMBAT);
        } else if (title.equals(GUIManager.MAGIC_MENU_TITLE)) {
            handleCategoryMenuClick(player, event.getSlot(), clicked, SkillCategory.MAGIC);
        } else if (title.equals(GUIManager.SURVIVAL_MENU_TITLE)) {
            handleCategoryMenuClick(player, event.getSlot(), clicked, SkillCategory.SURVIVAL);
        }
    }

    private void handleMainMenuClick(Player player, int slot, ItemStack clicked) {
        switch (slot) {
            case 20 -> plugin.getGUIManager().openCategoryMenu(player, SkillCategory.COMBAT);
            case 22 -> plugin.getGUIManager().openCategoryMenu(player, SkillCategory.MAGIC);
            case 24 -> plugin.getGUIManager().openCategoryMenu(player, SkillCategory.SURVIVAL);
        }
    }

    private void handleCategoryMenuClick(Player player, int slot, ItemStack clicked, SkillCategory category) {
        // Back button
        if (clicked.getType() == Material.ARROW) {
            plugin.getGUIManager().openMainMenu(player);
            return;
        }

        // Skill item slots
        int[] skillSlots = {20, 22, 24};
        var skills = plugin.getSkillManager().getSkillsByCategory(category);

        for (int i = 0; i < skillSlots.length && i < skills.size(); i++) {
            if (slot == skillSlots[i]) {
                Skill skill = skills.get(i);
                attemptLevelUp(player, skill);
                // Refresh the GUI
                plugin.getGUIManager().openCategoryMenu(player, category);
                return;
            }
        }
    }

    private void attemptLevelUp(Player player, Skill skill) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        int currentLevel = data.getSkillLevel(skill.getId());

        if (currentLevel >= skill.getMaxLevel()) {
            player.sendMessage(plugin.getConfigManager().getMessage("skill-max-level"));
            return;
        }

        if (data.getSkillPoints() < skill.getSkillPointCost()) {
            player.sendMessage(plugin.getConfigManager().getMessage("not-enough-points"));
            return;
        }

        if (plugin.getSkillManager().levelUpSkill(player, skill.getId())) {
            int newLevel = data.getSkillLevel(skill.getId());
            if (currentLevel == 0) {
                player.sendMessage(plugin.getConfigManager().getMessage("skill-unlocked")
                        .replace("{skill}", skill.getDisplayName()));
            } else {
                player.sendMessage(plugin.getConfigManager().getMessage("skill-leveled")
                        .replace("{skill}", skill.getDisplayName())
                        .replace("{level}", String.valueOf(newLevel)));
            }
        }
    }
}

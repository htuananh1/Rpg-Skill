package com.rpgskills.commands;

import com.rpgskills.RPGSkillsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /skills command to open the skill GUI.
 */
public class SkillsCommand implements CommandExecutor {

    private final RPGSkillsPlugin plugin;

    public SkillsCommand(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        plugin.getGUIManager().openMainMenu(player);
        return true;
    }
}

package com.rpgskills.commands;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.PlayerData;
import com.rpgskills.models.Skill;
import com.rpgskills.models.SkillCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the /skill command with subcommands: info, activate, admin.
 */
public class SkillCommand implements CommandExecutor, TabCompleter {

    private final RPGSkillsPlugin plugin;

    public SkillCommand(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "info" -> handleInfo(sender, args);
            case "activate" -> handleActivate(sender, args);
            case "admin" -> handleAdmin(sender, args);
            default -> sendUsage(sender);
        }

        return true;
    }

    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /skill info <skill>");
            return;
        }

        String skillId = args[1].toLowerCase();
        Skill skill = plugin.getSkillManager().getSkill(skillId);

        if (skill == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("skill-not-found"));
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        sender.sendMessage(skill.getCategory().getColor() + "§l" + skill.getDisplayName());
        sender.sendMessage(ChatColor.GRAY + skill.getDescription());
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Category: " + skill.getCategory().getFormattedName());
        sender.sendMessage(ChatColor.YELLOW + "Max Level: " + ChatColor.WHITE + skill.getMaxLevel());
        sender.sendMessage(ChatColor.YELLOW + "Cost: " + ChatColor.WHITE + skill.getSkillPointCost() + " skill point(s)");

        if (skill.isActivatable()) {
            sender.sendMessage(ChatColor.AQUA + "Mana Cost: " + ChatColor.WHITE + skill.getManaCost());
            sender.sendMessage(ChatColor.AQUA + "Cooldown: " + ChatColor.WHITE + skill.getCooldown() + "s");
        }

        // Show player-specific info if sender is a player
        if (sender instanceof Player player) {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            if (data != null) {
                int level = data.getSkillLevel(skillId);
                sender.sendMessage("");
                sender.sendMessage(ChatColor.GREEN + "Your Level: " + ChatColor.WHITE + level + "/" + skill.getMaxLevel());
                if (level > 0) {
                    sender.sendMessage(ChatColor.GREEN + "Current Effect: " + ChatColor.WHITE + skill.getEffectDescription(level));
                }
                if (level < skill.getMaxLevel()) {
                    sender.sendMessage(ChatColor.GREEN + "Next Level: " + ChatColor.WHITE + skill.getEffectDescription(level + 1));
                }
            }
        }

        sender.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
    }

    private void handleActivate(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /skill activate <skill>");
            return;
        }

        String skillId = args[1].toLowerCase();
        Skill skill = plugin.getSkillManager().getSkill(skillId);

        if (skill == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("skill-not-found"));
            return;
        }

        if (!skill.isActivatable()) {
            sender.sendMessage(ChatColor.RED + "This skill is passive and cannot be activated.");
            return;
        }

        plugin.getSkillManager().activateSkill(player, skillId);
    }

    private void handleAdmin(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rpgskills.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }

        if (args.length < 2) {
            sendAdminUsage(sender);
            return;
        }

        String adminAction = args[1].toLowerCase();

        switch (adminAction) {
            case "reload" -> {
                if (!sender.hasPermission("rpgskills.admin.reload")) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return;
                }
                plugin.getConfigManager().reload();
                sender.sendMessage(plugin.getConfigManager().getMessage("config-reloaded"));
            }
            case "reset" -> {
                if (!sender.hasPermission("rpgskills.admin.reset")) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /skill admin reset <player>");
                    return;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
                    return;
                }
                plugin.getPlayerDataManager().resetPlayerData(target.getUniqueId());
                plugin.getPlayerDataManager().loadPlayerData(target);
                sender.sendMessage(plugin.getConfigManager().getMessage("data-reset")
                        .replace("{player}", target.getName()));
            }
            case "setlevel" -> {
                if (!sender.hasPermission("rpgskills.admin.setlevel")) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return;
                }
                if (args.length < 5) {
                    sender.sendMessage(ChatColor.RED + "Usage: /skill admin setlevel <player> <skill> <level>");
                    return;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
                    return;
                }
                String skillId = args[3].toLowerCase();
                Skill skill = plugin.getSkillManager().getSkill(skillId);
                if (skill == null) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("skill-not-found"));
                    return;
                }
                try {
                    int level = Integer.parseInt(args[4]);
                    level = Math.max(0, Math.min(level, skill.getMaxLevel()));
                    PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
                    if (data != null) {
                        data.setSkillLevel(skillId, level);
                        plugin.getPlayerDataManager().savePlayerData(target.getUniqueId());
                        sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s " +
                                skill.getDisplayName() + " to level " + level);
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid level number.");
                }
            }
            case "givexp" -> {
                if (!sender.hasPermission("rpgskills.admin.givexp")) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return;
                }
                if (args.length < 5) {
                    sender.sendMessage(ChatColor.RED + "Usage: /skill admin givexp <player> <category> <amount>");
                    return;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
                    return;
                }
                try {
                    SkillCategory category = SkillCategory.valueOf(args[3].toUpperCase());
                    int amount = Integer.parseInt(args[4]);
                    plugin.getSkillManager().addXp(target, amount, category);
                    sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " " +
                            category.getDisplayName() + " XP to " + target.getName());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid category or amount. Categories: COMBAT, MAGIC, SURVIVAL");
                }
            }
            default -> sendAdminUsage(sender);
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "═══ RPG Skills Commands ═══");
        sender.sendMessage(ChatColor.YELLOW + "/skills" + ChatColor.GRAY + " - Open the skills GUI");
        sender.sendMessage(ChatColor.YELLOW + "/skill info <skill>" + ChatColor.GRAY + " - View skill info");
        sender.sendMessage(ChatColor.YELLOW + "/skill activate <skill>" + ChatColor.GRAY + " - Activate a skill");
        if (sender.hasPermission("rpgskills.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/skill admin" + ChatColor.GRAY + " - Admin commands");
        }
    }

    private void sendAdminUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "═══ RPG Skills Admin ═══");
        sender.sendMessage(ChatColor.YELLOW + "/skill admin reload" + ChatColor.GRAY + " - Reload config");
        sender.sendMessage(ChatColor.YELLOW + "/skill admin reset <player>" + ChatColor.GRAY + " - Reset player data");
        sender.sendMessage(ChatColor.YELLOW + "/skill admin setlevel <player> <skill> <level>" + ChatColor.GRAY + " - Set skill level");
        sender.sendMessage(ChatColor.YELLOW + "/skill admin givexp <player> <category> <amount>" + ChatColor.GRAY + " - Give XP");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("info", "activate"));
            if (sender.hasPermission("rpgskills.admin")) {
                completions.add("admin");
            }
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("info") || sub.equals("activate")) {
                completions.addAll(plugin.getSkillManager().getSkillIds());
            } else if (sub.equals("admin")) {
                completions.addAll(Arrays.asList("reload", "reset", "setlevel", "givexp"));
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("admin")) {
            String adminSub = args[1].toLowerCase();
            if (adminSub.equals("reset") || adminSub.equals("setlevel") || adminSub.equals("givexp")) {
                Bukkit.getOnlinePlayers().forEach(p -> completions.add(p.getName()));
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("admin")) {
            if (args[1].equalsIgnoreCase("setlevel")) {
                completions.addAll(plugin.getSkillManager().getSkillIds());
            } else if (args[1].equalsIgnoreCase("givexp")) {
                for (SkillCategory cat : SkillCategory.values()) {
                    completions.add(cat.name());
                }
            }
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(lastArg))
                .collect(Collectors.toList());
    }
}

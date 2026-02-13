package com.rpgskills;

import com.rpgskills.commands.SkillCommand;
import com.rpgskills.commands.SkillsCommand;
import com.rpgskills.listeners.CombatListener;
import com.rpgskills.listeners.FarmingListener;
import com.rpgskills.listeners.FishingListener;
import com.rpgskills.listeners.GUIListener;
import com.rpgskills.listeners.MiningListener;
import com.rpgskills.listeners.PlayerListener;
import com.rpgskills.managers.ConfigManager;
import com.rpgskills.managers.CooldownManager;
import com.rpgskills.managers.GUIManager;
import com.rpgskills.managers.ManaManager;
import com.rpgskills.managers.PlayerDataManager;
import com.rpgskills.managers.SkillManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RPGSkillsPlugin extends JavaPlugin {

    private static RPGSkillsPlugin instance;

    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private SkillManager skillManager;
    private GUIManager guiManager;
    private CooldownManager cooldownManager;
    private ManaManager manaManager;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config
        saveDefaultConfig();

        // Initialize managers
        configManager = new ConfigManager(this);
        skillManager = new SkillManager(this);
        playerDataManager = new PlayerDataManager(this);
        cooldownManager = new CooldownManager(this);
        manaManager = new ManaManager(this);
        guiManager = new GUIManager(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new MiningListener(this), this);
        getServer().getPluginManager().registerEvents(new FarmingListener(this), this);
        getServer().getPluginManager().registerEvents(new FishingListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        // Register commands
        getCommand("skills").setExecutor(new SkillsCommand(this));
        getCommand("skill").setExecutor(new SkillCommand(this));
        getCommand("skill").setTabCompleter(new SkillCommand(this));

        // Start mana regeneration task
        manaManager.startRegenTask();

        getLogger().info("RPG Skills plugin enabled!");
    }

    @Override
    public void onDisable() {
        // Save all player data
        if (playerDataManager != null) {
            playerDataManager.saveAllPlayers();
        }

        // Cancel mana regen task
        if (manaManager != null) {
            manaManager.stopRegenTask();
        }

        getLogger().info("RPG Skills plugin disabled!");
    }

    public static RPGSkillsPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public GUIManager getGUIManager() {
        return guiManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public ManaManager getManaManager() {
        return manaManager;
    }
}

package com.rpgskills.managers;

import com.rpgskills.RPGSkillsPlugin;
import com.rpgskills.models.PlayerData;
import com.rpgskills.models.SkillCategory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Manages loading, saving, and caching player data using YAML files.
 */
public class PlayerDataManager {

    private final RPGSkillsPlugin plugin;
    private final File dataFolder;
    private final Map<UUID, PlayerData> playerDataCache;

    public PlayerDataManager(RPGSkillsPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        this.playerDataCache = new HashMap<>();

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    /**
     * Get player data, loading from file if not cached.
     */
    public PlayerData getPlayerData(UUID uuid) {
        return playerDataCache.get(uuid);
    }

    /**
     * Get player data for an online player.
     */
    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    /**
     * Load player data from file or create new.
     */
    public PlayerData loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();

        if (playerDataCache.containsKey(uuid)) {
            PlayerData data = playerDataCache.get(uuid);
            data.setPlayerName(player.getName());
            return data;
        }

        File file = getPlayerFile(uuid);
        PlayerData data;

        if (file.exists()) {
            data = loadFromFile(file, uuid, player.getName());
        } else {
            data = new PlayerData(uuid, player.getName());
            data.setSkillPoints(plugin.getConfigManager().getStartingSkillPoints());
            double maxMana = plugin.getConfigManager().getMaxManaForLevel(1);
            data.setMaxMana(maxMana);
            data.setCurrentMana(maxMana);
        }

        playerDataCache.put(uuid, data);
        return data;
    }

    /**
     * Save player data to file.
     */
    public void savePlayerData(UUID uuid) {
        PlayerData data = playerDataCache.get(uuid);
        if (data == null) return;

        File file = getPlayerFile(uuid);
        YamlConfiguration config = new YamlConfiguration();

        config.set("uuid", uuid.toString());
        config.set("name", data.getPlayerName());
        config.set("level", data.getLevel());
        config.set("experience", data.getExperience());
        config.set("skill-points", data.getSkillPoints());
        config.set("current-mana", data.getCurrentMana());
        config.set("max-mana", data.getMaxMana());

        // Save skill levels
        for (Map.Entry<String, Integer> entry : data.getSkillLevels().entrySet()) {
            config.set("skills." + entry.getKey(), entry.getValue());
        }

        // Save category XP
        for (Map.Entry<String, Integer> entry : data.getCategoryXpMap().entrySet()) {
            config.set("category-xp." + entry.getKey(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data for " + uuid, e);
        }
    }

    /**
     * Save all cached player data.
     */
    public void saveAllPlayers() {
        for (UUID uuid : playerDataCache.keySet()) {
            savePlayerData(uuid);
        }
    }

    /**
     * Unload player data (save and remove from cache).
     */
    public void unloadPlayerData(UUID uuid) {
        savePlayerData(uuid);
        playerDataCache.remove(uuid);
    }

    /**
     * Reset player data.
     */
    public void resetPlayerData(UUID uuid) {
        PlayerData data = playerDataCache.get(uuid);
        if (data != null) {
            String name = data.getPlayerName();
            PlayerData newData = new PlayerData(uuid, name);
            newData.setSkillPoints(plugin.getConfigManager().getStartingSkillPoints());
            double maxMana = plugin.getConfigManager().getMaxManaForLevel(1);
            newData.setMaxMana(maxMana);
            newData.setCurrentMana(maxMana);
            playerDataCache.put(uuid, newData);
            savePlayerData(uuid);
        } else {
            // Delete the file if it exists
            File file = getPlayerFile(uuid);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private File getPlayerFile(UUID uuid) {
        return new File(dataFolder, uuid.toString() + ".yml");
    }

    private PlayerData loadFromFile(File file, UUID uuid, String name) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        PlayerData data = new PlayerData(uuid, name);
        data.setLevel(config.getInt("level", 1));
        data.setExperience(config.getInt("experience", 0));
        data.setSkillPoints(config.getInt("skill-points", 0));
        data.setMaxMana(config.getDouble("max-mana", plugin.getConfigManager().getMaxManaForLevel(data.getLevel())));
        data.setCurrentMana(config.getDouble("current-mana", data.getMaxMana()));

        // Load skill levels
        if (config.isConfigurationSection("skills")) {
            for (String key : config.getConfigurationSection("skills").getKeys(false)) {
                data.setSkillLevel(key, config.getInt("skills." + key, 0));
            }
        }

        // Load category XP
        if (config.isConfigurationSection("category-xp")) {
            for (String key : config.getConfigurationSection("category-xp").getKeys(false)) {
                try {
                    SkillCategory cat = SkillCategory.valueOf(key);
                    data.setCategoryXp(cat, config.getInt("category-xp." + key, 0));
                } catch (IllegalArgumentException ignored) {
                    // Skip invalid categories
                }
            }
        }

        return data;
    }
}

import { world } from "./mock_minecraft_server.js";
import { CONFIG, SKILLS } from "./config.js";

const DB_KEY = "rpg_player_data";

export class Database {
    static getPlayerData(player) {
        const data = player.getDynamicProperty(DB_KEY);
        if (data) {
            try {
                const parsed = JSON.parse(data);
                // Data Migration / Initialization for new fields
                if (parsed.version < 2) {
                    parsed.coins = parsed.coins ?? 0;
                    parsed.gems = parsed.gems ?? 0;
                    parsed.quests = parsed.quests ?? { active: [], completed: [], daily: [], lastUpdated: 0 };
                    parsed.stats = parsed.stats ?? {};
                    parsed.lastSelectedAbility = parsed.lastSelectedAbility ?? null;
                    parsed.settings.sounds = parsed.settings.sounds ?? true;
                    parsed.settings.xpPopups = parsed.settings.xpPopups ?? true;
                    parsed.version = 2;
                    this.savePlayerData(player, parsed);
                }
                return parsed;
            } catch (e) {
                console.error("Failed to parse player data: " + e);
                // Security Fix: Backup corrupted data instead of silent loss
                try {
                    player.setDynamicProperty(DB_KEY + "_corrupted", data);
                    console.warn(`Corrupted player data for ${player.name} has been backed up to ${DB_KEY}_corrupted`);
                } catch (backupError) {
                    console.error("Failed to backup corrupted player data: " + backupError);
                }
            }
        }
        return this.initializePlayer(player);
    }

    static savePlayerData(player, data) {
        player.setDynamicProperty(DB_KEY, JSON.stringify(data));
    }

    static initializePlayer(player) {
        const defaultData = {
            version: CONFIG.DATA_VERSION,
            globalLevel: 1,
            globalXp: 0,
            mana: 100,
            maxMana: 100,
            coins: 0,
            gems: 0,
            skills: {},
            perks: {},
            quests: {
                active: [],
                completed: [],
                daily: [],
                lastUpdated: 0
            },
            stats: {
                bonusDamage: 0,
                bonusDefense: 0,
                critChance: 5,
                lifesteal: 0,
                miningFortune: 0
            },
            lastSelectedAbility: null,
            settings: {
                particles: true,
                notifications: true,
                sounds: true,
                xpPopups: true
            }
        };

        for (const skill of Object.values(SKILLS)) {
            defaultData.skills[skill] = { level: 1, xp: 0 };
        }

        this.savePlayerData(player, defaultData);
        return defaultData;
    }
}

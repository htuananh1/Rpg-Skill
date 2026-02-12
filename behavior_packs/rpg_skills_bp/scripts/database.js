import { world } from "@minecraft/server";
import { CONFIG, SKILLS } from "./config.js";

const DB_KEY = "rpg_player_data";

export class Database {
    static getPlayerData(player) {
        const data = player.getDynamicProperty(DB_KEY);
        if (data) {
            try {
                return JSON.parse(data);
            } catch (e) {
                console.error("Failed to parse player data: " + e);
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
            skills: {},
            perks: {},
            settings: {
                particles: true,
                notifications: true
            }
        };

        for (const skill of Object.values(SKILLS)) {
            defaultData.skills[skill] = { level: 1, xp: 0 };
        }

        this.savePlayerData(player, defaultData);
        return defaultData;
    }
}

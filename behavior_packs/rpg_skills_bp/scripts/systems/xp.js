import { Database } from "../database.js";
import { CONFIG, getXpRequired } from "../config.js";
import { StatsSystem } from "./stats.js";

const lastXpGain = new Map(); // player_id -> { skill: last_time }

export class XpSystem {
    static addXp(player, skill, amount) {
        const now = Date.now();
        const playerKey = player.id + "_" + skill;
        const lastGain = lastXpGain.get(playerKey) || 0;

        if (now - lastGain < CONFIG.ANTI_EXPLOIT_COOLDOWN) {
            return;
        }
        lastXpGain.set(playerKey, now);

        const data = Database.getPlayerData(player);
        const skillData = data.skills[skill];

        if (!skillData) return;

        skillData.xp += amount;
        data.globalXp += amount;

        let leveledUp = false;
        while (skillData.xp >= getXpRequired(skillData.level) && skillData.level < CONFIG.MAX_LEVEL) {
            skillData.xp -= getXpRequired(skillData.level);
            skillData.level++;
            leveledUp = true;
            this.onSkillLevelUp(player, skill, skillData.level);
        }

        const oldGlobalLevel = data.globalLevel;
        const totalLevels = Object.values(data.skills).reduce((acc, s) => acc + s.level, 0);
        data.globalLevel = Math.floor(totalLevels / Object.keys(data.skills).length);

        if (data.globalLevel > oldGlobalLevel) {
            this.onGlobalLevelUp(player, data, data.globalLevel);
        }

        Database.savePlayerData(player, data);

        if (data.settings.notifications && !leveledUp) {
            player.onScreenDisplay.setActionBar({ translate: "rpg.notification.xp_gain", with: [amount.toString(), "rpg.skill." + skill] });
        }
    }

    static onSkillLevelUp(player, skill, newLevel) {
        player.sendMessage({ translate: "rpg.notification.level_up", with: ["rpg.skill." + skill, newLevel.toString()] });
        player.playSound("random.levelup");
        StatsSystem.recalculateStats(player);
    }

    static onGlobalLevelUp(player, data, newLevel) {
        player.sendMessage({ translate: "rpg.notification.global_level_up", with: [newLevel.toString()] });
        player.playSound("random.toast");
        data.maxMana += 10;
        data.mana = data.maxMana;
        StatsSystem.recalculateStats(player);
    }

    static onPlayerLeave(playerId) {
        for (const key of lastXpGain.keys()) {
            if (key.startsWith(playerId + "_")) {
                lastXpGain.delete(key);
            }
        }
    }
}

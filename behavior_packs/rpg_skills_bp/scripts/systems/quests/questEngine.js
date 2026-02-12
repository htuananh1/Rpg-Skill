import { Database } from "../../database.js";
import { QUEST_TEMPLATES, CONFIG } from "../../config.js";

export class QuestEngine {
    static updateDailyQuests(player) {
        const data = Database.getPlayerData(player);
        const now = Date.now();
        const oneDay = 24 * 60 * 60 * 1000;

        // Use date-based seeding for rotation (simplified)
        const todaySeed = Math.floor(now / oneDay);

        if (data.quests.lastUpdated < todaySeed) {
            // New day, rotate quests
            const available = [...QUEST_TEMPLATES];
            const selected = [];

            for (let i = 0; i < CONFIG.DAILY_QUEST_COUNT && available.length > 0; i++) {
                const index = (todaySeed + i) % available.length;
                selected.push(available.splice(index, 1)[0]);
            }

            data.quests.daily = selected;
            data.quests.completed = []; // Reset dailies completed
            data.quests.lastUpdated = todaySeed;
            Database.savePlayerData(player, data);
        }
    }

    static progressQuest(player, type, target, amount = 1) {
        const data = Database.getPlayerData(player);
        let changed = false;

        for (const quest of data.quests.active) {
            if (quest.type === type && (!quest.target || quest.target === target || (target && target.includes(quest.target)))) {
                if (quest.progress < quest.amount) {
                    quest.progress = Math.min(quest.amount, quest.progress + amount);
                    changed = true;
                    if (quest.progress >= quest.amount) {
                        player.onScreenDisplay.setActionBar("§aQuest Complete: " + quest.name);
                        if (data.settings.sounds) player.playSound("random.levelup");
                    }
                }
            }
        }

        if (changed) {
            Database.savePlayerData(player, data);
        }
    }
}

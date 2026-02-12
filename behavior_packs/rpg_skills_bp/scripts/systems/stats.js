import { Database } from "../database.js";
import { SKILLS } from "../config.js";

export class StatsSystem {
    static recalculateStats(player) {
        const data = Database.getPlayerData(player);
        const skills = data.skills;

        const newStats = {
            bonusDamage: Math.floor(skills[SKILLS.COMBAT].level * 0.5 + skills[SKILLS.ARCHERY].level * 0.3),
            bonusDefense: Math.floor(skills[SKILLS.DEFENSE].level * 0.8),
            critChance: 5 + Math.floor(skills[SKILLS.AGILITY].level * 0.2),
            lifesteal: Math.floor(skills[SKILLS.COMBAT].level / 20),
            miningFortune: Math.floor(skills[SKILLS.MINING].level / 10)
        };

        data.stats = newStats;
        Database.savePlayerData(player, data);
    }
}

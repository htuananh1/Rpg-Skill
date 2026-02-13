import { system } from "@minecraft/server";
import { Database } from "../../database.js";

export function handleArchery(player) {
    const data = Database.getPlayerData(player);
    const level = data.skills.archery.level;

    // Perk: Quick Draw (Unlocked at lvl 15)
    if (level >= 15) {
        // Increases projectile velocity slightly
        // This could be handled via events or buff system
    }

    // Perk: Eagle Eye (Unlocked at lvl 30)
    if (level >= 30) {
        // Increases critical hit chance with bows
    }

    // Perk: Multi-Shot (Unlocked at lvl 50)
    if (level >= 50) {
        // Chance to fire multiple arrows at once
    }
}

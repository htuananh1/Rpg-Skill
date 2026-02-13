import { system } from "@minecraft/server";
import { Database } from "../../database.js";

export function handleFishing(player) {
    const data = Database.getPlayerData(player);
    const level = data.skills.fishing.level;

    // Perk: Lucky Catch (Unlocked at lvl 20)
    if (level >= 20) {
        // Increases chance of catching rare items
    }

    // Perk: Fisherman's Luck (Unlocked at lvl 40)
    if (level >= 40) {
        // Reduces fishing time
    }

    // Perk: Treasure Hunter (Unlocked at lvl 60)
    if (level >= 60) {
        // Significantly increases chance of rare loot
    }
}

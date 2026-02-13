import { system } from "@minecraft/server";
import { Database } from "../../database.js";

export function handleAlchemy(player) {
    const data = Database.getPlayerData(player);
    const level = data.skills.alchemy.level;

    // Perk: Potion Master (Unlocked at lvl 25)
    if (level >= 25) {
        // Increases potion duration
    }

    // Perk: Enchantment Expert (Unlocked at lvl 45)
    if (level >= 45) {
        // Increases enchantment levels
    }

    // Perk: Transmutation (Unlocked at lvl 70)
    if (level >= 70) {
        // Chance to duplicate items when enchanting
    }
}

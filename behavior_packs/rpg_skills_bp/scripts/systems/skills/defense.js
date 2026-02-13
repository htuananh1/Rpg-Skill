import { system } from "@minecraft/server";
import { Database } from "../../database.js";

export function handleDefense(player) {
    const data = Database.getPlayerData(player);
    const level = data.skills.defense.level;

    // Perk: Damage Reduction (Unlocked at lvl 10)
    if (level >= 10) {
        // Reduces incoming damage
    }

    // Perk: Shield Master (Unlocked at lvl 30)
    if (level >= 30) {
        // Increases shield effectiveness
    }

    // Perk: Iron Skin (Unlocked at lvl 50)
    if (level >= 50) {
        // Significant damage reduction
    }
}

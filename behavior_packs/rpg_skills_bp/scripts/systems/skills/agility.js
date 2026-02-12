import { Database } from "../../database.js";

export function handleAgility(player) {
    const data = Database.getPlayerData(player);
    const level = data.skills.agility.level;

    // Perk: Fall Damage Reduction (Passive)
    // This is better handled in entityHurt but we can set a tag or property
    if (level >= 20) {
        // Reduced fall damage logic will be in events.js using this level
    }
}

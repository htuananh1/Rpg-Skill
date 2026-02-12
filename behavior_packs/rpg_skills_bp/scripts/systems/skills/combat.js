import { Database } from "../../database.js";

export function handleCombat(player, target) {
    const data = Database.getPlayerData(player);
    const level = data.skills.combat.level;

    // Perk: Crit Chance (Passive)
    if (level >= 10 && Math.random() < 0.05 + (level * 0.001)) {
        target.applyDamage(2);
        player.onScreenDisplay.setActionBar("§cCRITICAL HIT!§r");
    }

    // Perk: Lifesteal
    if (level >= 40 && Math.random() < 0.1) {
        player.runCommandAsync("effect @s regeneration 1 1 true");
    }
}

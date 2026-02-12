import { world, system } from "@minecraft/server";
import { registerEvents } from "./events.js";
import { Database } from "./database.js";
import { CONFIG } from "./config.js";

// Initialize system
registerEvents();

// Mana Regeneration Loop
system.runInterval(() => {
    for (const player of world.getAllPlayers()) {
        const data = Database.getPlayerData(player);
        if (data.mana < data.maxMana) {
            data.mana = Math.min(data.maxMana, data.mana + CONFIG.MANA_REGEN_AMOUNT);
            Database.savePlayerData(player, data);
        }
    }
}, CONFIG.MANA_REGEN_INTERVAL);

console.warn("RPG Skills Addon Loaded!");

import { world, system } from "@minecraft/server";
import { registerEvents } from "./events.js";
import { Database } from "./database.js";
import { CONFIG } from "./config.js";
import { BlockTracker } from "./systems/block_tracker.js";

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

// Anti-Exploit Data Persistence
system.runInterval(() => {
    BlockTracker.save();
}, 1200); // Save every 1 minute

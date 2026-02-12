import { isOre } from "./utils.js";
import { world, system, MolangVariableMap } from "@minecraft/server";
import { Database } from "../../database.js";

export function handleMining(player, block) {
    const data = Database.getPlayerData(player);
    const level = data.skills.mining.level;

    // Perk: Extra drops (Passive)
    if (level >= 10 && Math.random() < 0.1) {
        // Simple extra drop logic
        // This is complex to do perfectly without breaking loot tables,
        // but we can spawn an item at the location.
    }

    // Perk: Vein-sense (Active or Passive)
    if (level >= 20 && data.settings.particles) {
        // Highlight nearby ores
        const dim = player.dimension;
        const pos = block.location;
        system.run(() => {
            for (let x = -3; x <= 3; x++) {
                for (let y = -3; y <= 3; y++) {
                    for (let z = -3; z <= 3; z++) {
                        const b = dim.getBlock({ x: pos.x + x, y: pos.y + y, z: pos.z + z });
                        if (b && isOre(b.typeId)) {
                             dim.spawnParticle("minecraft:villager_happy", { x: b.location.x + 0.5, y: b.location.y + 0.5, z: b.location.z + 0.5 });
                        }
                    }
                }
            }
        });
    }
}

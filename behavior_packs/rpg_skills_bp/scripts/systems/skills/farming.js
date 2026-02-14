import { system } from "@minecraft/server";
import { Database } from "../../database.js";

export function handleFarming(player) {
    const data = Database.getPlayerData(player);
    const level = data.skills.farming.level;

    // Perk: Growth Aura (Unlocked at lvl 25)
    if (level >= 25 && system.currentTick % 100 === 0) {
        const dim = player.dimension;
        const pos = player.location;
        for (let x = -3; x <= 3; x++) {
            for (let z = -3; z <= 3; z++) {
                const b = dim.getBlock({ x: pos.x + x, y: pos.y, z: pos.z + z });
                if (b && (b.typeId.includes("wheat") || b.typeId.includes("carrot") || b.typeId.includes("potato"))) {
                    // Bone meal effect (randomly)
                    if (Math.random() < 0.1) {
                        const currentGrowth = b.permutation.getState("growth") ?? 0;
                        const newGrowth = Math.min(7, currentGrowth + 1);
                        // FIX: Use Script API setPermutation instead of invalid setblock command syntax
                        b.setPermutation(b.permutation.withState("growth", newGrowth));
                    }
                }
            }
        }
    }
}

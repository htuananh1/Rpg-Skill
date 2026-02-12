import { isGrowthAuraTarget } from "./utils.js";
import { world, system } from "@minecraft/server";
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
                if (b && isGrowthAuraTarget(b.typeId)) {
                    // Bone meal effect (randomly)
                    if (Math.random() < 0.1) {
                        dim.runCommandAsync(`setblock ${b.location.x} ${b.location.y} ${b.location.z} ${b.typeId} ${Math.min(7, b.permutation.getState("growth") + 1)}`);
                    }
                }
            }
        }
    }
}

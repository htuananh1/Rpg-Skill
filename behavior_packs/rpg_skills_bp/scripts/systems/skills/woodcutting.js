import { world, system } from "@minecraft/server";
import { Database } from "../../database.js";
import { SKILLS } from "../../config.js";
import { XpSystem } from "../xp.js";

export function handleWoodcutting(player, block) {
    const data = Database.getPlayerData(player);
    const level = data.skills.woodcutting.level;

    // Perk: Tree-feller (Unlocked at lvl 15)
    if (level >= 15 && player.isSneaking) {
        const dim = player.dimension;
        const startPos = block.location;
        const visited = new Set();
        const queue = [startPos];
        const logsToBreak = [];
        const MAX_LOGS = 64;

        visited.add(`${startPos.x},${startPos.y},${startPos.z}`);

        // BFS to find connected logs
        while (queue.length > 0 && logsToBreak.length < MAX_LOGS) {
            const pos = queue.shift();

            // Check 3x3x3 area around current position
            for (let x = -1; x <= 1; x++) {
                for (let y = -1; y <= 1; y++) {
                    for (let z = -1; z <= 1; z++) {
                        if (x === 0 && y === 0 && z === 0) continue;

                        const nextPos = { x: pos.x + x, y: pos.y + y, z: pos.z + z };
                        const key = `${nextPos.x},${nextPos.y},${nextPos.z}`;

                        if (visited.has(key)) continue;

                        const b = dim.getBlock(nextPos);
                        if (b && (b.typeId.includes("log") || b.typeId.includes("wood"))) {
                            visited.add(key);
                            queue.push(nextPos);
                            logsToBreak.push(nextPos);
                        }
                    }
                }
            }
        }

        // Break logs over time to reduce lag
        if (logsToBreak.length > 0) {
            let index = 0;
            const breakLoop = () => {
                if (!player.isValid()) return;

                if (index >= logsToBreak.length) {
                    // Award XP for the extra logs
                    // 5 XP per log (matching the base amount in events.js)
                    XpSystem.addXp(player, SKILLS.WOODCUTTING, logsToBreak.length * 5);
                    return;
                }

                // Break up to 3 blocks per tick
                for (let i = 0; i < 3 && index < logsToBreak.length; i++) {
                     const p = logsToBreak[index++];
                     // Use runCommandAsync to drop items
                     dim.runCommandAsync(`setblock ${p.x} ${p.y} ${p.z} air destroy`);
                }

                system.run(breakLoop);
            };
            system.run(breakLoop);
        }
    }
}

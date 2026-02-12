import { world, system } from "@minecraft/server";
import { Database } from "../../database.js";

export function handleWoodcutting(player, block) {
    const data = Database.getPlayerData(player);
    const level = data.skills.woodcutting.level;

    // Perk: Tree-feller (Unlocked at lvl 15)
    if (level >= 15 && player.isSneaking) {
        const dim = player.dimension;
        let logsFound = 0;
        let currentBlock = block;

        // Simple vertical feller
        const fellerLoop = (pos) => {
            if (logsFound > 20) return;
            const upPos = { x: pos.x, y: pos.y + 1, z: pos.z };
            const upBlock = dim.getBlock(upPos);
            if (upBlock && (upBlock.typeId.includes("log") || upBlock.typeId.includes("wood"))) {
                logsFound++;
                dim.runCommandAsync(`setblock ${upPos.x} ${upPos.y} ${upPos.z} air destroy`);
                fellerLoop(upPos);
            }
        };
        fellerLoop(block.location);
    }
}

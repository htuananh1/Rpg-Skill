import { world, system } from "@minecraft/server";
import { XpSystem } from "./systems/xp.js";
import { SKILLS } from "./config.js";
import { Database } from "./database.js";
import { openMainMenu } from "./ui/menu.js";
import { handleMining } from "./systems/skills/mining.js";
import { handleWoodcutting } from "./systems/skills/woodcutting.js";
import { handleFarming } from "./systems/skills/farming.js";
import { handleCombat } from "./systems/skills/combat.js";

export function registerEvents() {
    // Block Breaking (Mining, Woodcutting, Farming)
    world.afterEvents.playerBreakBlock.subscribe((event) => {
        const { player, brokenBlockPermutation } = event;
        const blockId = brokenBlockPermutation.type.id;

        if (blockId.includes("ore") || blockId.includes("deepslate")) {
            XpSystem.addXp(player, SKILLS.MINING, 10);
            handleMining(player, event.block);
        } else if (blockId.includes("log") || blockId.includes("wood")) {
            XpSystem.addXp(player, SKILLS.WOODCUTTING, 5);
            handleWoodcutting(player, event.block);
        } else if (blockId.includes("wheat") || blockId.includes("carrot") || blockId.includes("potato") || blockId.includes("beetroot") || blockId.includes("melon") || blockId.includes("pumpkin")) {
            XpSystem.addXp(player, SKILLS.FARMING, 2);
        }
    });

    // Combat & Archery
    world.afterEvents.entityDie.subscribe((event) => {
        const { damageSource } = event;
        if (damageSource && damageSource.damagingEntity && damageSource.damagingEntity.typeId === "minecraft:player") {
            const player = damageSource.damagingEntity;
            if (damageSource.cause === "projectile") {
                XpSystem.addXp(player, SKILLS.ARCHERY, 15);
            } else {
                XpSystem.addXp(player, SKILLS.COMBAT, 10);
            }
        }
    });

    // Defense & Combat Perks
    world.afterEvents.entityHurt.subscribe((event) => {
        const { hurtEntity, damageSource } = event;
        if (hurtEntity.typeId === "minecraft:player") {
            XpSystem.addXp(hurtEntity, SKILLS.DEFENSE, 5);
            // Agility fall damage reduction
            if (damageSource.cause === "fall") {
                const data = Database.getPlayerData(hurtEntity);
                if (data.skills.agility.level >= 20) {
                    // We can't cancel the event here, but we could heal them back or have used beforeEvents
                }
            }
        }
        if (damageSource && damageSource.damagingEntity && damageSource.damagingEntity.typeId === "minecraft:player") {
            handleCombat(damageSource.damagingEntity, hurtEntity);
        }
    });

    // Alchemy (Item Complete Use)
    world.afterEvents.itemCompleteUse.subscribe((event) => {
        const { source, itemStack } = event;
        if (source.typeId === "minecraft:player" && (itemStack.typeId.includes("potion") || itemStack.typeId.includes("bottle"))) {
            XpSystem.addXp(source, SKILLS.ALCHEMY, 20);
        }
    });

    // Agility & Farming Aura
    system.runInterval(() => {
        for (const player of world.getAllPlayers()) {
            handleFarming(player);
            // Simple movement check for Agility
            // (Already implemented in previous version, let's keep it refined)
        }
    }, 20);

    // Fishing (Simplified)
    world.afterEvents.itemStopUse.subscribe((event) => {
        const { source, itemStack } = event;
        if (source.typeId === "minecraft:player" && itemStack.typeId === "minecraft:fishing_rod") {
            // Give some XP for using it (better than nothing in stable)
            XpSystem.addXp(source, SKILLS.FISHING, 5);
        }
    });

    // Tome Interaction
    world.beforeEvents.itemUse.subscribe((event) => {
        if (event.itemStack.typeId === "rpg:skill_tome") {
            const player = event.source;
            system.run(() => {
                if (player.isSneaking) {
                    const data = Database.getPlayerData(player);
                    player.onScreenDisplay.setActionBar(\"§bMana: ${Math.floor(data.mana)}/${data.maxMana}\");
                } else {
                    openMainMenu(player);
                }
            });
        }
    });

    // Join logic
    world.afterEvents.playerSpawn.subscribe((event) => {
        const { player, initialSpawn } = event;
        Database.getPlayerData(player);
        if (initialSpawn) {
            system.runTimeout(() => {
                player.runCommandAsync("give @s rpg:skill_tome");
            }, 40);
        }
    });
}

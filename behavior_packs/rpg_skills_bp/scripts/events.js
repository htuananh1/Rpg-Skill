import { world, system } from "@minecraft/server";
import { XpSystem } from "./systems/xp.js";
import { SKILLS, CONFIG } from "./config.js";
import { Database } from "./database.js";
import { openMainMenu } from "./ui/menu.js";
import { openQuestsMenu } from "./ui/quests_ui.js";
import { openAdminMenu } from "./ui/admin_ui.js";
import { QuestEngine } from "./systems/quests/questEngine.js";
import { Economy } from "./systems/economy.js";
import { StatsSystem } from "./systems/stats.js";
import { handleMining } from "./systems/skills/mining.js";
import { handleWoodcutting } from "./systems/skills/woodcutting.js";
import { handleFarming } from "./systems/skills/farming.js";
import { handleCombat } from "./systems/skills/combat.js";
import { handleArchery } from "./systems/skills/archery.js";
import { handleFishing } from "./systems/skills/fishing.js";
import { handleAlchemy } from "./systems/skills/alchemy.js";
import { handleAgility } from "./systems/skills/agility.js";
import { handleDefense } from "./systems/skills/defense.js";
import { BlockTracker } from "./systems/block_tracker.js";

export function registerEvents() {
    // Block Breaking
        // Block Placement Tracking (Anti-Exploit)
    world.afterEvents.playerPlaceBlock.subscribe((event) => {
        const { block } = event;
        const blockId = block.typeId;

        // Track blocks that give XP to prevent place-break farming
        if (blockId.includes("ore") || blockId.includes("deepslate") ||
            blockId.includes("log") || blockId.includes("wood") ||
            blockId.includes("melon") || blockId.includes("pumpkin")) {
            BlockTracker.addBlock(block.location, block.dimension.id);
        }
    });

    // Block Breaking
    world.afterEvents.playerBreakBlock.subscribe((event) => {
        const { player, brokenBlockPermutation, block } = event;
        const blockId = brokenBlockPermutation.type.id;

        // Check if block was player-placed
        const isPlayerPlaced = BlockTracker.removeBlock(block.location, player.dimension.id);
        if (isPlayerPlaced) return;

        QuestEngine.progressQuest(player, "break", blockId);

        if (blockId.includes("ore") || blockId.includes("deepslate")) {
            XpSystem.addXp(player, SKILLS.MINING, 10);
            Economy.addCoins(player, 2, "Mining");
            handleMining(player, event.block);
        } else if (blockId.includes("log") || blockId.includes("wood")) {
            XpSystem.addXp(player, SKILLS.WOODCUTTING, 5);
            Economy.addCoins(player, 1, "Woodcutting");
            handleWoodcutting(player, event.block);
        } else if (blockId.includes("wheat") || blockId.includes("carrot") || blockId.includes("potato") || blockId.includes("beetroot") || blockId.includes("melon") || blockId.includes("pumpkin")) {
            // Special check for crops: must be fully grown
            if (blockId.includes("melon") || blockId.includes("pumpkin")) {
                XpSystem.addXp(player, SKILLS.FARMING, 2);
                Economy.addCoins(player, 1, "Farming");
            } else {
                try {
                    const growth = brokenBlockPermutation.getState("growth");
                    const isFullyGrown = (blockId.includes("beetroot") ? growth === 3 : growth === 7);

                    if (isFullyGrown) {
                        XpSystem.addXp(player, SKILLS.FARMING, 2);
                        Economy.addCoins(player, 1, "Farming");
                    }
                } catch (e) {
                    // In case growth state is missing
                }
            }
        }
    });

    // Combat
    world.afterEvents.entityDie.subscribe((event) => {
        const { damageSource, deadEntity } = event;
        if (damageSource && damageSource.damagingEntity && damageSource.damagingEntity.typeId === "minecraft:player") {
            const player = damageSource.damagingEntity;
            QuestEngine.progressQuest(player, "kill", deadEntity.typeId);

            if (damageSource.cause === "projectile") {
                XpSystem.addXp(player, SKILLS.ARCHERY, 15);
            } else {
                XpSystem.addXp(player, SKILLS.COMBAT, 10);
            }
            Economy.addCoins(player, 5, "Combat");
        }
    });

    // Fishing — FIX: playerFish event does not exist; use itemUse with fishing rod detection
    world.afterEvents.itemUse.subscribe((event) => {
        const player = event.source;
        if (event.itemStack && event.itemStack.typeId === "minecraft:fishing_rod") {
            XpSystem.addXp(player, SKILLS.FISHING, 20);
            QuestEngine.progressQuest(player, "fish", "any");
            Economy.addCoins(player, 10, "Fishing");
        }
    });

    // Item Interaction
    world.beforeEvents.itemUse.subscribe((event) => {
        const player = event.source;
        const item = event.itemStack;

        if (item.typeId === "rpgskills:skill_tome") {
            system.run(() => {
                if (player.isSneaking) {
                    const data = Database.getPlayerData(player);
                    if (data.lastSelectedAbility) {
                        player.sendMessage("§bCasting last ability: " + data.lastSelectedAbility);
                    } else {
                        player.sendMessage("§cNo ability selected!");
                    }
                } else {
                    openMainMenu(player);
                }
            });
        } else if (item.typeId === "rpgskills:menu_compass") {
            system.run(() => openMainMenu(player));
        } else if (item.typeId === "rpgskills:quest_journal") {
            system.run(() => openQuestsMenu(player));
        } else if (item.typeId === "rpgskills:admin_wand") {
            system.run(() => openAdminMenu(player));
        }
    });

    // Join logic & Rotation
    world.afterEvents.playerSpawn.subscribe((event) => {
        const { player, initialSpawn } = event;
        QuestEngine.updateDailyQuests(player);
        StatsSystem.recalculateStats(player);

        if (initialSpawn) {
            const data = Database.getPlayerData(player);
            system.runTimeout(() => {
                if (player.getComponent("inventory").container.emptySlotsCount > 0) {
                    player.runCommandAsync("give @s rpgskills:menu_compass");
                }
            }, 40);
        }
    });

    // Cleanup on leave
    world.afterEvents.playerLeave.subscribe((event) => {
        XpSystem.onPlayerLeave(event.playerId);
    });

    // Periodical Stats & Quests Check
    system.runInterval(() => {
        for (const player of world.getAllPlayers()) {
            handleFarming(player);
            handleAgility(player);
            handleArchery(player);
            handleFishing(player);
            handleAlchemy(player);
            handleDefense(player);
            // Optionally update derived stats if buffs changed
        }
    }, 100);
}

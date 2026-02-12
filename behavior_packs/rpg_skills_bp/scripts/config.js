export const CONFIG = {
    MAX_LEVEL: 100,
    BASE_XP: 100,
    XP_EXPONENT: 1.5,
    MANA_REGEN_INTERVAL: 20, // ticks
    MANA_REGEN_AMOUNT: 5,
    DATA_VERSION: 2,
    SAVE_INTERVAL: 6000,
    ANTI_EXPLOIT_COOLDOWN: 500,
    ADMIN_TAG: "rpg_admin",
    DEFAULT_CURRENCY: "coins",
    DAILY_QUEST_COUNT: 5
};

export const SKILLS = {
    MINING: "mining",
    WOODCUTTING: "woodcutting",
    FARMING: "farming",
    COMBAT: "combat",
    ARCHERY: "archery",
    FISHING: "fishing",
    ALCHEMY: "alchemy",
    AGILITY: "agility",
    DEFENSE: "defense"
};

export function getXpRequired(level) {
    return Math.floor(CONFIG.BASE_XP * Math.pow(level, CONFIG.XP_EXPONENT));
}

export const SHOP_ITEMS = [
    { id: "minecraft:iron_pickaxe", price: 100, category: "Tools", req: { skill: SKILLS.MINING, level: 5 } },
    { id: "minecraft:diamond_pickaxe", price: 1000, category: "Tools", req: { skill: SKILLS.MINING, level: 20 } },
    { id: "minecraft:iron_sword", price: 150, category: "Weapons", req: { skill: SKILLS.COMBAT, level: 5 } },
    { id: "minecraft:diamond_sword", price: 1500, category: "Weapons", req: { skill: SKILLS.COMBAT, level: 25 } },
    { id: "minecraft:iron_chestplate", price: 200, category: "Armor", req: { skill: SKILLS.DEFENSE, level: 10 } },
    { id: "minecraft:diamond_chestplate", price: 2000, category: "Armor", req: { skill: SKILLS.DEFENSE, level: 30 } },
    { id: "minecraft:enchanted_book", price: 500, category: "Utilities" },
    { id: "minecraft:golden_apple", price: 300, category: "Utilities" }
];

export const QUEST_TEMPLATES = [
    { id: "mine_stone", name: "Stone Breaker", description: "Mine 50 stone blocks.", type: "break", target: "minecraft:stone", amount: 50, reward: { coins: 50, xp: { [SKILLS.MINING]: 100 } } },
    { id: "kill_zombies", name: "Zombie Hunter", description: "Kill 10 zombies.", type: "kill", target: "minecraft:zombie", amount: 10, reward: { coins: 100, xp: { [SKILLS.COMBAT]: 200 } } },
    { id: "wood_gatherer", name: "Wood Gatherer", description: "Chop 32 logs.", type: "break", target: "log", amount: 32, reward: { coins: 40, xp: { [SKILLS.WOODCUTTING]: 80 } } },
    { id: "fish_catcher", name: "Fisherman's Joy", description: "Catch 5 fish.", type: "fish", amount: 5, reward: { coins: 60, xp: { [SKILLS.FISHING]: 120 } } }
];

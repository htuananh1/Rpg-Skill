export const CONFIG = {
    MAX_LEVEL: 100,
    BASE_XP: 100,
    XP_EXPONENT: 1.5,
    MANA_REGEN_INTERVAL: 20, // ticks
    MANA_REGEN_AMOUNT: 5,
    DATA_VERSION: 1,
    SAVE_INTERVAL: 6000, // 5 minutes in ticks (for auto-save if needed, but we save on change usually)
    ANTI_EXPLOIT_COOLDOWN: 500, // ms
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

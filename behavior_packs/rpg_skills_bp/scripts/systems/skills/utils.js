export function isCrop(blockId) {
    return blockId.includes("wheat") ||
           blockId.includes("carrot") ||
           blockId.includes("potato") ||
           blockId.includes("beetroot") ||
           blockId.includes("melon") ||
           blockId.includes("pumpkin");
}

export function isGrowthAuraTarget(blockId) {
    return blockId.includes("wheat") ||
           blockId.includes("carrot") ||
           blockId.includes("potato");
}

export function isMiningBlock(blockId) {
    return blockId.includes("ore") || blockId.includes("deepslate");
}

export function isOre(blockId) {
    return blockId.includes("ore");
}

export function isLog(blockId) {
    return blockId.includes("log") || blockId.includes("wood");
}

import { world } from "@minecraft/server";

const STORAGE_KEY_PREFIX = "rpg_placed_";
const MAX_STORAGE_LIMIT = 32000; // Slightly less than 32767 to be safe

export class BlockTracker {
    static #placedBlocks = new Set();
    static #loaded = false;

    static #load() {
        if (this.#loaded) return;
        const ids = world.getDynamicPropertyIds();
        for (const id of ids) {
            if (id.startsWith(STORAGE_KEY_PREFIX)) {
                const data = world.getDynamicProperty(id);
                if (data && typeof data === "string") {
                    const parts = data.split(";");
                    for (const part of parts) {
                        if (part) this.#placedBlocks.add(part);
                    }
                }
            }
        }
        this.#loaded = true;
    }

    static save() {
        if (!this.#loaded) return;
        const blocks = Array.from(this.#placedBlocks);
        let currentKeyIndex = 0;
        let currentData = "";

        // First, clear existing properties to avoid leftover data
        const ids = world.getDynamicPropertyIds();
        for (const id of ids) {
            if (id.startsWith(STORAGE_KEY_PREFIX)) {
                world.setDynamicProperty(id, undefined);
            }
        }

        for (const block of blocks) {
            // Check if adding this block would exceed the limit
            if ((currentData + block + ";").length > MAX_STORAGE_LIMIT) {
                world.setDynamicProperty(STORAGE_KEY_PREFIX + currentKeyIndex, currentData);
                currentKeyIndex++;
                currentData = "";
            }
            currentData += block + ";";
        }

        if (currentData.length > 0) {
            world.setDynamicProperty(STORAGE_KEY_PREFIX + currentKeyIndex, currentData);
        }
    }

    static getKey(location, dimensionId) {
        // Use floor to ensure we have integer coordinates
        return `${Math.floor(location.x)},${Math.floor(location.y)},${Math.floor(location.z)},${dimensionId}`;
    }

    /**
     * Records a block as being placed by a player.
     * @param {Object} location - The block location {x, y, z}
     * @param {string} dimensionId - The dimension ID
     */
    static addBlock(location, dimensionId) {
        this.#load();
        this.#placedBlocks.add(this.getKey(location, dimensionId));
    }

    /**
     * Checks if a block at a given location was placed by a player and removes it from tracking.
     * @param {Object} location - The block location {x, y, z}
     * @param {string} dimensionId - The dimension ID
     * @returns {boolean} True if the block was player-placed, false otherwise.
     */
    static removeBlock(location, dimensionId) {
        this.#load();
        const key = this.getKey(location, dimensionId);
        if (this.#placedBlocks.has(key)) {
            this.#placedBlocks.delete(key);
            return true;
        }
        return false;
    }

    /**
     * Checks if a block at a given location was placed by a player without removing it.
     * @param {Object} location - The block location {x, y, z}
     * @param {string} dimensionId - The dimension ID
     * @returns {boolean} True if the block was player-placed, false otherwise.
     */
    static isPlayerPlaced(location, dimensionId) {
        this.#load();
        return this.#placedBlocks.has(this.getKey(location, dimensionId));
    }
}

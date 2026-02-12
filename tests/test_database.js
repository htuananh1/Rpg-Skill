import { Database } from './database.js';

// Mock player
class MockPlayer {
    constructor(name) {
        this.name = name;
        this.properties = {};
    }
    getDynamicProperty(key) {
        return this.properties[key];
    }
    setDynamicProperty(key, value) {
        this.properties[key] = value;
    }
}

async function testCorruptionFix() {
    const player = new MockPlayer("TestPlayer");
    const DB_KEY = "rpg_player_data";
    const CORRUPTED_DATA = "{ corrupted: json ";

    // Set corrupted data
    player.setDynamicProperty(DB_KEY, CORRUPTED_DATA);

    console.log("Testing with corrupted data...");
    const data = Database.getPlayerData(player);

    console.log("Returned data version:", data.version);

    // Check if backup exists
    const backup = player.getDynamicProperty(DB_KEY + "_corrupted");
    if (backup === CORRUPTED_DATA) {
        console.log("SUCCESS: Corrupted data was backed up to " + DB_KEY + "_corrupted");
    } else {
        console.error("FAILURE: Corrupted data was NOT backed up!");
        process.exit(1);
    }

    // Check if original data was reset
    const currentData = player.getDynamicProperty(DB_KEY);
    try {
        JSON.parse(currentData);
        console.log("Current data in DB is now valid initialized data.");
    } catch (e) {
        console.error("FAILURE: Current data in DB is still corrupted!");
        process.exit(1);
    }
}

testCorruptionFix();

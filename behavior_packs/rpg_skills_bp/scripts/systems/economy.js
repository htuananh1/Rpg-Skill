import { Database } from "../database.js";

export class Economy {
    static addCoins(player, amount, reason = "") {
        const data = Database.getPlayerData(player);
        data.coins += amount;
        Database.savePlayerData(player, data);

        if (data.settings.notifications) {
            player.onScreenDisplay.setActionBar("§e+" + amount + " Coins " + (reason ? "(" + reason + ")" : "") + "§r");
        }
    }

    static removeCoins(player, amount) {
        const data = Database.getPlayerData(player);
        if (data.coins >= amount) {
            data.coins -= amount;
            Database.savePlayerData(player, data);
            return true;
        }
        return false;
    }
}

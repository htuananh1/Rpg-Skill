import { ActionFormData, MessageFormData } from "@minecraft/server-ui";
import { Database } from "../database.js";
import { Router } from "./router.js";
import { SHOP_ITEMS } from "../config.js";

const debounce = new Map();

export function openShopMenu(player) {
    const data = Database.getPlayerData(player);
    const categories = [...new Set(SHOP_ITEMS.map(item => item.category))];

    const form = new ActionFormData()
        .title({ translate: "ui.shop.title" })
        .body(`Coins: §e${data.coins}§r`);

    for (const cat of categories) {
        form.button(cat);
    }
    form.button({ translate: "ui.back" });

    form.show(player).then((response) => {
        if (response.canceled || response.selection === categories.length) {
            Router.back(player);
            return;
        }
        openCategoryMenu(player, categories[response.selection]);
    });
}

function openCategoryMenu(player, category) {
    const data = Database.getPlayerData(player);
    const items = SHOP_ITEMS.filter(i => i.category === category);

    const form = new ActionFormData()
        .title(category)
        .body(`Coins: §e${data.coins}§r`);

    for (const item of items) {
        let label = `${item.id.split(":")[1].replace("_", " ")}\n§ePrice: ${item.price}§r`;
        if (item.req) {
            const hasSkill = data.skills[item.req.skill].level >= item.req.level;
            label += ` | ${hasSkill ? "§a" : "§c"}${item.req.skill} ${item.req.level}§r`;
        }
        form.button(label);
    }
    form.button({ translate: "ui.back" });

    form.show(player).then((response) => {
        if (response.canceled || response.selection === items.length) {
            openShopMenu(player);
            return;
        }
        confirmPurchase(player, items[response.selection]);
    });
}

function confirmPurchase(player, item) {
    const data = Database.getPlayerData(player);

    if (debounce.get(player.id) > Date.now()) return;

    if (item.req && data.skills[item.req.skill].level < item.req.level) {
        player.sendMessage("§cYou do not meet the skill requirements for this item!");
        return;
    }

    if (data.coins < item.price) {
        player.sendMessage({ translate: "ui.insufficient_funds" });
        return;
    }

    const form = new MessageFormData()
        .title("Confirm Purchase")
        .body(`Are you sure you want to buy ${item.id} for §e${item.price} coins§r?`)
        .button1({ translate: "ui.confirm" })
        .button2({ translate: "ui.back" });

    form.show(player).then((response) => {
        if (response.canceled || response.selection === 1) {
            openShopMenu(player);
            return;
        }

        // Final check and transaction
        if (data.coins >= item.price) {
            debounce.set(player.id, Date.now() + 500);
            data.coins -= item.price;
            Database.savePlayerData(player, data);
            player.runCommandAsync(`give @s ${item.id}`);
            player.sendMessage(`§aPurchased ${item.id}!§r`);
        }
        openShopMenu(player);
    });
}

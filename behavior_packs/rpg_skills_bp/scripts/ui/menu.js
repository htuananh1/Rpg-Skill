import { ActionFormData } from "@minecraft/server-ui";
import { Database } from "../database.js";
import { SKILLS, getXpRequired } from "../config.js";
import { Router } from "./router.js";
import { UIComponents } from "./components.js";
import { openQuestsMenu } from "./quests_ui.js";
import { openShopMenu } from "./shop_ui.js";
import { openSettingsMenu } from "./settings_ui.js";

export function openMainMenu(player) {
    const data = Database.getPlayerData(player);
    Router.clearStack(player);

    const form = new ActionFormData()
        .title({ translate: "ui.main.title" })
        .body("§6Global Level: §l" + data.globalLevel + "§r\n§eCoins: " + data.coins + "§r | §bMana: " + Math.floor(data.mana) + "/" + data.maxMana + "§r")
        .button("Skills", "textures/items/book_enchanted")
        .button("Quests", "textures/items/book_portfolio")
        .button("Shop", "textures/items/emerald")
        .button("Stats", "textures/items/iron_chestplate")
        .button("Settings", "textures/items/compass_item")
        .button("Help", "textures/items/paper");

    form.show(player).then((response) => {
        if (response.canceled) return;
        Router.push(player, openMainMenu);

        switch (response.selection) {
            case 0: openSkillsMenu(player); break;
            case 1: openQuestsMenu(player); break;
            case 2: openShopMenu(player); break;
            case 3: openStatsMenu(player); break;
            case 4: openSettingsMenu(player); break;
            case 5: openHelpMenu(player); break;
        }
    });
}

function openSkillsMenu(player) {
    const data = Database.getPlayerData(player);
    const form = new ActionFormData()
        .title({ translate: "ui.skills.title" });

    const skillList = Object.keys(data.skills);
    for (const skill of skillList) {
        const sData = data.skills[skill];
        const nextXp = getXpRequired(sData.level);
        form.button("§0" + skill.toUpperCase() + "§r\nLvl: " + sData.level + " " + UIComponents.progressBar(sData.xp, nextXp, 5));
    }

    form.button({ translate: "ui.back" });

    form.show(player).then((response) => {
        if (response.canceled || response.selection === skillList.length) {
            Router.back(player);
            return;
        }
        openSkillDetails(player, skillList[response.selection]);
    });
}

function openSkillDetails(player, skill) {
    const data = Database.getPlayerData(player);
    const sData = data.skills[skill];
    const nextXp = getXpRequired(sData.level);

    const form = new ActionFormData()
        .title(skill.toUpperCase())
        .body("Level: " + sData.level + "\nXP: " + Math.floor(sData.xp) + "/" + nextXp + "\n\nPerks unlocked at various levels...")
        .button({ translate: "ui.back" });

    form.show(player).then(() => {
        openSkillsMenu(player);
    });
}

function openStatsMenu(player) {
    const data = Database.getPlayerData(player);
    const stats = data.stats;
    const body = "§lDerived Stats§r\n" +
        "Bonus Damage: §a+" + stats.bonusDamage + "%§r\n" +
        "Bonus Defense: §a+" + stats.bonusDefense + "%§r\n" +
        "Crit Chance: §a" + stats.critChance + "%§r\n" +
        "Lifesteal: §a" + stats.lifesteal + "%§r\n" +
        "Mining Fortune: §a" + stats.miningFortune + "§r\n\n" +
        "§7Breakdown: Base + Skill bonuses§r";

    const form = new ActionFormData()
        .title({ translate: "ui.stats.title" })
        .body(body)
        .button({ translate: "ui.back" });

    form.show(player).then(() => {
        Router.back(player);
    });
}

function openHelpMenu(player) {
    const form = new ActionFormData()
        .title({ translate: "ui.help.title" })
        .body("Welcome to the RPG Skills system!\n\n" +
            "1. Use the §lSkill Tome§r to manage skills.\n" +
            "2. Use the §lCompass§r to access this menu.\n" +
            "3. Complete §lQuests§r to earn Coins and XP.\n" +
            "4. Spend Coins in the §lShop§r.\n\n" +
            "Sneak + Use Tome to cast your last ability!")
        .button({ translate: "ui.back" });

    form.show(player).then(() => {
        Router.back(player);
    });
}

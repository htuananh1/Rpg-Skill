import { ActionFormData, MessageFormData } from "@minecraft/server-ui";
import { Database } from "../database.js";
import { SKILLS, getXpRequired } from "../config.js";

export function openMainMenu(player) {
    const data = Database.getPlayerData(player);
    const form = new ActionFormData()
        .title({ translate: "rpg.menu.title" })
        .body(`§6Global Level: §l${data.globalLevel}§r\n§bMana: §l${Math.floor(data.mana)}/${data.maxMana}§r`)
        .button({ translate: "rpg.menu.skills" }, "textures/items/book_enchanted")
        .button({ translate: "rpg.menu.perks" }, "textures/items/nether_star")
        .button({ translate: "rpg.menu.settings" }, "textures/items/compass_item");

    form.show(player).then((response) => {
        if (response.canceled) return;
        if (response.selection === 0) openSkillsMenu(player);
        if (response.selection === 1) openPerksMenu(player);
        if (response.selection === 2) openSettingsMenu(player);
    });
}

function openSkillsMenu(player) {
    const data = Database.getPlayerData(player);
    const form = new ActionFormData()
        .title({ translate: "rpg.menu.skills" });

    const skillList = Object.keys(data.skills);
    for (const skill of skillList) {
        const sData = data.skills[skill];
        const nextXp = getXpRequired(sData.level);
        form.button(`§0${skill.toUpperCase()}§r\nLevel: ${sData.level} (${Math.floor(sData.xp)}/${nextXp})`);
    }

    form.button({ translate: "rpg.menu.close" });

    form.show(player).then((response) => {
        if (response.canceled || response.selection === skillList.length) {
            openMainMenu(player);
            return;
        }
        openMainMenu(player);
    });
}

function openPerksMenu(player) {
    const data = Database.getPlayerData(player);
    const form = new ActionFormData()
        .title({ translate: "rpg.menu.perks" })
        .body("Perks unlock automatically at certain skill levels.");

    form.button("Mining: Vein Sense (Lvl 20)");
    form.button("Woodcutting: Tree Feller (Lvl 15)");
    form.button("Farming: Growth Aura (Lvl 25)");
    form.button("Combat: Lifesteal (Lvl 40)");
    form.button("Agility: Fall Reduction (Lvl 20)");
    form.button("Defense: Thorns (Lvl 30)");
    form.button({ translate: "rpg.menu.close" });

    form.show(player).then(() => {
        openMainMenu(player);
    });
}

function openSettingsMenu(player) {
    const data = Database.getPlayerData(player);
    const form = new MessageFormData()
        .title({ translate: "rpg.menu.settings" })
        .body("Customize your RPG experience.")
        .button1(`Notifications: ${data.settings.notifications ? "ON" : "OFF"}`)
        .button2(`Particles: ${data.settings.particles ? "ON" : "OFF"}`);

    form.show(player).then((response) => {
        if (response.canceled) {
            openMainMenu(player);
            return;
        }
        if (response.selection === 1) data.settings.notifications = !data.settings.notifications;
        if (response.selection === 0) data.settings.particles = !data.settings.particles;

        Database.savePlayerData(player, data);
        openSettingsMenu(player);
    });
}

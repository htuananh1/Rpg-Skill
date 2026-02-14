import { ActionFormData, ModalFormData } from "@minecraft/server-ui";
import { Database } from "../database.js";
import { Router } from "./router.js";
import { world } from "@minecraft/server";
import { CONFIG, SKILLS } from "../config.js";
import { XpSystem } from "../systems/xp.js";

export function openAdminMenu(player) {
    if (!player.hasTag(CONFIG.ADMIN_TAG)) {
        player.sendMessage({ translate: "ui.no_permission" });
        return;
    }

    const form = new ActionFormData()
        .title({ translate: "ui.admin.title" })
        .button("Player Management")
        .button("Global Settings")
        .button({ translate: "ui.close" });

    form.show(player).then((response) => {
        if (response.canceled || response.selection === 2) return;
        if (response.selection === 0) openPlayerList(player);
        if (response.selection === 1) openGlobalSettings(player);
    });
}

function openPlayerList(player) {
    const players = world.getAllPlayers();
    const form = new ActionFormData()
        .title("Select Player");

    for (const p of players) {
        form.button(p.name);
    }
    form.button({ translate: "ui.back" });

    form.show(player).then((response) => {
        if (response.canceled || response.selection === players.length) {
            openAdminMenu(player);
            return;
        }
        openPlayerActions(player, players[response.selection]);
    });
}

function openPlayerActions(admin, target) {
    const form = new ActionFormData()
        .title(`Managing ${target.name}`)
        .button("Give Coins")
        .button("Give Skill XP")
        .button("Reset Profile")
        .button({ translate: "ui.back" });

    form.show(admin).then((response) => {
        if (response.canceled || response.selection === 3) {
            openPlayerList(admin);
            return;
        }
        if (response.selection === 0) {
            const modal = new ModalFormData()
                .title("Give Coins")
                .textField("Amount", "100");
            modal.show(admin).then((res) => {
                if (res.canceled) return;
                const amt = parseInt(res.formValues[0]);
                if (!isNaN(amt)) {
                    const data = Database.getPlayerData(target);
                    data.coins += amt;
                    Database.savePlayerData(target, data);
                    admin.sendMessage(`Gave ${amt} coins to ${target.name}`);
                }
            });
        } else if (response.selection === 1) {
            const skillList = Object.values(SKILLS);
            const modal = new ModalFormData()
                .title("Give Skill XP")
                .dropdown("Skill", skillList)
                .textField("Amount", "1000");
            modal.show(admin).then((res) => {
                if (res.canceled) return;
                const skill = skillList[res.formValues[0]];
                const amt = parseInt(res.formValues[1]);
                XpSystem.addXp(target, skill, amt);
                admin.sendMessage(`Gave ${amt} XP in ${skill} to ${target.name}`);
            });
        } else if (response.selection === 2) {
            Database.initializePlayer(target);
            admin.sendMessage(`Reset profile for ${target.name}`);
        }
    });
}

function openGlobalSettings(player) {
    const isDebug = world.getDynamicProperty("rpg_debug_mode") || false;
    const form = new ActionFormData()
        .title("Global Settings")
        .button(`Debug Mode: ${isDebug ? "§aON" : "§cOFF"}`)
        .button({ translate: "ui.back" });

    form.show(player).then((response) => {
        if (response.canceled || response.selection === 1) {
             openAdminMenu(player);
             return;
        }
        if (response.selection === 0) {
            world.setDynamicProperty("rpg_debug_mode", !isDebug);
            player.sendMessage(`Debug Mode set to: ${!isDebug}`);
            openGlobalSettings(player);
        }
    });
}

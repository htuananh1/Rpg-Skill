import { MessageFormData } from "@minecraft/server-ui";
import { Database } from "../database.js";
import { Router } from "./router.js";

export function openSettingsMenu(player) {
    const data = Database.getPlayerData(player);
    const form = new MessageFormData()
        .title({ translate: "ui.settings.title" })
        .body("Customize your RPG experience.\n\nNotifications: " + (data.settings.notifications ? "§aON" : "§cOFF") + "\nParticles: " + (data.settings.particles ? "§aON" : "§cOFF") + "\nSounds: " + (data.settings.sounds ? "§aON" : "§cOFF") + "\nXP Popups: " + (data.settings.xpPopups ? "§aON" : "§cOFF"))
        .button1("Toggle Particles/Sounds")
        .button2("Toggle Notifs/Popups");

    form.show(player).then((response) => {
        if (response.canceled) {
            Router.back(player);
            return;
        }
        if (response.selection === 0) {
            data.settings.particles = !data.settings.particles;
            data.settings.sounds = !data.settings.sounds;
        } else {
            data.settings.notifications = !data.settings.notifications;
            data.settings.xpPopups = !data.settings.xpPopups;
        }

        Database.savePlayerData(player, data);
        openSettingsMenu(player);
    });
}

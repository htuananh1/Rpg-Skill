import { ActionFormData } from "@minecraft/server-ui";
import { Database } from "../database.js";
import { Router } from "./router.js";
import { UIComponents } from "./components.js";
import { XpSystem } from "../systems/xp.js";

export function openQuestsMenu(player) {
    const data = Database.getPlayerData(player);
    const form = new ActionFormData()
        .title({ translate: "ui.quests.title" })
        .body("Active Quests: " + data.quests.active.length + "\nDaily Quests: " + data.quests.daily.length)
        .button("Active Quests")
        .button("Daily Quests")
        .button({ translate: "ui.back" });

    form.show(player).then((response) => {
        if (response.canceled || response.selection === 2) {
            Router.back(player);
            return;
        }
        if (response.selection === 0) openActiveQuests(player);
        if (response.selection === 1) openDailyQuests(player);
    });
}

function openActiveQuests(player) {
    const data = Database.getPlayerData(player);
    const form = new ActionFormData()
        .title("Active Quests");

    if (data.quests.active.length === 0) {
        form.body("No active quests.");
    }

    for (const q of data.quests.active) {
        form.button(q.name + "\n" + UIComponents.progressBar(q.progress, q.amount) + " " + q.progress + "/" + q.amount);
    }
    form.button({ translate: "ui.back" });

    form.show(player).then((response) => {
        if (response.canceled || response.selection === data.quests.active.length) {
            openQuestsMenu(player);
            return;
        }
        const quest = data.quests.active[response.selection];
        if (quest.progress >= quest.amount) {
            claimQuestReward(player, quest);
        } else {
            player.sendMessage("Quest: " + quest.name + "\n" + quest.description + "\nProgress: " + quest.progress + "/" + quest.amount);
            openActiveQuests(player);
        }
    });
}

function openDailyQuests(player) {
    const data = Database.getPlayerData(player);
    const form = new ActionFormData()
        .title("Daily Quests");

    for (const q of data.quests.daily) {
        const isActive = data.quests.active.some(aq => aq.id === q.id);
        const isCompleted = data.quests.completed.includes(q.id);
        form.button(q.name + (isCompleted ? " §a(Completed)" : isActive ? " §e(Active)" : ""));
    }
    form.button({ translate: "ui.back" });

    form.show(player).then((response) => {
        if (response.canceled || response.selection === data.quests.daily.length) {
            openQuestsMenu(player);
            return;
        }
        const quest = data.quests.daily[response.selection];
        if (data.quests.completed.includes(quest.id)) {
            player.sendMessage("You already completed this daily quest!");
            openDailyQuests(player);
        } else if (data.quests.active.some(aq => aq.id === quest.id)) {
            openActiveQuests(player);
        } else {
            data.quests.active.push({ ...quest, progress: 0 });
            Database.savePlayerData(player, data);
            player.sendMessage("§aAccepted quest: " + quest.name + "§r");
            openDailyQuests(player);
        }
    });
}

function claimQuestReward(player, quest) {
    const data = Database.getPlayerData(player);
    data.quests.active = data.quests.active.filter(aq => aq.id !== quest.id);
    data.quests.completed.push(quest.id);

    data.coins += quest.reward.coins || 0;
    if (quest.reward.xp) {
        for (const [skill, amt] of Object.entries(quest.reward.xp)) {
            XpSystem.addXp(player, skill, amt);
        }
    }

    Database.savePlayerData(player, data);
    player.sendMessage("§aCompleted " + quest.name + "! Claimed rewards.§r");
    openQuestsMenu(player);
}

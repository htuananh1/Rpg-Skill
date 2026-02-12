const backStack = new Map();

export class Router {
    static open(player, menuFunc, ...args) {
        if (!backStack.has(player.id)) {
            backStack.set(player.id, []);
        }

        // Don't add to backstack if it's the same menu or we're going back
        // For simplicity, we'll let the menu functions handle whether to push to stack
        menuFunc(player, ...args);
    }

    static push(player, menuFunc, ...args) {
        if (!backStack.has(player.id)) {
            backStack.set(player.id, []);
        }
        backStack.get(player.id).push({ func: menuFunc, args });
    }

    static back(player) {
        const stack = backStack.get(player.id);
        if (stack && stack.length > 0) {
            const previous = stack.pop();
            previous.func(player, ...previous.args);
        }
    }

    static clearStack(player) {
        backStack.set(player.id, []);
    }
}

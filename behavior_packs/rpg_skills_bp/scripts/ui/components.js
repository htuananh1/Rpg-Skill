export class UIComponents {
    static progressBar(current, max, length = 10) {
        const percentage = Math.min(Math.max(current / max, 0), 1);
        const filledLength = Math.round(length * percentage);
        const emptyLength = length - filledLength;

        return "§a" + "■".repeat(filledLength) + "§7" + "■".repeat(emptyLength) + "§r";
    }

    static paginate(items, page, pageSize) {
        const start = page * pageSize;
        const end = start + pageSize;
        return {
            items: items.slice(start, end),
            totalEntry: items.length,
            totalPages: Math.ceil(items.length / pageSize),
            currentPage: page
        };
    }
}

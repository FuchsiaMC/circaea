package net.fuchsiamc.circaea.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Simple class for returning command statuses from outside a command.
 */
// todo: move to gaura
public record Response(boolean successful, String message) {
    public String message() {
        return (successful ? ChatColor.GREEN : ChatColor.RED) + message;
    }

    public boolean executeAsCommand(CommandSender sender) {
        sender.sendMessage(message());
        return successful();
    }
}

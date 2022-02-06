package net.fuchsiamc.circaea.commands.debug;

import net.fuchsiamc.circaea.framework.commands.IDebugCommandExecutor;
import net.fuchsiamc.circaea.util.PlayerName;
import net.fuchsiamc.circaea.util.PlayerNameResolver;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class PlayerNameTestCommand implements IDebugCommandExecutor {
    @Override
    public String getDebugCommand() {
        return "playername";
    }

    @Override
    public boolean onCommand(@Nullable CommandSender sender, @Nullable Command command, @Nullable String label, @Nullable String[] args) {
        if (sender == null)
            return false;

        if (args == null || args.length != 1)
            return false;

        sender.sendMessage(ChatColor.RED + "Input: " + args[0]);
        PlayerName name = PlayerNameResolver.resolve(args[0]);
        sender.sendMessage(ChatColor.RED + "PlayerName type: " + name.nameType());
        OfflinePlayer plr = name.getPlayer();
        sender.sendMessage(ChatColor.RED + "Player resolved: " + (plr == null ? "null" : plr.getName()));

        return true;
    }
}

package net.fuchsiamc.circaea.commands;

import net.fuchsiamc.circaea.framework.commands.IFuchsiaCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ViewPermissionsCommand implements IFuchsiaCommand {
    @Override
    public String getCommand() {
        return "viewperms";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}

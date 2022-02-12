package net.fuchsiamc.circaea.commands;

import net.fuchsiamc.circaea.Circaea;
import net.fuchsiamc.gaura.commands.IFuchsiaCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;

@SuppressWarnings("ClassCanBeRecord")
public class RefreshPermissionsCommand implements IFuchsiaCommand {
    private final Circaea circaea;

    public RefreshPermissionsCommand(Circaea circaea) {
        this.circaea = circaea;
    }

    @Override
    public String getCommand() {
        return "refreshperms";
    }

    @Override
    public boolean onCommand(@Nullable CommandSender sender, @Nullable Command command, @Nullable String label, @Nullable String[] args) {
        if (args != null && args.length > 0)
            return false;

        circaea.refreshPermissions();

        return true;
    }
}

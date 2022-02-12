package net.fuchsiamc.circaea.commands;

import net.fuchsiamc.circaea.Circaea;
import net.fuchsiamc.circaea.permissions.PermissionRank;
import net.fuchsiamc.circaea.util.CommandResponse;
import net.fuchsiamc.gaura.commands.IFuchsiaCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.ArrayList;

@SuppressWarnings("ClassCanBeRecord")
public class RegisterRankCommand implements IFuchsiaCommand {
    private final Circaea circaea;

    public RegisterRankCommand(Circaea circaea) {
        this.circaea = circaea;
    }

    @Override
    public String getCommand() {
        return "registerrank";
    }

    @Override
    public boolean onCommand(@Nullable CommandSender sender, @Nullable Command command, @Nullable String label, @Nullable String[] args) {
        if (args == null || args.length != 2) {
            if (sender != null) {
                sender.sendMessage("Please specify a name and priority.");
            }

            return false;
        }

        int priority;

        try {
            priority = Integer.parseInt(args[1]);
        } catch (NumberFormatException ignored) {
            if (sender != null) {
                sender.sendMessage("Could not parse second argument (priority) into a readable integer.");
            }

            return false;
        }

        CommandResponse response = circaea.registerRank(new PermissionRank(new ArrayList<>(), args[0], priority, null));

        if (sender != null) {
            sender.sendMessage(response.message());
        }

        return response.successful();
    }
}

package net.fuchsiamc.circaea.framework.registrars;

import net.fuchsiamc.circaea.framework.FuchsiaPlugin;
import net.fuchsiamc.circaea.framework.commands.IFuchsiaCommand;
import org.bukkit.command.CommandExecutor;

import javax.annotation.Nullable;
import java.util.Objects;

public class CommandExecutorRegistrar extends Registrar<CommandExecutor> {
    public CommandExecutorRegistrar(FuchsiaPlugin plugin) {
        super(plugin);
    }

    public CommandExecutor register(CommandExecutor item, String command) {
        Objects.requireNonNull(plugin.getCommand(command)).setExecutor(item);

        return item;
    }

    @Override
    @Nullable
    public CommandExecutor register(CommandExecutor item) {
        if (item instanceof IFuchsiaCommand fuchsiaCommand && fuchsiaCommand.canBeRegistered()) {
            return register(item, fuchsiaCommand.getCommand());
        }

        return null;
    }
}

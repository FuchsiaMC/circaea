package net.fuchsiamc.circaea.framework.commands;

import org.bukkit.command.CommandExecutor;

/**
 * An extended CommandExecutor designed to work in tandem with FuchsiaPlugin.
 */
public interface IFuchsiaCommand extends CommandExecutor {
    /**
     * Gets the command's name.
     * @return The command name used for registering commands.
     */
    String getCommand();

    /**
     * Whether this command can be registered.
     * @return Whether this command can be registered.
     */
    default boolean canBeRegistered() {
        return true;
    }
}

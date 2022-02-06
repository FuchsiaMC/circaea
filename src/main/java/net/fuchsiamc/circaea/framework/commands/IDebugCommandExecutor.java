package net.fuchsiamc.circaea.framework.commands;

import net.fuchsiamc.circaea.framework.util.Debugger;

/**
 * Indicates a command is for debugging only and should not work in a non-debug environment.
 */
public interface IDebugCommandExecutor extends IFuchsiaCommand {
    @Override
    default String getCommand() {
        return "?" + getDebugCommand();
    }

    @Override
    default boolean canBeRegistered() {
        // Check if the JDWP is active. There can be false-positives, so we insert other specialties later.
        return Debugger.isAttached();
    }

    /**
     * Gets the debug command's name.
     * @return The command name used for registering commands. "?" is prepended in-game.
     */
    String getDebugCommand();
}

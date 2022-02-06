package net.fuchsiamc.circaea;

import net.fuchsiamc.circaea.commands.debug.PlayerNameTestCommand;
import net.fuchsiamc.circaea.eventhandlers.PlayerEventHandler;
import net.fuchsiamc.circaea.framework.FuchsiaPlugin;
import net.fuchsiamc.circaea.framework.commands.IFuchsiaCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Circaea extends FuchsiaPlugin {
    /**
     * HashMap containing the permissions of all players that have joined the server since it has started.
     */
    public final HashMap<UUID, PermissionAttachment> permissions = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();

        // Ensure config exists.
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new PlayerEventHandler(this), this);
    }

    @Override
    public List<IFuchsiaCommand> getCommands() {
        List<IFuchsiaCommand> list = new ArrayList<>();

        list.add(new PlayerNameTestCommand());

        return list;
    }

    public void refreshPermissions() {
        permissions.clear();

        for (Player player : getServer().getOnlinePlayers()) {
            if (player != null) {
                addPlayerPermissions(player);
            }
        }
    }

    public void addPlayerPermissions(OfflinePlayer player) {

    }
}

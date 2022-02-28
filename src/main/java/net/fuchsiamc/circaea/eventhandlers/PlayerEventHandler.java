package net.fuchsiamc.circaea.eventhandlers;

import net.fuchsiamc.circaea.Circaea;
import net.fuchsiamc.circaea.managers.PlayerManager;
import net.fuchsiamc.circaea.permissions.PermissionRank;
import net.fuchsiamc.circaea.permissions.PermittedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventHandler implements Listener {
    private final Circaea circaea;

    // cache this, so we don't look it up every time
    private final PlayerManager manager;
    private final PermissionRank defaultRank;

    public PlayerEventHandler(Circaea circaea) {
        this.circaea = circaea;

        manager = circaea.getPlayerManager();
        defaultRank = circaea.getRankManager().getDefaultRank();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // return a permitted player if found, if not, create one and return that
        PermittedPlayer permittedPlayer =
                manager.registerPlayer(new PermittedPlayer(player.getUniqueId(), defaultRank));

        // set up the permission attachment
        manager.setupAttachment(player);

        // set the player's perms
        manager.setupPermissions(permittedPlayer, circaea.getGroupManager());

        // refresh commands
        player.updateCommands();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // set all registered perms to false
        manager.removePermissions(manager.getPlayer(player.getUniqueId()), circaea.getGroupManager());

        // remove the permission attachment
        manager.removeAttachment(player);
    }
}

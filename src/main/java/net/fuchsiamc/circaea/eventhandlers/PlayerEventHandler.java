package net.fuchsiamc.circaea.eventhandlers;

import net.fuchsiamc.circaea.Circaea;
import net.fuchsiamc.circaea.managers.GroupManager;
import net.fuchsiamc.circaea.managers.PlayerManager;
import net.fuchsiamc.circaea.permissions.PermittedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventHandler implements Listener {
    private final PlayerManager manager;
    private final GroupManager groupManager;

    public PlayerEventHandler(Circaea circaea) {
        manager = circaea.getPlayerManager();
        groupManager = circaea.getGroupManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // if the permitted player doesn't exist, we want to register it
        if (!manager.playerExists(player)) {
            manager.registerPlayer(player);
        }

        // set up the permission attachment
        manager.setupAttachment(player);

        // get the permitted player with our attachment
        PermittedPlayer permittedPlayer = manager.getPermittedPlayer(player);

        // set the player's perms
        manager.setPerms(permittedPlayer, groupManager);
        // refresh commands
        player.updateCommands();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        //manager.removeAttachment(player);
        manager.removePerms(manager.getPermittedPlayer(player.getUniqueId()), groupManager);
    }
}

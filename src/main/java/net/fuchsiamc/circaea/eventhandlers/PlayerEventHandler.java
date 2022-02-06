package net.fuchsiamc.circaea.eventhandlers;

import net.fuchsiamc.circaea.Circaea;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("ClassCanBeRecord")
public class PlayerEventHandler implements Listener {
    private final Circaea circaea;

    public PlayerEventHandler(Circaea circaea) {
        this.circaea = circaea;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        circaea.addPlayerPermissions(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().removeAttachment(circaea.permissions.get(event.getPlayer().getUniqueId()));
    }
}

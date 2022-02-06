package net.fuchsiamc.circaea.permissions;

import net.fuchsiamc.circaea.Circaea;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Object that stores the rank and permission groups of a player.
 */
public class PermittedPlayer {
    public final Circaea circaea;
    public final PermissionRank rank;
    public final List<PermissionGroup> groups;

    public PermittedPlayer(Circaea circaea, @Nullable PermissionRank rank, List<PermissionGroup> groups) {
        this.circaea = circaea;

        if (rank == null) {
            this.rank = circaea.getDefaultRank();
        } else {
            this.rank = rank;
        }

        this.groups = groups;
    }

    public void serialize() {

    }
}

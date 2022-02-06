package net.fuchsiamc.circaea.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * An object representing a player's name, created in PlayerNameResolver.
 */
public record PlayerName(NameType nameType, String name) {

    /**
     * Retrieves the player associated with this object.
     *
     * @return The player based on their name, which may be null if a name/UUID doesn't correlate with a player, a player is offline, or a name could not be validated.
     */
    @Nullable
    public OfflinePlayer getPlayer() {
        return nameType.resolvePlayer(name);
    }

    public enum NameType {
        UUID(name -> Bukkit.getOfflinePlayer(java.util.UUID.fromString(name))),
        RAW(Bukkit::getPlayer),
        INVALID(name -> null);

        private final IPlayerResolver playerResolver;

        NameType(IPlayerResolver playerResolver) {
            this.playerResolver = playerResolver;
        }

        /**
         * Retrieves the player associated with this object.
         *
         * @return The player based on their name, which may be null if a name/UUID doesn't correlate with a player, a player is offline, or a name could not be validated.
         */
        @Nullable
        public OfflinePlayer resolvePlayer(String name) {
            return playerResolver.resolvePlayer(name);
        }

        public interface IPlayerResolver {
            @Nullable
            OfflinePlayer resolvePlayer(String name);
        }
    }
}

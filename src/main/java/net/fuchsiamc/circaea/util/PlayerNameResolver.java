package net.fuchsiamc.circaea.util;

import java.util.regex.Pattern;

/**
 * Arbitrarily resolves a player from a string, given a name or UUID.
 */
public final class PlayerNameResolver {
    public static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$"
    );

    public static final int USERNAME_LENGTH_LIMIT = 16;

    /**
     * Resolve a player's name from either a UUID or raw name input.
     * @param str The player's name.
     * @return A PlayerName object capable of resolving a Player object.
     */
    public static PlayerName resolve(String str) {
        if (UUID_PATTERN.matcher(str).matches())
            return new PlayerName(PlayerName.NameType.UUID, str);

        if (str.length() > 0 && str.length() <= USERNAME_LENGTH_LIMIT)
            return new PlayerName(PlayerName.NameType.RAW, str);

        return new PlayerName(PlayerName.NameType.INVALID, "");
    }
}

package net.fuchsiamc.circaea.util;

/**
 * Simple class for returning command statuses from outside a command.
 */
public record CommandResponse(boolean successful, String message) {
}

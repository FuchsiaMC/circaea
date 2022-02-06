package net.fuchsiamc.circaea.permissions;

import java.util.List;

public record PermissionGroup(List<String> addedPermissions,
                              List<String> removedPermissions,
                              String name) {
}

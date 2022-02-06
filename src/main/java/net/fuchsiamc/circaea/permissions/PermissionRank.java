package net.fuchsiamc.circaea.permissions;

import java.util.List;

public final class PermissionRank {
    public final List<PermissionGroup> permissionGroups;
    public final String name;
    public final int priority;
    public PermissionRank childRank;

    public PermissionRank(List<PermissionGroup> permissionGroups, String name, int priority, PermissionRank childRank) {
        this.permissionGroups = permissionGroups;
        this.name = name;
        this.priority = priority;
        this.childRank = childRank;
    }

    public void serializeToConfig() {
    }
}

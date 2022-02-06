package net.fuchsiamc.circaea.permissions;

import javax.annotation.Nullable;
import java.util.List;

public record PermissionRank(List<PermissionGroup> permissionGroups,
                             String name,
                             int priority,
                             @Nullable net.fuchsiamc.circaea.permissions.PermissionRank childRank) {
    public PermissionRank(List<PermissionGroup> permissionGroups, String name, int priority, @Nullable PermissionRank childRank) {
        this.permissionGroups = permissionGroups;
        this.name = name;
        this.priority = priority;
        this.childRank = childRank;
    }
}

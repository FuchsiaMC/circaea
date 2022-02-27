package net.fuchsiamc.circaea.permissions;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;

public final class PermissionGroup {
    /**
     * MongoDB automatically generated id.
     */
    private ObjectId id;

    @Getter
    @Setter
    private String name;

    @BsonProperty(value = "added_permissions")
    @Getter
    @Setter
    private List<String> addedPermissions;

    @BsonProperty(value = "removed_permissions")
    @Getter
    @Setter
    private List<String> removedPermissions;

    public PermissionGroup(String name, List<String> addedPermissions,
                           List<String> removedPermissions) {
        this.addedPermissions = addedPermissions;
        this.removedPermissions = removedPermissions;
        this.name = name;
    }

    /**
     * Empty constructor needed for MongoDB to parse database information.
     */
    public PermissionGroup() {
    }

    @Override
    public String toString() {
        return "PermissionGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", addedPermissions=" + addedPermissions +
                ", removedPermissions=" + removedPermissions +
                '}';
    }
}

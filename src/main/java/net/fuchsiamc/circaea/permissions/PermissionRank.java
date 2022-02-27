package net.fuchsiamc.circaea.permissions;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import java.util.List;

public class PermissionRank {
    /**
     * MongoDB automatically generated id.
     */
    private ObjectId id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int priority;

    @BsonProperty(value = "permission_groups")
    @Getter
    @Setter
    private List<String> permissionGroups;

    @BsonProperty(value = "child_rank")
    @Getter
    @Setter
    private String childRank;

    public PermissionRank(List<String> permissionGroups,
                          String name,
                          int priority,
                          @Nullable String childRankName) {
        this.permissionGroups = permissionGroups;
        this.name = name;
        this.priority = priority;
        this.childRank = childRankName;
    }

    /**
     * Empty constructor needed for MongoDB to parse database information.
     */
    public PermissionRank() {
    }

    @Override
    public String toString() {
        return "PermissionRank{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", permissionGroups=" + permissionGroups +
                ", childRank=" + childRank +
                '}';
    }
}

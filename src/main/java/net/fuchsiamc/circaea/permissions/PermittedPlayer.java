package net.fuchsiamc.circaea.permissions;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.bukkit.permissions.PermissionAttachment;

import java.util.List;
import java.util.UUID;

/**
 * Object that stores the rank and permission groups of a player.
 */
public class PermittedPlayer {
    /**
     * MongoDB automatically generated id.
     */
    private ObjectId id;

    @BsonProperty(value = "uuid")
    @Getter
    @Setter
    private UUID playerUuid;

    @Getter
    @Setter
    private PermissionRank rank;

    @BsonProperty(value = "permission_groups")
    @Getter
    @Setter
    private List<String> permissionGroups;

    @BsonIgnore
    @Getter
    @Setter
    private PermissionAttachment attachment;

    public PermittedPlayer(UUID playerUuid, PermissionRank rank, List<String> groups) {
        this.playerUuid = playerUuid;
        this.rank = rank;
        this.permissionGroups = groups;
    }

    /**
     * Empty constructor needed for MongoDB to parse database information.
     */
    public PermittedPlayer() {
    }

    @Override
    public String toString() {
        return "PermittedPlayer{" +
                "id=" + id +
                ", playerUuid=" + playerUuid +
                ", rank=" + rank +
                ", permissionGroups=" + permissionGroups +
                ", attachment=" + attachment +
                '}';
    }
}

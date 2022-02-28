package net.fuchsiamc.circaea.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import net.fuchsiamc.circaea.Circaea;
import net.fuchsiamc.circaea.permissions.PermissionGroup;
import net.fuchsiamc.circaea.permissions.PermittedPlayer;
import net.fuchsiamc.circaea.util.Response;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private final Circaea circaea;

    /**
     * The mongo database collection for players.
     */
    private MongoCollection<PermittedPlayer> playersCollection;

    private final Map<UUID, PermissionAttachment> attachedPlayers = new HashMap<>();

    public PlayerManager(Circaea circaea) {
        this.circaea = circaea;
    }

    public void initialize(MongoDatabase db) {
        // initialize the collection
        playersCollection = db.getCollection("players", PermittedPlayer.class);
    }

    public PermittedPlayer registerPlayer(PermittedPlayer player) {
        // check if a player with the same uuid already exists because we don't want duplicates
        PermittedPlayer existingPlayer = getPlayer(player.getPlayerUuid());

        if (existingPlayer != null) {
            return existingPlayer;
        }

        // add it to the database
        playersCollection.insertOne(player);
        return player;
    }

    public Response updatePlayer(PermittedPlayer player) {
        // replace the player in the database
        if (playersCollection.findOneAndReplace(new Document("uuid", player.getPlayerUuid()), player) == null) {
            return new Response(false, "This permitted player does not exist!");
        }

        // refresh commands
        Player onlinePlayer = Bukkit.getPlayer(player.getPlayerUuid());
        if (onlinePlayer != null) {
            onlinePlayer.updateCommands();
        }

        return new Response(true, "Successfully updated permitted player!");
    }

    public PermittedPlayer getPlayer(UUID playerUuid) {
        return playersCollection.find(Filters.eq("uuid", playerUuid)).first();
    }

    public boolean playerExists(UUID playerUuid) {
        return getPlayer(playerUuid) != null;
    }

    public void setupAttachment(Player player) {
        attachedPlayers.put(player.getUniqueId(), player.addAttachment(circaea));
    }

    public void removeAttachment(Player player) {
        attachedPlayers.remove(player.getUniqueId());
    }

    public void setupPermissions(PermittedPlayer player, GroupManager manager) {
        PermissionAttachment attachment = attachedPlayers.get(player.getPlayerUuid());

        for (String groupName : player.getRank().getPermissionGroups()) {
            PermissionGroup group = manager.getGroup(groupName);

            for (String perm : group.getAddedPermissions()) {
                attachment.setPermission(perm, true);
            }

            for (String perm : group.getRemovedPermissions()) {
                attachment.setPermission(perm, false);
            }
        }

        for (String groupName : player.getPermissionGroups()) {
            PermissionGroup group = manager.getGroup(groupName);

            for (String perm : group.getAddedPermissions()) {
                attachment.setPermission(perm, true);
            }

            for (String perm : group.getRemovedPermissions()) {
                attachment.setPermission(perm, false);
            }
        }
    }

    public void removePermissions(PermittedPlayer player, GroupManager manager) {
        PermissionAttachment attachment = attachedPlayers.get(player.getPlayerUuid());

        for (String groupName : player.getRank().getPermissionGroups()) {
            PermissionGroup group = manager.getGroup(groupName);

            for (String perm : group.getAddedPermissions()) {
                attachment.setPermission(perm, false);
            }
        }

        for (String groupName : player.getPermissionGroups()) {
            PermissionGroup group = manager.getGroup(groupName);

            for (String perm : group.getAddedPermissions()) {
                attachment.setPermission(perm, false);
            }
        }
    }

    public void refreshAllPermissions() {
        Bukkit.getScheduler().runTaskAsynchronously(circaea, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                // refresh commands
                player.updateCommands();
            }
        });
    }
}

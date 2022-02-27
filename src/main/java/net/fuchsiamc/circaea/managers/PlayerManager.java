package net.fuchsiamc.circaea.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.fuchsiamc.circaea.Circaea;
import net.fuchsiamc.circaea.permissions.PermissionGroup;
import net.fuchsiamc.circaea.permissions.PermittedPlayer;
import net.fuchsiamc.circaea.util.CommandResponse;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {
    private final Circaea circaea;

    /**
     * A list containing the player data of every player that has ever joined the server.
     */
    @Getter
    private final List<PermittedPlayer> players = new ArrayList<>();

    /**
     * The mongo database collection for players.
     */
    private MongoCollection<PermittedPlayer> playersCollection;

    public PlayerManager(Circaea circaea) {
        this.circaea = circaea;
    }

    /**
     * Intialize the player database collection and add all players to this local Circaea instance.
     *
     * @param db The MongoDatabase that the players collection can be found in.
     */
    public void initialize(MongoDatabase db) {
        // initialize the collection
        playersCollection = db.getCollection("players", PermittedPlayer.class);

        // add all the players from the database to our list
        for (PermittedPlayer player : playersCollection.find()) {
            players.add(player);
        }
    }

    /**
     * Sync the players of this Circaea instance with the players present in the database.
     */
    public void syncPlayers() {
        players.clear();

        for (PermittedPlayer player : playersCollection.find()) {
            players.add(player);
        }
    }

    public boolean playerExists(Player player) {
        // look for any players with the same uuid in the players list
        return players.stream().anyMatch(permittedPlayer -> permittedPlayer.getPlayerUuid().equals(player.getUniqueId()));
    }

    /**
     * Gets the permitted player from the local players collection.
     *
     * @param player The player you wish to get a permitted player object from.
     * @return Returns the permitted player object if successful, returns null otherwise.
     */
    public PermittedPlayer getPermittedPlayer(Player player) {
        return players.stream().filter(permittedPlayer ->
                permittedPlayer.getPlayerUuid().equals(player.getUniqueId())).findFirst().orElse(null);
    }

    public PermittedPlayer getPermittedPlayer(UUID uuid) {
        return players.stream().filter(permittedPlayer ->
                permittedPlayer.getPlayerUuid().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Register a player to the players collection.
     *
     * @param player The player to register.
     * @return Returns the permitted player object associated with the provided player.
     */
    public PermittedPlayer registerPlayer(Player player) {
        // create the player data object
        PermittedPlayer permittedPlayer =
                new PermittedPlayer(player.getUniqueId(), circaea.getRankManager().getDefaultRank(), new ArrayList<>());

        // add it to the players collection
        players.add(permittedPlayer);
        // add it to the database
        playersCollection.insertOne(permittedPlayer);
        // sync the player change with the other connected clients
        //circaea.getSocketClient().messageServer(SocketData.SyncPlayers.getData());
        // return the permitted player
        return permittedPlayer;
    }

    public CommandResponse updatePlayer(PermittedPlayer player) {
        if (players.stream().anyMatch(permittedPlayer -> permittedPlayer.getPlayerUuid() != player.getPlayerUuid())) {
            return new CommandResponse(false, "Did not find a player with the same UUID in the player collection!");
        }

        // replace the existing player
        players.replaceAll(rPlayer -> {
            // find the existing group and replace it
            if (rPlayer.getPlayerUuid().equals(player.getPlayerUuid())) {
                return player;
            }
            // if it's not the group we're looking for then don't change it
            return rPlayer;
        });

        // replace the group in the database
        playersCollection.findOneAndReplace(new Document("uuid", player.getPlayerUuid()), player);

        // sync the group change with the other connected clients
        //circaea.getSocketClient().messageServer(SocketData.SyncPlayers.getData());
        // return the successful response
        return new CommandResponse(true, "Successfully replaced old group of the same name!");
    }

    /**
     * Set a PermissionAttachment to the provided player.
     *
     * @param player The player you wish to attach a PermissionAttachment to.
     */
    public void setupAttachment(Player player) {
        players.replaceAll(permittedPlayer -> {
            // only replace the player we're looking for
            if (permittedPlayer.getPlayerUuid().equals(player.getUniqueId())) {
                // add the attachment
                permittedPlayer.setAttachment(player.addAttachment(circaea));
                // return our new permitted player
                return permittedPlayer;
            }

            // if this isn't the player we're looking for, don't change it
            return permittedPlayer;
        });
    }

    // bad

    /**
     * Remove a PermissionAttachment from the provided player.
     *
     * @param player The player you wish to remove a PermissionAttachment from.
     */
    public void removeAttachment(Player player) {
        players.replaceAll(permittedPlayer -> {
            // only replace the player we're looking for
            if (permittedPlayer.getPlayerUuid().equals(player.getUniqueId())) {
                // remove the attachment
                player.removeAttachment(permittedPlayer.getAttachment());
                // remove the attachment from the stored object
                permittedPlayer.setAttachment(null);
                // return our new permitted player
                return permittedPlayer;
            }

            // if this isn't the player we're looking for, don't change it
            return permittedPlayer;
        });
    }

    public void setPerms(PermittedPlayer player, GroupManager manager) {
        for (String groupName : player.getRank().getPermissionGroups()) {
            PermissionGroup group = manager.getGroup(groupName);

            for (String perm : group.getAddedPermissions()) {
                player.getAttachment().setPermission(perm, true);
            }

            for (String perm : group.getRemovedPermissions()) {
                player.getAttachment().setPermission(perm, false);
            }
        }

        for (String groupName : player.getPermissionGroups()) {
            PermissionGroup group = manager.getGroup(groupName);

            for (String perm : group.getAddedPermissions()) {
                player.getAttachment().setPermission(perm, true);
            }

            for (String perm : group.getRemovedPermissions()) {
                player.getAttachment().setPermission(perm, false);
            }
        }
    }

    public void removePerms(PermittedPlayer player, GroupManager manager) {
        for (String groupName : player.getRank().getPermissionGroups()) {
            PermissionGroup group = manager.getGroup(groupName);

            for (String perm : group.getAddedPermissions()) {
                player.getAttachment().setPermission(perm, false);
            }
        }

        for (String groupName : player.getPermissionGroups()) {
            PermissionGroup group = manager.getGroup(groupName);

            for (String perm : group.getAddedPermissions()) {
                player.getAttachment().setPermission(perm, false);
            }
        }
    }
}

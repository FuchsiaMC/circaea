package net.fuchsiamc.circaea.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.fuchsiamc.circaea.Circaea;
import net.fuchsiamc.circaea.permissions.PermissionGroup;
import net.fuchsiamc.circaea.util.CommandResponse;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupManager {
    private final Circaea circaea;

    /**
     * A list containing all registered permission groups.
     */
    @Getter
    private final List<PermissionGroup> groups = new ArrayList<>();

    /**
     * The mongo database collection for groups.
     */
    private MongoCollection<PermissionGroup> groupsCollection;

    public GroupManager(Circaea circaea) {
        this.circaea = circaea;
    }

    /**
     * Intialize the groups database collection, add all groups to this local Circaea instance.
     *
     * @param db The MongoDatabase that the groups collection can be found in.
     */
    public void initialize(MongoDatabase db) {
        // initialize the collection
        groupsCollection = db.getCollection("groups", PermissionGroup.class);

        // add all the groups from the database to our list
        for (PermissionGroup group : groupsCollection.find()) {
            groups.add(group);
        }
    }

    /**
     * Register a group to this local Circaea instance, to the database, and to every connected socket client.
     *
     * @param group The group you wish to register.
     * @return Returns a CommandResponse.
     */
    public CommandResponse registerGroup(PermissionGroup group) {
        for (PermissionGroup registeredGroup : groups) {
            // if trying to register an already existing name
            if (Objects.equals(group.getName(), registeredGroup.getName())) {
                // replace the existing group
                groups.replaceAll(rGroup -> {
                    // find the existing group and replace it
                    if (Objects.equals(rGroup.getName(), group.getName())) {
                        return group;
                    }
                    // if it's not the group we're looking for then don't change it
                    return rGroup;
                });

                // replace the group in the database
                groupsCollection.findOneAndReplace(new Document("name", group.getName()), group);
                // sync the group change with the other connected clients
                //circaea.getSocketClient().messageServer(SocketData.SyncGroups.getData());
                // return the successful response
                return new CommandResponse(true, "Successfully replaced old group of the same name!");
            }
        }

        // if we're here, it means we're registering a brand-new group

        // add it to the groups collection
        groups.add(group);
        // add it to the database
        groupsCollection.insertOne(group);
        // sync the group change with the other connected clients
        //circaea.getSocketClient().messageServer(SocketData.SyncGroups.getData());
        // return the successful response
        return new CommandResponse(true, "Successfully registered new permission group!");
    }

    /**
     * Sync the groups of this Circaea instance with the groups present in the database.
     */
    public void syncGroups() {
        groups.clear();

        for (PermissionGroup group : groupsCollection.find()) {
            groups.add(group);
        }
    }

    public boolean groupExists(String name) {
        return groups.stream().anyMatch(group -> group.getName().equals(name));
    }

    /**
     * Gets a group based on the provided name from the local group collection.
     *
     * @param name The name of the group you wish to get a PermissionGroup from.
     * @return Returns the group object if successful, returns null otherwise.
     */
    public PermissionGroup getGroup(String name) {
        return groups.stream().filter(group ->
                group.getName().equals(name)).findFirst().orElse(null);
    }
}

package net.fuchsiamc.circaea.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import net.fuchsiamc.circaea.permissions.PermissionGroup;
import net.fuchsiamc.circaea.util.Response;
import org.bson.Document;

public class GroupManager {
    /**
     * The mongo database collection for groups.
     */
    private MongoCollection<PermissionGroup> groupsCollection;

    public void initialize(MongoDatabase db) {
        // initialize the collection
        groupsCollection = db.getCollection("groups", PermissionGroup.class);
    }

    public Response registerGroup(PermissionGroup group) {
        // check if a group with the same name already exists because we don't want duplicates
        if (groupsCollection.find(Filters.eq("name", group.getName())).first() != null) {
            return new Response(false, "This permission group already exists!");
        }

        // add it to the database
        groupsCollection.insertOne(group);
        return new Response(true, "Successfully registered new permission group!");
    }

    public Response updateGroup(PermissionGroup group) {
        // replace the group in the database
        if (groupsCollection.findOneAndReplace(new Document("name", group.getName()), group) == null) {
            return new Response(false, "This permission group does not exist!");
        }

        return new Response(true, "Successfully updated permission group!");
    }

    public FindIterable<PermissionGroup> getGroups() {
        return groupsCollection.find();
    }

    public PermissionGroup getGroup(String groupName) {
        return groupsCollection.find(Filters.eq("name", groupName)).first();
    }

    public boolean groupExists(String groupName) {
        return getGroup(groupName) != null;
    }
}

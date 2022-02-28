package net.fuchsiamc.circaea.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import net.fuchsiamc.circaea.Circaea;
import net.fuchsiamc.circaea.permissions.PermissionRank;
import net.fuchsiamc.circaea.util.Response;
import org.bson.Document;

public class RankManager {
    private final Circaea circaea;

    /**
     * The mongo database collection for ranks.
     */
    private MongoCollection<PermissionRank> ranksCollection;

    public RankManager(Circaea circaea) {
        this.circaea = circaea;
    }

    public void initialize(MongoDatabase db) {
        // initialize the collection
        ranksCollection = db.getCollection("ranks", PermissionRank.class);

        // if a default rank does not exist, register it
        if (ranksCollection.find(Filters.eq("name", "circaea:default")).first() != null) {
            registerRank(new PermissionRank("circaea:default", 0, null));
        }
    }

    public Response registerRank(PermissionRank rank) {
        // check if a rank with the same name already exists because we don't want duplicates
        if (ranksCollection.find(Filters.eq("name", rank.getName())).first() != null) {
            return new Response(false, "This permission rank already exists!");
        }

        // check if the rank's priority isn't 0
        if (!rank.getName().equals("circaea:default") && rank.getPriority() == 0) {
            return new Response(false, "Only \"circaea:default\" can have a priority of 0!");
        }

        // check if any existing ranks have a conflicting priority
        for (PermissionRank permissionRank : ranksCollection.find()) {
            if (permissionRank.getPriority() == rank.getPriority()) {
                return new Response(false, "A rank with the same priority already exists!");
            }
        }

        // add it to the database
        ranksCollection.insertOne(rank);
        return new Response(true, "Successfully registered new permission rank!");
    }

    public Response updateRank(PermissionRank rank) {
        // replace the rank in the database
        if (ranksCollection.findOneAndReplace(new Document("name", rank.getName()), rank) == null) {
            return new Response(false, "This permission rank does not exist!");
        }

        if (!rank.getName().equals("circaea:default") && rank.getPriority() == 0) {
            return new Response(false, "Only \"circaea:default\" can have a priority of 0!");
        }

        for (PermissionRank permissionRank : ranksCollection.find()) {
            // check if the rank we're trying to replace has a conflicting priority
            // UNLESS the conflicting priority is itself (it's not conflicting)
            if (permissionRank.getPriority() == rank.getPriority() && !permissionRank.getName().equals(rank.getName())) {
                return new Response(false, "A rank with the same priority already exists!");
            }
        }

        circaea.getPlayerManager().refreshAllPermissions();

        return new Response(true, "Successfully updated permission rank!");
    }


    public PermissionRank getDefaultRank() {
        return ranksCollection.find(Filters.eq("name", "circaea:default")).first();
    }

    public FindIterable<PermissionRank> getRanks() {
        return ranksCollection.find();
    }

    public PermissionRank getRank(String rankName) {
        return ranksCollection.find(Filters.eq("name", rankName)).first();
    }

    public boolean rankExists(String rankName) {
        return getRank(rankName) != null;
    }
}

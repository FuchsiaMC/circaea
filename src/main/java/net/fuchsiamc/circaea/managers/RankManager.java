package net.fuchsiamc.circaea.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.fuchsiamc.circaea.Circaea;
import net.fuchsiamc.circaea.permissions.PermissionRank;
import net.fuchsiamc.circaea.util.CommandResponse;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RankManager {
    private final Circaea circaea;

    /**
     * A list containing all registered permission ranks.
     */
    @Getter
    private final List<PermissionRank> ranks = new ArrayList<>();

    /**
     * The mongo database collection for ranks.
     */
    private MongoCollection<PermissionRank> ranksCollection;

    public RankManager(Circaea circaea) {
        this.circaea = circaea;
    }

    /**
     * Intialize the ranks database collection, add all ranks to this local Circaea instance, and
     * create a default rank if it does not exist.
     *
     * @param db The MongoDatabase that the ranks collection can be found in.
     */
    public void initialize(MongoDatabase db) {
        // initialize the collection
        ranksCollection = db.getCollection("ranks", PermissionRank.class);

        // add all the ranks from the database to our list
        for (PermissionRank rank : ranksCollection.find()) {
            ranks.add(rank);
        }

        // if a default rank does not exist, register it
        if (ranks.stream().noneMatch(x -> Objects.equals(x.getName(), "circaea:default"))) {
            registerRank(new PermissionRank(new ArrayList<>(), "circaea:default", 0, null));
        }
    }

    /**
     * Register a rank to this local Circaea instance, to the database, and to every connected socket client.
     *
     * @param rank The rank you wish to register.
     * @return Returns a CommandResponse.
     */
    public CommandResponse registerRank(PermissionRank rank) {
        // check if the rank's priority isn't 0
        if (!Objects.equals(rank.getName(), "circaea:default") && rank.getPriority() == 0)
            return new CommandResponse(false, "Only \"circaea:default\" can have a priority of 0!");

        for (PermissionRank registeredRank : ranks) {
            // if trying to register a rank with the same priority as an existing one
            if (registeredRank.getPriority() == rank.getPriority() &&
                    !Objects.equals(registeredRank.getName(), rank.getName())) {
                // return unsuccessful response
                return new CommandResponse(false,
                        "Rank \"" + registeredRank.getName() + "\"'s " +
                                "priority conflicts with the rank you're trying to register!");
            }

            // if trying to register an already existing name
            if (Objects.equals(rank.getName(), registeredRank.getName())) {
                // replace the existing rank
                ranks.replaceAll(rRank -> {
                    // find the existing rank and replace it
                    if (Objects.equals(rRank.getName(), rank.getName())) {
                        return rank;
                    }
                    // if it's not the rank we're looking for then don't change it
                    return rRank;
                });

                // replace the rank in the database
                ranksCollection.findOneAndReplace(new Document("name", rank.getName()), rank);
                // sync the rank change with the other connected clients
                //circaea.getSocketClient().messageServer(SocketData.SyncRanks.getData());
                // return the successful response
                return new CommandResponse(true, "Successfully replaced old rank of the same name!");
            }
        }

        // if we're here, it means we're registering a brand-new rank

        // add it to the ranks collection
        ranks.add(rank);
        // add it to the database
        ranksCollection.insertOne(rank);
        // sync the rank change with the other connected clients
        //circaea.getSocketClient().messageServer(SocketData.SyncRanks.getData());
        // return the successful response
        return new CommandResponse(true, "Successfully registered new permission rank!");
    }

    /**
     * Sync the ranks of this Circaea instance with the ranks present in the database.
     */
    public void syncRanks() {
        ranks.clear();

        for (PermissionRank rank : ranksCollection.find()) {
            ranks.add(rank);
        }
    }

    public PermissionRank getDefaultRank() {
        // a default rank will ALWAYS exist
        //noinspection OptionalGetWithoutIsPresent
        return ranks.stream().filter(permissionRank ->
                Objects.equals(permissionRank.getName(), "circaea:default")).findFirst().get();
    }

    public boolean rankExists(String name) {
        return ranks.stream().anyMatch(rank -> rank.getName().equals(name));
    }

    /**
     * Gets a rank based on the provided name from the local group collection.
     *
     * @param name The name of the rank you wish to get a PermissionRank from.
     * @return Returns the rank object if successful, returns null otherwise.
     */
    public PermissionRank getRank(String name) {
        return ranks.stream().filter(rank ->
                rank.getName().equals(name)).findFirst().orElse(null);
    }
}

package net.fuchsiamc.circaea;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.fuchsiamc.circaea.commands.CircaeaCommand;
import net.fuchsiamc.circaea.commands.debug.PlayerNameTestCommand;
import net.fuchsiamc.circaea.eventhandlers.PlayerEventHandler;
import net.fuchsiamc.circaea.managers.GroupManager;
import net.fuchsiamc.circaea.managers.PlayerManager;
import net.fuchsiamc.circaea.managers.RankManager;
import net.fuchsiamc.gaura.commands.IFuchsiaCommand;
import net.fuchsiamc.gaura.core.FuchsiaPlugin;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// todo: might want to write manager interfaces or something
public final class Circaea extends FuchsiaPlugin {
    /**
     * The mongo database for circaea.
     */
    @Getter
    private MongoDatabase database;

    /**
     * Manager class manging everything to do with permitted players.
     */
    @Getter
    private PlayerManager playerManager;

    /**
     * Manager class manging everything to do with permission ranks.
     */
    @Getter
    private RankManager rankManager;

    /**
     * Manager class managing everything to do with permission groups.
     */
    @Getter
    private GroupManager groupManager;

    @Getter
    private MongoClient mongoClient;

    @Override
    public void onEnable() {
        super.onEnable();

        playerManager = new PlayerManager(this);
        rankManager = new RankManager();
        groupManager = new GroupManager();

        // Ensure config exists.
        saveDefaultConfig();

        // todo: move to gaura config system
        // todo: move to gaura logging/disable
        if (setupMongoDB())
            return;

        rankManager.initialize(database);
        groupManager.initialize(database);
        playerManager.initialize(database);

        getServer().getPluginManager().registerEvents(new PlayerEventHandler(this), this);
    }

    @Override
    public void onDisable() {
        mongoClient.close();
    }

    private boolean setupMongoDB() {
        // todo: also allow not using a string
        String clientUrl = getConfig().getString("mongodb.clienturl");

        if (Objects.equals(clientUrl, "") || clientUrl == null) {
            getLogger().severe("MongoDB Client URL was not provided in the config.");
            getServer().getPluginManager().disablePlugin(this);
            return true;
        }

        // set up the mongo client
        ConnectionString connectionString =
                new ConnectionString(clientUrl);

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(connectionString)
                .codecRegistry(pojoCodecRegistry)
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("circaea");
        return false;
    }

    @Override
    public List<IFuchsiaCommand> getCommands() {
        List<IFuchsiaCommand> list = new ArrayList<>();

        list.add(new PlayerNameTestCommand());
        list.add(new CircaeaCommand(this));

        return list;
    }
}

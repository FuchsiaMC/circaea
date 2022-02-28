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
import net.fuchsiamc.circaea.websocket.CircaeaClient;
import net.fuchsiamc.circaea.websocket.CircaeaServer;
import net.fuchsiamc.gaura.commands.IFuchsiaCommand;
import net.fuchsiamc.gaura.core.FuchsiaPlugin;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.scheduler.BukkitRunnable;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Circaea extends FuchsiaPlugin {
    /**
     * The mongo database for circaea.
     */
    @Getter
    private MongoDatabase database;

    /**
     * Web socket client for Circaea, used for syncing between servers.
     */
    @Getter
    private CircaeaClient socketClient;

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
    private BukkitRunnable clientRunnable;

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

    private boolean setupSocket() {
        String address = getConfig().getString("websocket.address");
        if (address == null || address.equals("")) {
            address = "localhost";
        }

        int port = getConfig().getInt("websocket.port");
        if (port == 0) {
            port = 567;
        }

        // initialize socket client

        try {
            socketClient = new CircaeaClient(this, new URI("ws://" + address + ":" + port));
        } catch (URISyntaxException e) {
            getLogger().severe("Web Socket Client failed to initialize.");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return true;
        }

        // initialize and run socket server
        // socket server then runs socket client when it starts up
        // or if we're not starting up a server, start the client here

        boolean runServer = getConfig().getBoolean("websocket.server.enabled");

        if (runServer) {
            WebSocketServer server = new CircaeaServer(this, new InetSocketAddress(address, port));
            // todo: dispose on disable
            new BukkitRunnable() {
                @Override
                public void run() {
                    server.run();
                }
            }.runTaskAsynchronously(this);
        } else {
            // connect to the socket client
            clientRunnable.runTaskAsynchronously(this);
        }

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

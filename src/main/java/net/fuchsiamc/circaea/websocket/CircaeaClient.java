package net.fuchsiamc.circaea.websocket;

import net.fuchsiamc.circaea.Circaea;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class CircaeaClient extends WebSocketClient {
    private final Circaea circaea;

    public CircaeaClient(Circaea circaea, URI serverURI) {
        super(serverURI);
        this.circaea = circaea;
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        circaea.getLogger().info("Socket client successfully opened.");
    }

    @Override
    public void onMessage(String s) {
        // we don't send any strings so this is empty
    }

    @Override
    public void onMessage(ByteBuffer message) {
        byte data = message.array()[0];

        circaea.getLogger().info(
                "Socket client received ByteBuffer: " + message +
                        " with data of: " + data
        );

        if (data == SocketData.SyncGroups.getData()) {
            circaea.getGroupManager().syncGroups();
        } else if (data == SocketData.SyncRanks.getData()) {
            circaea.getRankManager().syncRanks();
        } else if (data == SocketData.SyncPlayers.getData()) {
            circaea.getPlayerManager().syncPlayers();
        }
    }

    /**
     * Message the WebSocket server with a byte, telling it what to do.
     *
     * @param b The byte of data to send.
     */
    public void messageServer(byte b) {
        // send a byte of data to the socket server.
        send(new byte[]{b});
    }

    @Override
    public void onError(Exception ex) {
        circaea.getLogger().warning("Web socket client threw an error: " + ex);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        circaea.getLogger().warning("Web socket client closed with exit code: " + code +
                " with the reason of: " + reason);
    }
}

package net.fuchsiamc.circaea.websocket;

import net.fuchsiamc.circaea.Circaea;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

// todo: move to its own program
public class CircaeaServer extends WebSocketServer {

    private final Circaea circaea;

    public CircaeaServer(Circaea circaea, InetSocketAddress address) {
        super(address);
        this.circaea = circaea;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        circaea.getLogger().info("New socket client connection: " + handshake.getResourceDescriptor());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        circaea.getLogger().warning(
                "Web socket closed " + conn.getRemoteSocketAddress() +
                        " with exit code: " + code +
                        " and reason of: " + reason
        );
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // we dont send string messages
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        byte[] messageData = message.array();

        circaea.getLogger().info(
                "Socket server received ByteBuffer: " + message +
                        " with data of: " + Arrays.toString(messageData) +
                        " from: " + conn.getRemoteSocketAddress()
        );

        // send the data a client sent us to everyone
        // for example if a client tells us to sync ranks
        // we tell every connected client to sync ranks
        // including the one that told us to sync

        // we only want to send 1 byte of data
        ByteBuffer data = ByteBuffer.allocate(messageData.length);
        data.put(messageData);

        // send the data to every client
        broadcast(messageData);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        circaea.getLogger().severe("An error occurred in socket server: " + conn.getRemoteSocketAddress() + ": " + ex);
    }

    @Override
    public void onStart() {
        circaea.getLogger().info("Socket server successfully started!");

        // connect to the socket client
        circaea.getClientRunnable().runTaskAsynchronously(circaea);
    }
}

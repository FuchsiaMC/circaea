package net.fuchsiamc.circaea.websocket;

import lombok.Getter;

public enum SocketData {
    SyncGroups(0),
    SyncRanks(1),
    SyncPlayers(2),
    ;

    @Getter
    private final byte data;

    SocketData(int data) {
        this.data = (byte) data;
    }
}

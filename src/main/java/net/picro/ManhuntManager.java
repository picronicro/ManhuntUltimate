package net.picro;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;

public class ManhuntManager {

    private final ServerPlayerEntity host;

    private ArrayList<ServerPlayerEntity> runners;
    private ArrayList<ServerPlayerEntity> hunters;

    public ManhuntManager(ServerPlayerEntity host) {
        this.host = host;
    }

    // getters/setters
    public ServerPlayerEntity getHost() {
        return host;
    }

}

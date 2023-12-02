package net.picro;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ManhuntManager {

    private final ServerPlayerEntity host;

    private Set<ServerPlayerEntity> runners = new HashSet<>();
    private Set<ServerPlayerEntity> hunters = new HashSet<>();

    public ManhuntManager(ServerPlayerEntity host) {
        this.host = host;

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("listplayers").executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal("runners: " + runners), false);
                    context.getSource().sendFeedback(() -> Text.literal("hunters: " + hunters), false);

                    return 1;
                })));
    }

    // DEBUG
    public void listPlayers() {
        System.out.println("runners: " + runners);
        System.out.println("hunters: " + hunters);
    }

    // getters/setters
    public ServerPlayerEntity getHost() {
        return host;
    }

    public Set<ServerPlayerEntity> getRunners() {
        return runners;
    }

    public void setRunners(Set<ServerPlayerEntity> runners) {
        System.out.println(runners);
        this.runners = runners;
    }

    public Set<ServerPlayerEntity> getHunters() {
        return hunters;
    }

    public void setHunters(Set<ServerPlayerEntity> hunters) {
        this.hunters = hunters;
    }
}

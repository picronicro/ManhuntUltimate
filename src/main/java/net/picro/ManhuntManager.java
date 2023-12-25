package net.picro;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;

public class ManhuntManager {

    private final ServerPlayerEntity host;

    // lists
    private Set<ServerPlayerEntity> runners = new HashSet<>();
    private Set<ServerPlayerEntity> hunters = new HashSet<>();
    // timeout (TIMEOUT)
    private int timeout = 60;

    // start mode
    private StartModeEnum startMode = StartModeEnum.TIMEOUT;

    // run session
    public ManhuntSession manhuntSession;

    public ManhuntManager(ServerPlayerEntity host) {
        this.host = host;

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("listplayers").executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal("runners: " + runners), false);
                    context.getSource().sendFeedback(() -> Text.literal("hunters: " + hunters), false);

                    return 1;
                })));
    }

    // methods
    // start manhunt run session
    public void startRun() {
        Main.LOGGER.warn("Starting new manhunt session");
        manhuntSession = new ManhuntSession(this, host.getPos(), timeout);
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

    public StartModeEnum getStartMode() {
        return startMode;
    }

    public void setStartMode(StartModeEnum startMode) {
        this.startMode = startMode;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    // start mode enum
    public enum StartModeEnum {
        TIMEOUT("timeout", "Hunters will be stunned for a defined amount of time, so runners will have time to prepare."),
        PUNCH("hitting a hunter", "Speedrun starts when one of the runners hits a hunter.");

        public final String name;
        public final String desc;

        StartModeEnum(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }
    }

    // player role
    public enum PlayerRole {
        RUNNER,
        HUNTER
    }
}

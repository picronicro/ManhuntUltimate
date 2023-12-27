package net.picro;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.HashSet;
import java.util.Set;

import static net.picro.packets.ManhuntInGamePackets.packetDeath;

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

        registerEvents();
    }

    // methods
    // start manhunt run session
    public void startRun() {
        Main.LOGGER.warn("Starting new manhunt session");
        manhuntSession = new ManhuntSession(this, host.getPos(), timeout);
    }

    // register events
    private void registerEvents() {
        // player death event
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (manhuntSession != null) {
                if (entity instanceof ServerPlayerEntity) {
                    if (damageAmount >= entity.getHealth()) {
                        ServerPlayerEntity player = (ServerPlayerEntity) entity;
                        packetDeath(player, false);
                        player.setHealth(20);
                        player.getHungerManager().setFoodLevel(20);
                        player.getHungerManager().setSaturationLevel(10);
                        player.changeGameMode(GameMode.SPECTATOR);

                        // drop xp/item logic
                        player.getInventory().dropAll();
                        ExperienceOrbEntity.spawn(player.getServerWorld(), player.getPos(), player.getXpToDrop());
                        player.setExperienceLevel(0);
                        player.setExperiencePoints(0);
                        player.setVelocity(0, 0, 0);
                        player.clearStatusEffects();
                        player.dismountVehicle();

                        // if it was a runner
                        if (manhuntSession.getAliveRunners().contains(player)) {
                            manhuntSession.getAliveRunners().remove(player);
                            manhuntSession.areRunnersAlive(player);
                        }

                        return false;
                    } else {
                        return true;
                    }
                }
            }
            return true;
        });
    }

    // getters/setters
    public ServerPlayerEntity getHost() {
        return host;
    }

    public Set<ServerPlayerEntity> getRunners() {
        return runners;
    }
    public void setRunners(Set<ServerPlayerEntity> runners) {
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

    // ENUMS
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

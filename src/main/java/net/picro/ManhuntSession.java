package net.picro;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameMode;
import net.picro.ManhuntManager.StartModeEnum;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.picro.packets.ManhuntGamePreparationPackets.*;
import static net.picro.packets.ManhuntInGamePackets.*;

public class ManhuntSession {

    private final ManhuntManager manhuntManager;

    private final Set<ServerPlayerEntity> aliveRunners;
    private final Vec3d startOrigin;
    // timeout only for TIMEOUT mode
    private int timeout;

    public ManhuntSession(@NotNull ManhuntManager manhuntManager, Vec3d startOrigin, int timeout) {
        this.manhuntManager = manhuntManager;
        this.startOrigin = startOrigin;
        this.timeout = timeout;

        // world spawn
        manhuntManager.getHost().getServerWorld()
                .setSpawnPos(new BlockPos(new Vec3i((int) startOrigin.getX(), (int) startOrigin.getY(), (int) startOrigin.getZ())), 0);

        // alive runners
        aliveRunners = new HashSet<>(manhuntManager.getRunners());

        // basic preparation
        preparePlayers();
        startSession(manhuntManager.getStartMode());

        // register packets
        registerPackets();
    }

    private void startSession(StartModeEnum startType) {
        switch (startType) {
            case TIMEOUT -> timeoutModeSession();
            case PUNCH -> {
            }
        }
    }

    // prepare players
    private void preparePlayers() {
        for (ServerPlayerEntity player : PlayerLookup.all(Objects.requireNonNull(manhuntManager.getHost().getServer()))) {
            player.changeGameMode(GameMode.SURVIVAL);
            player.setHealth(20);
            player.getHungerManager().setFoodLevel(20);
            player.getHungerManager().setSaturationLevel(20);
            player.clearStatusEffects();
            player.dismountVehicle();
        }
    }

    // MODES
    // start TIMEOUT mode session
    public void timeoutModeSession() {
        // teleport runners
        manhuntManager.getRunners().forEach(player -> {
            packetAssignRoles(player, ManhuntManager.PlayerRole.RUNNER); // client var role
            player.sendMessage(Text.literal(Formatting.YELLOW + "ℹ Prepare yourself before the hunters will be released."));
            player.teleport(manhuntManager.getHost().getServerWorld(), startOrigin.getX(), startOrigin.getY(), startOrigin.getZ(), 90, 0);
        });
        // teleport hunters
        manhuntManager.getHunters().forEach(player -> {
            packetAssignRoles(player, ManhuntManager.PlayerRole.HUNTER); // client var role
            player.sendMessage(Text.literal(Formatting.YELLOW + "ℹ You will be released at the end of the preparation time."));
            player.teleport(manhuntManager.getHost().getServerWorld(), startOrigin.getX(), startOrigin.getY(), startOrigin.getZ(), 90, 0);
        });

        // start timer
        startTimer();
    }

    // misc
    // start timer
    private void startTimer() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                PlayerLookup.all(Objects.requireNonNull(manhuntManager.getHost().getServer())).forEach(player -> {
                    packetUpdateTimer(player, timeout);
                    if (manhuntManager.getHunters().contains(player)) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 9999, 999, false, false, false));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 9999, 999, false, false, false));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 9999, 999, false, false, false));
                    }
                });

                if (timeout <= 0) {
                    this.cancel();
                    for (ServerPlayerEntity player : PlayerLookup.all(manhuntManager.getHost().getServer())) {
                        packetEndTimer(player);
                        if (manhuntManager.getHunters().contains(player))
                            player.clearStatusEffects();
                    }
                }
                timeout--;
            }
        }, 0, 1000);
    }

    // register packets
    private void registerPackets() {
        // respawn packet
        ServerPlayNetworking.registerGlobalReceiver(PACKET_RESPAWN, (server, player, handler, buf, responseSender) -> {
            // respawn
            if (player.getSpawnPointPosition() != null) { // if spawnpoint exists
                BlockPos pos = player.getSpawnPointPosition();
                player.teleport(pos.getX(), pos.getY(), pos.getZ());
            } else { // or use world spawn
                BlockPos pos = player.getServerWorld().getSpawnPos();
                player.teleport(pos.getX(), pos.getY(), pos.getZ());
            }
            player.setPitch(0);
            player.setYaw(0);
            player.changeGameMode(GameMode.SURVIVAL);
        });
    }

    // dispose session
    private void dispose() {
        ServerPlayNetworking.unregisterGlobalReceiver(PACKET_RESPAWN);
    }

    // check that runners alive
    public void areRunnersAlive(ServerPlayerEntity lastPlayer) {
        // lastPlayer for kill-cam
        if (aliveRunners.isEmpty()) {
            // stop the game
            lastPlayer.sendMessage(Text.literal("last man standing"));
            for (ServerPlayerEntity player : PlayerLookup.all(Objects.requireNonNull(manhuntManager.getHost().getServer()))) {
                packetGameEnd(player, ManhuntManager.PlayerRole.HUNTER);
            }

            dispose();
            manhuntManager.manhuntSession = null;
        }

    }

    // getters & setters
    // get alive runners
    public Set<ServerPlayerEntity> getAliveRunners() {
        return aliveRunners;
    }

}

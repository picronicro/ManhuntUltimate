package net.picro;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;
import static net.picro.packets.ManhuntToggleStatusPacket.packetManhuntToggleStatus;

public class Main implements ModInitializer {

    // modid
    public static final String MOD_ID = "manhuntultimate";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean isManhuntActivated = false;

    // manhunt manager
    public static ManhuntManager manhuntManager;

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");

        // on join event
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            System.out.println(handler.getPlayer().getName());
            packetManhuntToggleStatus(handler.getPlayer(), isManhuntActivated);
        });

        // activate (permanently) manhunt mode
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("manhunt").executes(context ->
                                {
                                    context.getSource().sendFeedback(() -> Text.literal("Called /manhunt with no arguments"), false);

                                    // DEBUG
                                    var playerPos = context.getSource().getPlayer().getPos(); // PLAYER
                                    var targetPos = new Vec3d(-31, 70, 82); // TARGET

                                    double d = targetPos.x - playerPos.x;
                                    double f = targetPos.z - playerPos.z;
                                    //context.getSource().getPlayer().setYaw(MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F));

                                    System.out.println(context.getSource().getPlayer().getYaw());
                                    System.out.println(MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F));

                                    return 1;
                                })
                                .then(literal("activate").executes(context -> {
                                    if (!isManhuntActivated) {
                                        isManhuntActivated = true;

                                        manhuntManager = new ManhuntManager(context.getSource().getPlayer());
                                        context.getSource().sendFeedback(() -> Text.literal(Formatting.GREEN + "Manhunt mode activated"), false);
                                    } else {
                                        context.getSource().sendFeedback(() -> Text.literal(Formatting.RED + "Manhunt mode is already activated"), false);
                                    }

                                    for (ServerPlayerEntity player : PlayerLookup.all(context.getSource().getServer())) {
                                        packetManhuntToggleStatus(player, isManhuntActivated);
                                    }

                                    return 1;
                                }))
                ));
    }

    // debug
    public static double calculateAngle(Vec3d pos1, Vec3d pos2) {
        // Calculate the difference in coordinates
        double deltaX = pos2.x - pos1.x;
        double deltaY = pos2.y - pos1.y;
        double deltaZ = pos2.z - pos1.z;

        // Calculate the angle using atan2
        double theta = Math.atan2(deltaZ, deltaX);

        // Convert the angle to degrees if needed
        // double degrees = Math.toDegrees(theta);

        return theta;
    }

}
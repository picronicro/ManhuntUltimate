package net.picro;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// getString(ctx, "string")
import java.util.Objects;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
// word()
import static com.mojang.brigadier.arguments.StringArgumentType.word;
// literal("foo")
import static net.minecraft.server.command.CommandManager.literal;
// argument("bar", word())
import static net.minecraft.server.command.CommandManager.argument;
// Import everything in the CommandManager
import static net.minecraft.server.command.CommandManager.*;

public class Main implements ModInitializer {

	// modid
	public static final String MOD_ID = "manhuntultimate";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static boolean isManhuntActivated = false;

	// packets identifier
	public static final Identifier PACKET_ID = new Identifier(MOD_ID, "custom_packet");
	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		// enable manhunt mode command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(CommandManager.literal("manhunt").executes(context -> {
					context.getSource().sendFeedback(() -> Text.literal("Called /manhunt with no arguments"), false);

					return 1;
				})
						.then(literal("toggle").executes(context -> {
							if (Main.isManhuntActivated) {
								Main.isManhuntActivated = false;
								context.getSource().sendFeedback(() -> Text.literal("Manhunt mode has been disabled"), false);
							} else {
								Main.isManhuntActivated = true;
								context.getSource().sendFeedback(() -> Text.literal("Manhunt mode has been enabled"), false);
							}

							for (ServerPlayerEntity player : PlayerLookup.all(context.getSource().getServer())) {
								ServerPlayNetworking.send(player, PACKET_ID, PacketByteBufs.create().writeBoolean(isManhuntActivated));
							}

							return 1;
						}))
				));

		// activate (permanently) manhunt mode
	}
}
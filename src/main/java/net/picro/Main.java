package net.picro;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;

public class Main implements ModInitializer {

	// modid
	public static final String MOD_ID = "manhuntultimate";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static boolean isManhuntActivated = false;

	// packets identifier
	public static final Identifier PACKET_ID = new Identifier(MOD_ID, "custom_packet");

	// manhunt manager
	public static ManhuntManager manhuntManager;

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		// on join event
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			System.out.println(handler.getPlayer().getName());
			packetManhuntToggleStatus(handler.getPlayer());
		});

		// activate (permanently) manhunt mode
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(CommandManager.literal("manhunt").executes(context -> {
					context.getSource().sendFeedback(() -> Text.literal("Called /manhunt with no arguments"), false);
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
								packetManhuntToggleStatus(player);
							}

							return 1;
						}))
				));

	}

	// other methods
	// send manhunt toggle packet
	private void packetManhuntToggleStatus(ServerPlayerEntity player) {
		ServerPlayNetworking.send(player, PACKET_ID, PacketByteBufs.create().writeBoolean(isManhuntActivated));
	}

}
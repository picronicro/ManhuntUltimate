package net.picro;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.picro.hud.CustomHud;
import net.picro.packets.ManhuntGamePreparationPackets;
import net.picro.packets.ManhuntInGamePackets;
import net.picro.packets.ManhuntToggleStatusPacket;
import net.picro.screens.CustomDeathScreen;
import net.picro.screens.GameOverScreen;

public class MainClient implements ClientModInitializer {

	// hud
	private CustomHud hud;

	// player role
	public static ManhuntManager.PlayerRole PLAYER_ROLE;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		System.out.println("client response");
//		HudRenderCallback.EVENT.register(hud);

		// dispose hud on disconnect
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			System.out.println("CLIENT HAS BEEN DISCONNECTED");
		});

		// manhunt mode status & create hud on login
		ClientPlayNetworking.registerGlobalReceiver(ManhuntToggleStatusPacket.PACKET_ID, (client, handler, buf, responseSender) -> {
			boolean toggle = buf.readBoolean();

			client.execute(() -> {
				if (hud == null) {
					hud = new CustomHud();
				}
				MinecraftClient.getInstance().setScreen(new GameOverScreen(ManhuntManager.PlayerRole.HUNTER));
				hud.setManhuntMode(toggle);
			});
		});

		// start: assign roles
		ClientPlayNetworking.registerGlobalReceiver(ManhuntGamePreparationPackets.PACKET_ASSIGN_ROLES, (client, handler, buf, responseSender) -> {
			ManhuntManager.PlayerRole role = buf.readEnumConstant(ManhuntManager.PlayerRole.class);

			client.execute(() -> {
				PLAYER_ROLE = role;
				hud.showRole(PLAYER_ROLE);
			});
		});

		// start: update timer
		ClientPlayNetworking.registerGlobalReceiver(ManhuntGamePreparationPackets.PACKET_TIMER_UPDATE, (client, handler, buf, responseSender) -> {
			int time = buf.readInt();

			client.execute(() -> hud.updateTime(time));
		});

		// start: end timer
		ClientPlayNetworking.registerGlobalReceiver(ManhuntGamePreparationPackets.PACKET_TIMER_END, (client, handler, buf, responseSender) -> {
			client.execute(() -> hud.endTimer());
		});

		// game: death packet
		ClientPlayNetworking.registerGlobalReceiver(ManhuntInGamePackets.PACKET_DEATH, (client, handler, buf, responseSender) -> {
			boolean isFinal = buf.readBoolean();

			// todo: если isFinal равен true, то показывать экран статов (другой пакет)
			client.execute(() -> {
				if (!isFinal) {
					MinecraftClient.getInstance().setScreen(new CustomDeathScreen());
				}
			});
		});

		// game: game over
		ClientPlayNetworking.registerGlobalReceiver(ManhuntInGamePackets.PACKET_GAME_END, (client, handler, buf, responseSender) -> {
			ManhuntManager.PlayerRole winner = buf.readEnumConstant(ManhuntManager.PlayerRole.class);

			client.execute(() -> {
				MinecraftClient.getInstance().setScreen(new GameOverScreen(winner));
			});
		});

		// game: runner pos
		ClientPlayNetworking.registerGlobalReceiver(ManhuntInGamePackets.PACKET_RUNNER_POS, (client, handler, buf, responseSender) -> {
			String name = buf.readString();
			BlockPos pos = buf.readBlockPos();

			client.execute(() -> hud.getCompass().updateRunnerPos(name, new Vec3d(pos.getX(), pos.getY(), pos.getZ())));
		});

	}

}
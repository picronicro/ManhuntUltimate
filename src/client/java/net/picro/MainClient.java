package net.picro;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class MainClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		System.out.println("client response");

		CustomHud hud = new CustomHud();
		HudRenderCallback.EVENT.register(hud);

		ClientPlayNetworking.registerGlobalReceiver(Main.PACKET_ID, (client, handler, buf, responseSender) -> {
			boolean toggle = buf.readBoolean();

			client.execute(() -> {
				hud.setManhuntMode(toggle);
			});
		});
	}
}
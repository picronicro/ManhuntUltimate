package net.picro.screens;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.picro.MainClient;
import net.picro.ManhuntManager;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import static net.picro.packets.ClientManhuntInGamePackets.packetRespawn;

public class CustomDeathScreen extends BaseOwoScreen<FlowLayout> {

    private int respawnTimeout = 7;
    // components
    private ParentComponent footer;
    private ParentComponent header;
    private LabelComponent label;

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var base = rootComponent
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);
        base.mouseDown().subscribe((mouseX, mouseY, button) -> {
            if (MainClient.PLAYER_ROLE == ManhuntManager.PlayerRole.HUNTER) {
                if (button == 0 && respawnTimeout <= 0) {
                    packetRespawn();
                    MinecraftClient.getInstance().options.setPerspective(Perspective.FIRST_PERSON);
                    this.close();
                }
            } else {
                if (button == 0 && respawnTimeout <= 5) {
                    assert MinecraftClient.getInstance().player != null;
                    MinecraftClient.getInstance().player.sendMessage(Text.literal("ℹ Your teammates can revive you."));
                    MinecraftClient.getInstance().options.setPerspective(Perspective.FIRST_PERSON);
                    this.close();
                }
            }
            return true;
        });

        label = Components.label(Text.literal("Respawning in 8..."));
        footer = Containers.horizontalFlow(Sizing.fill(), Sizing.fill(10))
                .child(label)
                .surface(Surface.flat(ColorHelper.Argb.getArgb(150, 0, 0, 0)));
        footer.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        footer.positioning(Positioning.relative(0, 115));
        rootComponent.child(footer);

        header = Containers.horizontalFlow(Sizing.fill(), Sizing.fill(10))
                .surface(Surface.flat(ColorHelper.Argb.getArgb(150, 0, 0, 0)));
        header.positioning(Positioning.relative(0, -15));
        rootComponent.child(header);

        // player perspective
        MinecraftClient.getInstance().player.setPitch(40);
        MinecraftClient.getInstance().options.setPerspective(Perspective.THIRD_PERSON_BACK);

        if (MainClient.PLAYER_ROLE == ManhuntManager.PlayerRole.HUNTER) {
            startRespawnTimeout();
        } else {
            spectateTransition();
        }
    }

    // start timeout
    private void startRespawnTimeout() {
        // respawn timeout
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (respawnTimeout == 5) {
                    footer.positioning().animate(250, Easing.LINEAR, Positioning.relative(0, 100)).forwards();
                    header.positioning().animate(250, Easing.LINEAR, Positioning.relative(0, 0)).forwards();
                }

                if (respawnTimeout > 0) {
                    label.text(Text.literal("Respawning in " + respawnTimeout + "..."));
                    respawnTimeout--;
                } else {
                    label.text(Text.literal("Press LMB to respawn"));
                    this.cancel();
                }
            }
        }, 0, 1000);
    }

    // start spectate transition
    private void spectateTransition() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (respawnTimeout == 5) {
                    footer.positioning().animate(250, Easing.LINEAR, Positioning.relative(0, 100)).forwards();
                    header.positioning().animate(250, Easing.LINEAR, Positioning.relative(0, 0)).forwards();
                    label.text(Text.literal("Press LMB to spectate"));
                    this.cancel();
                }
                respawnTimeout--;
            }
        }, 0, 1000);
    }

}

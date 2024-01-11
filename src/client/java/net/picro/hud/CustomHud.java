package net.picro.hud;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Window;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.picro.Main;
import net.picro.MainClient;
import net.picro.ManhuntManager;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.*;

public class CustomHud {

    private boolean isManhuntMode = false;

    // time tick sfx
    private static final List<Integer> TICK_TIME = Arrays.asList(60, 30, 10, 5, 4, 3, 2, 1);

    // timer text
    private Component timerContainer;
    private final LabelComponent timer = Components.label(Text.literal("akebloh"));

    public CustomHud() {
        createHud();
    }

    private void createHud() {
        Window window = MinecraftClient.getInstance().getWindow();

        // manhunt mode status
        Hud.add(new Identifier(Main.MOD_ID, "mode_status"), () -> Components.label(Text.literal(""))
                .positioning(Positioning.absolute(10, 10))
        );

        // timer (hidden by default)
        timerContainer = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(timer)
                .child(Components.item(new ItemStack(Items.CLOCK))
                        .sizing(Sizing.fixed(32)))
                .gap(4)
                .verticalAlignment(VerticalAlignment.CENTER)
                .positioning(Positioning.relative(97, 250));
        Hud.add(new Identifier(Main.MOD_ID, "release_timeout"), () -> timerContainer);

        // compass
        CompassHud compass = new CompassHud();
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            // DEBUG
            assert client.player != null;
            var playerPos = client.player.getPos(); // PLAYER
            var targetPos = new Vec3d(-31, 70, 82); // TARGET

            double d = targetPos.x - playerPos.x;
            double f = targetPos.z - playerPos.z;


            // waypoint
            compass.updatePos((int) MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F));
            // yaw marker
            //compass.updateYawMarker((int) normalizeYaw(client.player.getYaw()));
        });
        Hud.add(new Identifier(Main.MOD_ID, "compass"), compass::getRoot);
    }

    private float normalizeYaw(float yaw) {
        // Ensure yaw is within the range -180 to 180
        while (yaw < -180) {
            yaw += 360;
        }
        while (yaw > 180) {
            yaw -= 360;
        }
        return yaw;
    }

    public void setManhuntMode(boolean manhuntMode) {
        isManhuntMode = manhuntMode;
        LabelComponent modeStatus = (LabelComponent) Hud.getComponent(new Identifier(Main.MOD_ID, "mode_status"));
        assert modeStatus != null;
        modeStatus.text(Text.literal("Manhunt mode"));
    }

    // show player's role
    public void showRole(ManhuntManager.PlayerRole role) {
        switch (role) {
            case RUNNER -> Hud.add(new Identifier(Main.MOD_ID, "role_showup"),
                    () -> Containers.verticalFlow(Sizing.content(), Sizing.content())
                            .child(Components.label(Text.literal("You are " + Formatting.AQUA + Formatting.BOLD + "RUNNER")))
                            .child(Components.label(Text.literal("Your objective is to kill the Ender Dragon before the hunters catch you."))
                                    .sizing(Sizing.fixed(150), Sizing.content()))
                            .gap(12)
                            .positioning(Positioning.relative(3, 50)));
            case HUNTER -> Hud.add(new Identifier(Main.MOD_ID, "role_showup"),
                    () -> Containers.verticalFlow(Sizing.content(), Sizing.content())
                            .child(Components.label(Text.literal("You are " + Formatting.RED + Formatting.BOLD + "HUNTER")))
                            .child(Components.label(Text.literal("Your objective is to kill all runners before they kill the Ender Dragon."))
                                    .sizing(Sizing.fixed(150), Sizing.content()))
                            .gap(12)
                            .positioning(Positioning.relative(3, 50)));
        }

        delayedExecutor(15, TimeUnit.SECONDS)
                .execute(() -> Objects.requireNonNull(Hud.getComponent(new Identifier(Main.MOD_ID, "role_showup"))).positioning()
                        .animate(2000, Easing.SINE, Positioning.relative(-200, 50)).forwards());
    }

    // hunter's freedom timer
    // update time
    public void updateTime(int time) {
        timerContainer.positioning(Positioning.relative(97, 97));
        if (time > 1) {
            timer.text(Text.literal(time + " seconds\nfor preparation"));
        } else if (time == 0) {
            timer.text(Text.literal(Formatting.RED.toString() + time + " seconds\nfor preparation"));
        } else {
            timer.text(Text.literal(time + " second\nfor preparation"));
        }
        if (TICK_TIME.contains(time)) {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1F, 1F);
        }
    }

    // end timer
    public void endTimer() {
        InGameHud hud = MinecraftClient.getInstance().inGameHud;
        switch (MainClient.PLAYER_ROLE) {
            case RUNNER ->
                    hud.setOverlayMessage(Text.literal(Formatting.BOLD.toString() + Formatting.RED + "Hunters have been released!"), false);
            case HUNTER ->
                    hud.setOverlayMessage(Text.literal(Formatting.BOLD.toString() + Formatting.YELLOW + "You've been released!"), false);
        }


        delayedExecutor(3, TimeUnit.SECONDS)
                .execute(() -> Objects.requireNonNull(Hud.getComponent(new Identifier(Main.MOD_ID, "release_timeout")))
                        .positioning().animate(8000, Easing.LINEAR, Positioning.relative(97, 150)).forwards());
    }

}

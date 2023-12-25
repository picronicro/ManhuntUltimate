package net.picro.screens;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.GameMode;
import net.picro.Main;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CustomDeathScreen extends BaseOwoScreen<FlowLayout> {

    private final boolean isFinal;

    public CustomDeathScreen(boolean isFinal) {
        this.isFinal = isFinal;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var base = rootComponent
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);
        base.mouseDown().subscribe((mouseX, mouseY, button) -> {
            if (button == 0) {
                // TODO: короче, хуйня полная. лучше отправлять пакет на сервер и там же эту залупу обрабатывать.
                /*// respawn
                if (player.getSpawnPointPosition() != null) { // if spawnpoint exists
                    BlockPos pos = player.getSpawnPointPosition();
                    player.teleport(pos.getX(), pos.getY(), pos.getZ());
                } else { // or use world spawn
                    BlockPos pos = player.getServerWorld().getSpawnPos();
                    player.teleport(pos.getX(), pos.getY(), pos.getZ());
                }
                player.changeGameMode(GameMode.SURVIVAL);*/
            }
            return true;
        });

        var footer = Containers.horizontalFlow(Sizing.fill(), Sizing.fill(10))
                .child(Components.label(Text.literal("Press LMB to respawn.")))
                .surface(Surface.flat(ColorHelper.Argb.getArgb(150, 0, 0, 0)));
        footer.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        footer.positioning(Positioning.relative(0, 115));
        footer.positioning().animate(250, Easing.LINEAR, Positioning.relative(0, 100)).forwards();
        rootComponent.child(footer);

        var header = Containers.horizontalFlow(Sizing.fill(), Sizing.fill(10))
                .surface(Surface.flat(ColorHelper.Argb.getArgb(150, 0, 0, 0)));
        header.positioning(Positioning.relative(0, -15));
        header.positioning().animate(250, Easing.LINEAR, Positioning.relative(0, 0)).forwards();
        rootComponent.child(header);

        MinecraftClient.getInstance().player.setPitch(40);
        MinecraftClient.getInstance().options.setPerspective(Perspective.THIRD_PERSON_BACK);
    }

}

package net.picro.screens;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.picro.Main;
import net.picro.ManhuntManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AddPlayersScreen extends BaseOwoScreen<FlowLayout> {

    // are runners
    private final boolean areRunners;

    // list of players
    private final Set<ServerPlayerEntity> players = new HashSet<>();

    public AddPlayersScreen(boolean areRunners, Set<ServerPlayerEntity> players) {
        this.areRunners = areRunners;
        this.players.addAll(players);
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.blur(8, 6))
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        // add button
        ButtonComponent btn_add = Components.button(Text.literal("Add " + (areRunners ? "runners" : "hunters")), buttonComponent -> {
            // add players to the manhuntManager
            if (areRunners) {
                Main.manhuntManager.setRunners(players);
            } else {
                Main.manhuntManager.setHunters(players);
            }

            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
            MinecraftClient.getInstance().setScreen(new RunConfigScreen());
        });
        btn_add.active(!players.isEmpty()).margins(Insets.top(4));

        ArrayList<Component> widgets = new ArrayList<>();
        for (ServerPlayerEntity player : PlayerLookup.all(Objects.requireNonNull(MinecraftClient.getInstance().getServer()))) {
            var checkbox = Components.checkbox(Text.of("")).onChanged(aBoolean -> {
                if (aBoolean) {
                    players.add(player);
                } else {
                    players.remove(player);
                }

                // check that list is empty
                btn_add.active(!players.isEmpty());
            }).checked(players.contains(player));
            var nickname = Components.label(player.getName());

            // single container
            widgets.add(Containers
                    .horizontalFlow(Sizing.content(), Sizing.content())
                    .child(checkbox)
                    .child(nickname)
                    .verticalAlignment(VerticalAlignment.CENTER)
                    .padding(Insets.bottom(4))
            );
        }

        // container of single containers
        var playersContainer = Containers
                .verticalFlow(Sizing.content(), Sizing.content())
                .children(widgets)
                .padding(Insets.of(10));

        // scrollable container of playerContainer
        ScrollContainer playersScrollable = (ScrollContainer) Containers.verticalScroll(Sizing.fixed(200), Sizing.fill(60), playersContainer).surface(Surface.DARK_PANEL);
        playersScrollable.scrollbar(ScrollContainer.Scrollbar.flat(Color.WHITE));

        rootComponent.child(
                Containers
                        .verticalFlow(Sizing.content(), Sizing.content())
                        .child(playersScrollable)
                        .child(btn_add)
                        .padding(Insets.of(10))
                        .surface(Surface.PANEL)
        );
    }

}

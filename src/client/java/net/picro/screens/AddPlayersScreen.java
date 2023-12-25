package net.picro.screens;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.DropdownComponent;
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
import java.util.concurrent.Flow;

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
            if (areRunners) Main.manhuntManager.setRunners(players);
            else Main.manhuntManager.setHunters(players);

            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
            MinecraftClient.getInstance().setScreen(new RunConfigScreen());
        });
        btn_add.active(!players.isEmpty());

        // clear button
        ButtonComponent btn_clear = Components.button(Text.literal("⏻"), buttonComponent -> {
            if (areRunners) Main.manhuntManager.setRunners(new HashSet<>());
            else Main.manhuntManager.setHunters(new HashSet<>());

            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1F, 1F);
            MinecraftClient.getInstance().setScreen(new RunConfigScreen());
        });
        btn_clear.tooltip(Text.literal("Clear list"));
        btn_clear.margins(Insets.left(4));

        ArrayList<Component> widgets = new ArrayList<>();
        for (ServerPlayerEntity player : PlayerLookup.all(Objects.requireNonNull(MinecraftClient.getInstance().getServer()))) {
            System.out.println(player.getPos());
            // I should check that player can't be both runners and hunter
            boolean isOpposite = false; // if player in an opposite side, don't include him
            String message;
            if (areRunners) {
                message = " is hunter";
                if (Main.manhuntManager.getHunters().contains(player)) isOpposite = true;
            } else {
                message = " is runner";
                if (Main.manhuntManager.getRunners().contains(player)) isOpposite = true;
            }

            // add player
            ArrayList<Component> children = new ArrayList<>();
            if (!isOpposite) {
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

                children.add(checkbox);
                children.add(nickname);
            } else {
                var nickname = Components.label(Text.literal(player.getName().getString() + message));
                children.add(nickname);
            }

            widgets.add(Containers
                    .horizontalFlow(Sizing.content(), Sizing.content())
                    .children(children)
                    .verticalAlignment(VerticalAlignment.CENTER)
                    .padding(Insets.bottom(4)));
        }

        // container of single containers
        var playersContainer = Containers
                .verticalFlow(Sizing.content(), Sizing.content())
                .children(widgets)
                .padding(Insets.of(10));

        // scrollable container of playerContainer
        ScrollContainer playersScrollable = (ScrollContainer) Containers.verticalScroll(Sizing.fixed(200), Sizing.fill(60), playersContainer).surface(Surface.DARK_PANEL);
        playersScrollable.scrollbar(ScrollContainer.Scrollbar.flat(Color.WHITE));

        var base = Containers
                .verticalFlow(Sizing.content(), Sizing.content())
                .child(playersScrollable)
                .child(Containers.horizontalFlow(Sizing.content(), Sizing.content()).child(btn_add).child(btn_clear).margins(Insets.top(4)))
                .padding(Insets.of(10))
                .surface(Surface.PANEL)
                .id("test_asdf");
        var test = rootComponent.child(base);

        // back button (as a dropdown)
        DropdownComponent btn_back = Components.dropdown(Sizing.content())
                .button(Text.literal("< Back"), dropdownComponent -> {
                    MinecraftClient.getInstance().setScreen(new RunConfigScreen());
                });
        btn_back.positioning(Positioning.absolute(20, 20));
        rootComponent.child(btn_back);
    }

}

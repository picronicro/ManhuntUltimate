package net.picro.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.picro.Main;
import net.picro.ManhuntManager;

import java.util.stream.Collectors;

public class RunConfigScreen extends BaseUIModelScreen<FlowLayout> {

    public RunConfigScreen() {
        super(FlowLayout.class, DataSource.asset(new Identifier(Main.MOD_ID, "run_config")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.blur(8, 6));

        // DEBUG
        rootComponent.child(Components.button(Text.literal("list"), buttonComponent -> {
            Main.manhuntManager.listPlayers();
        }));

        // buttons
        // add runners
        rootComponent.childById(ButtonComponent.class, "btn_add_runners").onPress(buttonComponent -> {
            MinecraftClient.getInstance().setScreen(new AddPlayersScreen(true, Main.manhuntManager.getRunners()));
        });

        // add hunters
        rootComponent.childById(ButtonComponent.class, "btn_add_hunters").onPress(buttonComponent -> {
            MinecraftClient.getInstance().setScreen(new AddPlayersScreen(false, Main.manhuntManager.getHunters()));
        });

        ManhuntManager manhuntManager = Main.manhuntManager;
        // labels
        // runners
        rootComponent.childById(LabelComponent.class, "label_runners")
                .text(Text.of(manhuntManager.getRunners().isEmpty() ? "Add runners" :
                        manhuntManager.getRunners().stream()
                                .map(runner -> String.valueOf(runner.getName().getString()))
                                .collect(Collectors.joining(", "))));

        // hunters
        rootComponent.childById(LabelComponent.class, "label_hunters")
                .text(Text.of(manhuntManager.getHunters().isEmpty() ? "Add hunters" :
                        manhuntManager.getHunters().stream()
                                .map(hunter -> String.valueOf(hunter.getName().getString()))
                                .collect(Collectors.joining(", "))));
    }

}

package net.picro.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.picro.Main;
import net.picro.ManhuntManager;
import net.picro.ManhuntManager.StartModeEnum;
import net.picro.ManhuntSession;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RunConfigScreen extends BaseUIModelScreen<FlowLayout> {

    public RunConfigScreen() {
        super(FlowLayout.class, DataSource.asset(new Identifier(Main.MOD_ID, "run_config")));
    }

    // timeout
    private int timeout = 60;
    private boolean isIncorrect = false;

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.blur(8, 6));

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

        // start mode selection dropdown
        rootComponent.childById(ButtonComponent.class, "btn_start_mode").onPress(btn -> {
            // dropdown start mode select menu

            DropdownComponent dropdown = Components.dropdown(Sizing.content())
                    .text(Text.literal("Start run by..."))
                    .button(Text.literal(StartModeEnum.TIMEOUT.name), dropdownComponent -> {
                        Main.manhuntManager.setStartMode(StartModeEnum.TIMEOUT);
                        MinecraftClient.getInstance().setScreen(new RunConfigScreen());
                    })
                    .button(Text.literal(StartModeEnum.PUNCH.name), dropdownComponent -> {
                        Main.manhuntManager.setStartMode(StartModeEnum.PUNCH);
                        MinecraftClient.getInstance().setScreen(new RunConfigScreen());
                    });
            dropdown.positioning(Positioning.absolute(btn.x() + 22, btn.y()));
            dropdown.zIndex(999); // fixes text overlapping

            rootComponent.child(dropdown);
        });

        // start mode label
        rootComponent.childById(LabelComponent.class, "label_start_mode")
                .text(Text.literal("Start run by " + Main.manhuntManager.getStartMode().name + Formatting.BLUE + " ℹ"))
                .tooltip(Text.literal(Main.manhuntManager.getStartMode().desc));

        // startMode specified
        // timeout textbox
        rootComponent.childById(TextBoxComponent.class, "textbox_timeout").text(String.valueOf(timeout));
        rootComponent.childById(TextBoxComponent.class, "textbox_timeout").onChanged().subscribe(value -> {
            try {
                timeout = Integer.parseInt(value);
                manhuntManager.setTimeout(timeout);
                isIncorrect = false;
            } catch (NumberFormatException e) {
                isIncorrect = true;
            }
        });

        // THE FINAL ONES
        // START button
        ButtonComponent btnStart = rootComponent.childById(ButtonComponent.class, "btn_start").onPress(buttonComponent -> {
            manhuntManager.startRun();
            this.close();
        });
        boolean areRunnersEmpty = Main.manhuntManager.getRunners().isEmpty();
        boolean areHuntersEmpty = Main.manhuntManager.getHunters().isEmpty();
        btnStart.active(!areRunnersEmpty && !areHuntersEmpty);

        // status
        StringJoiner warningJoiner = getStringJoiner(areRunnersEmpty, areHuntersEmpty);
        LabelComponent req = Components.label(Text.literal(warningJoiner.toString()));
        req.horizontalTextAlignment();
        req.margins(Insets.top(10));
        rootComponent.child(req);
    }

    @NotNull
    private StringJoiner getStringJoiner(boolean areRunnersEmpty, boolean areHuntersEmpty) {
        StringJoiner warningJoiner = new StringJoiner("\n");
        if (Main.manhuntManager.getStartMode() == StartModeEnum.TIMEOUT && timeout <= 0)
            warningJoiner.add("❌ timeout must be more than zero!");
        if (Main.manhuntManager.getStartMode() == StartModeEnum.TIMEOUT && isIncorrect)
            warningJoiner.add("❌ incorrect timeout value!");
        if (areRunnersEmpty) warningJoiner.add("❌ Runners list is empty! Add at least one player.");
        if (areHuntersEmpty) warningJoiner.add("❌ Hunters list is empty! Add at least one player.");
        if (!areRunnersEmpty && !areHuntersEmpty)
            warningJoiner.add("Before starting the game, make sure, that you have enough space for players.");
        return warningJoiner;
    }

}

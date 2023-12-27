package net.picro.screens;

import com.sun.jna.platform.win32.Winspool;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.picro.ManhuntManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GameOverScreen extends BaseOwoScreen<FlowLayout> {

    private final ManhuntManager.PlayerRole winnerRole;

    private FlowLayout footer;
    private FlowLayout header;
    // stat components
    private List<Component> stats;

    public GameOverScreen(ManhuntManager.PlayerRole winner) {
        this.winnerRole = winner;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var base = rootComponent
                .surface(Surface.flat(ColorHelper.Argb.getArgb(150, 0, 0, 0)))
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        // stats
        var timeStat = Components.label(Text.literal("Time: 0:00")).sizing(Sizing.fixed(0));
        var killStat = Components.label(Text.literal("Kills: 0")).sizing(Sizing.fixed(0));
        var blockStat = Components.label(Text.literal("Meters passed: 0m")).sizing(Sizing.fixed(0));
        var winner = Components.label(Text.literal(winnerRole.name() + "S WIN")).sizing(Sizing.fixed(0));

        stats = Arrays.asList(timeStat, killStat, blockStat, winner);

        var statContainer = Containers.verticalFlow(Sizing.content(), Sizing.content())
                .children(stats)
                .gap(4);

        rootComponent.child(statContainer);

        // footer & header
        footer = Containers.horizontalFlow(Sizing.fill(), Sizing.fill(10));
        footer.surface(Surface.flat(ColorHelper.Argb.getArgb(150, 0, 0, 0)));
        footer.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        footer.positioning(Positioning.relative(0, 115));
        rootComponent.child(footer);

        header = Containers.horizontalFlow(Sizing.fill(), Sizing.fill(10));
        header.surface(Surface.flat(ColorHelper.Argb.getArgb(150, 0, 0, 0)));
        header.positioning(Positioning.relative(0, -15));
        rootComponent.child(header);

        runTransition();
    }

    // transition
    private void runTransition() {
        CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS).execute(() -> {
            // footer/header transition
            footer.positioning().animate(250, Easing.LINEAR, Positioning.relative(0, 100)).forwards();
            header.positioning().animate(250, Easing.LINEAR, Positioning.relative(0, 0)).forwards();

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int index = 0;

                @Override
                public void run() {
                    stats.get(index).sizing(Sizing.content());
                    index++;
                    if (index == 4) {
                        footer.child(Components.label(Text.literal("Waiting for host...")));
                        this.cancel();
                    } else {
                        // play sound
                    }
                }
            }, 0, 500);
        });
    }

}

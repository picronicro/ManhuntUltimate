package net.picro.hud;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.HudContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Timer;
import java.util.TimerTask;

public class CompassHud {

    // client
    private final MinecraftClient client = MinecraftClient.getInstance();

    // root layout
    private final FlowLayout root;

    // status text
    private LabelComponent yawValue;
    private LabelComponent directionValue;

    // skull marker
    private ItemComponent marker;
    private LabelComponent yawMarker;

    public CompassHud() {
        root = Containers.verticalFlow(Sizing.fill(), Sizing.fill());
        root.positioning(Positioning.absolute(0, 0));
        root.horizontalAlignment(HorizontalAlignment.CENTER);
        build();

        // loop
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // update yaw marker
                updateYawMarker((int) normalizeYaw(client.player.getYaw()));

                // update text status
                yawValue.text(Text.literal(String.valueOf((int) normalizeYaw(client.player.getYaw()))));
                directionValue.text(Text.literal(client.player.getHorizontalFacing().getName()));
            }
        }, 0, 1);
    }

    private void build() {
        // bg
        var box = Containers.horizontalFlow(Sizing.fixed(195), Sizing.fixed(16));
        box.surface(Surface.VANILLA_TRANSLUCENT);
        box.margins(Insets.top(10));
        root.child(box);

        // text box with
        yawValue = Components.label(Text.literal("0"));
        yawValue.horizontalSizing(Sizing.fixed(30));
        yawValue.horizontalTextAlignment(HorizontalAlignment.RIGHT);
        yawValue.shadow(true);

        directionValue = Components.label(Text.literal("Direction"));
        directionValue.horizontalSizing(Sizing.fixed(40));
        directionValue.horizontalTextAlignment(HorizontalAlignment.LEFT);
        directionValue.shadow(true);

        var textContainer = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        textContainer.gap(5);
        textContainer.margins(Insets.top(5));
        textContainer.child(directionValue);
        textContainer.child(yawValue);
        root.child(textContainer);

        // debug
        ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
        NbtCompound nbt = new NbtCompound();
        nbt.putString("SkullOwner", "picronicro");
        skull.setNbt(nbt);

        marker = Components.item(skull);

        root.child(marker);

        // yaw marker
        yawMarker = Components.label(Text.literal("▼").formatted(Formatting.RED));
        yawMarker.horizontalTextAlignment();
        yawMarker.zIndex(1000);
        root.child(yawMarker);
    }

    public void updatePos(int yaw) {
        int width = root.width() / 2;
        marker.positioning(Positioning.absolute((yaw / 2) + width - 8, 10));
    }

    public void updateYawMarker(int yaw) {
        int width = root.width() / 2;
        yawMarker.positioning(Positioning.absolute((yaw / 2) + width - 3, 10));
    }

    // misc
    // yaw in client goes more/less than [-180, 180] for some reason
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

    public FlowLayout getRoot() {
        return root;
    }
}

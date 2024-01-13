package net.picro.hud;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class CompassHud {

    // client
    private final MinecraftClient client = MinecraftClient.getInstance();

    // is showed up (triggers once)
    private boolean isShowedUp = false;

    // root layout
    private final FlowLayout root;
    // status text
    private LabelComponent yawValue;
    private LabelComponent directionValue;
    // yaw marker
    private LabelComponent yawMarker;
    // runners
    private HashMap<String, ItemComponent> runnerMarkers = new HashMap<>();
    private HashMap<String, Vec3d> runnerPositions = new HashMap<>();

    public CompassHud() {
        root = Containers.verticalFlow(Sizing.fill(), Sizing.fill());
        root.positioning(Positioning.absolute(0, -40));
        root.horizontalAlignment(HorizontalAlignment.CENTER);
        build();

        // loop
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // dropdown animation when runners map is not empty
                if (!isShowedUp && !runnerMarkers.isEmpty()) {
                    isShowedUp = true;
                    root.positioning().animate(1500, Easing.SINE, Positioning.absolute(0, 0)).forwards();
                }

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

        // yaw marker
        yawMarker = Components.label(Text.literal("▼").formatted(Formatting.RED));
        yawMarker.horizontalTextAlignment();
        yawMarker.zIndex(1000);
        root.child(yawMarker);
    }

    // initial setup, all hunters have to get first packet with all runners
    public void addRunner(String name) {
        ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
        NbtCompound nbt = new NbtCompound();
        nbt.putString("SkullOwner", name);
        skull.setNbt(nbt);
        var marker = Components.item(skull);
        root.child(marker);

        runnerMarkers.put(name, marker);
    }

    // if runner died
    public void removeRunner(String name) {
        runnerMarkers.get(name).remove();
        runnerMarkers.remove(name);
    }

    public void updateRunnerPos(String name, Vec3d targetPos) {
        if (runnerMarkers.containsKey(name)) {
            // calculate yaw for runner marker
            var playerPos = client.player.getPos(); // PLAYER

            double d = targetPos.x - playerPos.x;
            double f = targetPos.z - playerPos.z;

            int yaw = (int) MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F);

            // and then set its positioning
            int width = root.width() / 2;
            runnerMarkers.get(name).positioning(Positioning.absolute((yaw / 2) + width - 8, 10));
        }
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

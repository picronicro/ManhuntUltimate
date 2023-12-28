package net.picro.hud;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PlayerHeadItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CompassHud {

    private final FlowLayout root;

    // skull marker
    private ItemComponent marker;
    private LabelComponent yawMarker;


    public CompassHud() {
        root = Containers.verticalFlow(Sizing.fill(), Sizing.fill());
        root.positioning(Positioning.absolute(0, 0));
        draw();
    }

    private void draw() {
        ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
        NbtCompound nbt = new NbtCompound();
        nbt.putString("SkullOwner", "picronicro");
        skull.setNbt(nbt);

        marker = Components.item(skull);

        root.child(marker);

        // yaw marker
        yawMarker = Components.label(Text.literal("V").formatted(Formatting.RED));
        yawMarker.horizontalTextAlignment();
        yawMarker.zIndex(1000);
        root.child(yawMarker);
    }

    public void updatePos(int yaw) {
        int width = root.width() / 2;
        marker.positioning(Positioning.absolute((yaw / 2) + width, 10));
    }

    public void updateYawMarker(int yaw) {
        int width = root.width() / 2;
        yawMarker.positioning(Positioning.absolute((yaw / 2) + width, 15));
    }

    public FlowLayout getRoot() {
        return root;
    }

}

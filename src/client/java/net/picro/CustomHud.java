package net.picro;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.thread.ThreadExecutor;

public class CustomHud implements HudRenderCallback {

    private boolean isManhuntMode = false;

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        if (isManhuntMode) {
            drawContext.drawText(MinecraftClient.getInstance().textRenderer, "Manhunt mode", 10, 10,
                    ColorHelper.Argb.getArgb(255, 20, 186, 26), true);
        }
    }

    public void setManhuntMode(boolean manhuntMode) {
        isManhuntMode = manhuntMode;
    }
}

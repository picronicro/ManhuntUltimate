package net.picro.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.picro.Main;
import net.picro.screens.RunConfigScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuMixin extends Screen {

    protected GameMenuMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("HEAD"), method = "initWidgets")
    private void addEnableMUButton(CallbackInfo ci) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // mode status
        TextWidget status = new TextWidget(Text.of("Manhunt mode is " + (Main.isManhuntActivated ? Formatting.GREEN + "enabled" : Formatting.RED + "disabled")), textRenderer);
        status.alignLeft();
        status.setPosition(10, 10);
        addDrawableChild(status);

        // manhunt menu
        if (Main.isManhuntActivated) {
            if (Main.manhuntManager.getHost().getUuid() == MinecraftClient.getInstance().player.getUuid()) {
                addDrawableChild(new ButtonWidget.Builder(Text.of("Configure run"), button -> {
                    MinecraftClient.getInstance().setScreen(new RunConfigScreen());
                }).dimensions(10, 24, 50, 20).build());
            }
        }
    }

}

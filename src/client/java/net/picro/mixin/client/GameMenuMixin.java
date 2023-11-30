package net.picro.mixin.client;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.picro.Main;
import net.picro.MainClient;
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
        addDrawableChild(new ButtonWidget.Builder(Text.of(String.valueOf(Main.isManhuntActivated)), button -> {
            button.setMessage(Text.of(String.valueOf(Main.isManhuntActivated)));

            Main.isManhuntActivated = true;
        }).dimensions(10, 10, 40, 40).build());
    }

}

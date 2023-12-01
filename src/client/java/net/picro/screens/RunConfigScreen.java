package net.picro.screens;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.picro.Main;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RunConfigScreen extends BaseUIModelScreen<FlowLayout> {

    protected RunConfigScreen(Class<FlowLayout> rootComponentClass, DataSource source) {
        super(rootComponentClass, DataSource.asset(new Identifier(Main.MOD_ID, "run_config")));
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        // background
        rootComponent
                .surface(Surface.blur(8, 4))
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        // layout
        ArrayList<Component> components = new ArrayList<>();

        var title = Components.label(Text.of("Configuration"));
        components.add(title);

        var btn = Components.button(Text.literal("lolkek"), buttonComponent -> {
            buttonComponent.sizing(Sizing.fill(80));
        });
        components.add(btn);

        // background layout
        rootComponent.child(
                Containers
                        .verticalFlow(Sizing.content(), Sizing.content())
                        .children(components)
                        .padding(Insets.of(10))
                        .surface(Surface.DARK_PANEL)
                        .verticalAlignment(VerticalAlignment.CENTER)
                        .horizontalAlignment(HorizontalAlignment.CENTER)

        );
    }

}

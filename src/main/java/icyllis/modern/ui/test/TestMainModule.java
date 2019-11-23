package icyllis.modern.ui.test;

import icyllis.modern.api.module.IModernModule;
import icyllis.modern.api.internal.IElementBuilder;
import icyllis.modern.core.ModernUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.text.DecimalFormat;

public class TestMainModule implements IModernModule {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(ModernUI.MODID, "textures/gui/gui_default_background.png");
    public static final ResourceLocation FRAME = new ResourceLocation(ModernUI.MODID, "textures/gui/gui_default_frame.png");

    @Override
    public void createElements(IElementBuilder builder) {
        DecimalFormat df = new DecimalFormat("0.00");
        builder.defaultBackground();
        builder.texture()
                .tex(BACKGROUND)
                .pos(-128, -128)
                .uv(0, 0)
                .size(256, 256);
        builder.texture()
                .tex(FRAME)
                .pos(-128, -128)
                .uv(0, 0)
                .size(256, 256)
                .color(() -> 0xeedc82);
        builder.textLine()
                .text(() -> "Snownee likes to eat :0000::0100: Malay died")
                .pos(-50, -60, true);
        builder.textLine()
                .text(() -> ":0000::0100::0000::0100::0000::0100::0000::0100:")
                .pos(-50, -40, true);
        /*builder.textLine()
                .text(() -> "World partial ticks: " + df.format(Minecraft.getInstance().getRenderPartialTicks()))
                .pos(0, -20, true);*/

    }

}

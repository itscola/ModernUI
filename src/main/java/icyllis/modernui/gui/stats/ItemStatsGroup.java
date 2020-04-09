/*
 * Modern UI.
 * Copyright (C) 2019 BloCamLimb. All rights reserved.
 *
 * Modern UI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Modern UI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Modern UI. If not, see <https://www.gnu.org/licenses/>.
 */

package icyllis.modernui.gui.stats;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import icyllis.modernui.font.TextAlign;
import icyllis.modernui.gui.master.DrawTools;
import icyllis.modernui.gui.scroll.ScrollWindow;
import icyllis.modernui.gui.scroll.UniformScrollGroup;
import icyllis.modernui.gui.util.Color3I;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.stats.Stats;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.Set;

/**
 * Item/Block to variable type values statistics tab, can be ordered
 */
public class ItemStatsGroup extends UniformScrollGroup<ItemStatsEntry> {

    public static int ENTRY_HEIGHT = 20;

    private final TextureManager textureManager = Minecraft.getInstance().getTextureManager();

    private final int[] uMap;

    public ItemStatsGroup(ScrollWindow<?> window, Type type) {
        super(window, ENTRY_HEIGHT);

        Set<Item> set = Sets.newIdentityHashSet();
        // if someone breaks the vanilla rules, he should be fucked away
        if (type == Type.ITEMS) {
            Stats.ITEM_BROKEN.forEach(e -> {
                if (!(e.getValue() instanceof BlockItem)) {
                    set.add(e.getValue());
                }
            });
            Stats.ITEM_CRAFTED.forEach(e -> {
                if (!(e.getValue() instanceof BlockItem)) {
                    set.add(e.getValue());
                }
            });
            Stats.ITEM_USED.forEach(e -> {
                if (!(e.getValue() instanceof BlockItem)) {
                    set.add(e.getValue());
                }
            });
            Stats.ITEM_PICKED_UP.forEach(e -> {
                if (!(e.getValue() instanceof BlockItem)) {
                    set.add(e.getValue());
                }
            });
            Stats.ITEM_DROPPED.forEach(e -> {
                if (!(e.getValue() instanceof BlockItem)) {
                    set.add(e.getValue());
                }
            });
            uMap = new int[]{4, 1, 2, 5, 6};
        } else {
            Stats.BLOCK_MINED.forEach(e -> set.add(e.getValue().asItem()));
            Stats.ITEM_CRAFTED.forEach(e -> {
                if (e.getValue() instanceof BlockItem) {
                    set.add(e.getValue());
                }
            });
            Stats.ITEM_USED.forEach(e -> {
                if (e.getValue() instanceof BlockItem) {
                    set.add(e.getValue());
                }
            });
            Stats.ITEM_PICKED_UP.forEach(e -> {
                if (e.getValue() instanceof BlockItem) {
                    set.add(e.getValue());
                }
            });
            Stats.ITEM_DROPPED.forEach(e -> {
                if (e.getValue() instanceof BlockItem) {
                    set.add(e.getValue());
                }
            });
            uMap = new int[]{3, 1, 2, 5, 6};
        }

        set.remove(Items.AIR);
        int i = 0;
        for (Item item : set) {
            entries.add(new ItemStatsEntry(item, (i & 1) == 0 ? Color3I.WHILE : Color3I.GRAY));
            i++;
        }

        // 20 for labels header
        height = 20 + entries.size() * entryHeight;
    }

    @Override
    public void layout(float x1, float x2, float y) {
        super.layout(x1, x2, y);
        x1 = centerX - 144;
        x2 = centerX + 144;
        y += 20;
        int i = 0;
        for (ItemStatsEntry entry : entries) {
            float cy = y + i * entryHeight;
            entry.layout(x1, x2, cy);
            i++;
        }
    }

    public void updateValues(StatisticsManager manager) {
        entries.forEach(e -> e.updateValue(manager));
    }

    @Override
    public void draw(float time) {
        super.draw(time);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        textureManager.bindTexture(AbstractGui.STATS_ICON_LOCATION);
        GlStateManager.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GlStateManager.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        int i = 0;
        float x3 = centerX - 80;
        for (int c : uMap) {
            DrawTools.blit(x3 + i * 50, y1 + 1, c * 18, 18, 18, 18, 128, 128);
            i++;
        }
    }

    public enum Type {
        BLOCKS,
        ITEMS
    }
}
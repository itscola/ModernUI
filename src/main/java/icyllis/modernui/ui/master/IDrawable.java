/*
 * Modern UI.
 * Copyright (C) 2019 BloCamLimb. All rights reserved.
 *
 * Modern UI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * 3.0 any later version.
 *
 * Modern UI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Modern UI. If not, see <https://www.gnu.org/licenses/>.
 */

package icyllis.modernui.ui.master;

import icyllis.modernui.graphics.renderer.Canvas;

import javax.annotation.Nonnull;

/**
 * This is an really basic interface that represents a drawable element in gui
 * But also can be used for update animations.
 * And can also listen resize and tick events from current element
 */
public interface IDrawable {

    /**
     * Draw content you want, called every frame
     * You have to do animations update at the top of lines if needed
     *
     * @param canvas canvas to draw content
     * @param time   elapsed time from a gui open
     *               unit: floating point ticks, 20.0 ticks = 1 second
     */
    void draw(@Nonnull Canvas canvas, float time);

    /**
     * Called when game window size changed, used to reset position, use layout for multiple elements
     *
     * @param width  scaled game window width
     * @param height scaled game window height
     */
    default void resize(int width, int height) {
    }

    /**
     * Ticks something you like, used by % calculation to update gui values or state
     *
     * @param ticks elapsed ticks from a gui open, 20 tick = 1 second
     */
    default void tick(int ticks) {
    }

}

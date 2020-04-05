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

package icyllis.modernui.gui.widget;

import icyllis.modernui.gui.master.IElement;
import icyllis.modernui.gui.master.IFocuser;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class SliderSmooth extends Slider implements IElement, IGuiEventListener {

    private Consumer<Double> receiver;

    public SliderSmooth(IFocuser focuser, float width, double initPercent, Consumer<Double> receiver) {
        super(focuser, width);
        this.slideOffset = getMaxSlideOffset() * MathHelper.clamp(initPercent, 0, 1);
        this.receiver = receiver;
    }

    @Override
    protected void onStopChanging() {

    }

    @Override
    protected void slideToOffset(float offset) {
        double prev = slideOffset;
        slideOffset = MathHelper.clamp(offset, 0, getMaxSlideOffset());
        if (prev != slideOffset) {
            double slidePercent = slideOffset / getMaxSlideOffset();
            receiver.accept(slidePercent);
        }
    }
}

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

package icyllis.modernui.ui.animation;

import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Record values and apply changes with interpolator
 */
public class Applier {

    private final float startValue;
    private final float endValue;

    private float logicStart;
    private float logicEnd;

    private final Supplier<Float> getter;
    private final Consumer<Float> setter;

    @Nonnull
    private ITimeInterpolator interpolator = ITimeInterpolator.LINEAR;

    public Applier(float startValue, float endValue, Supplier<Float> getter, Consumer<Float> setter) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.getter = getter;
        this.setter = setter;
    }

    public Applier setInterpolator(@Nonnull ITimeInterpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    void record(boolean inverted, boolean isFull) {
        if (isFull) {
            if (inverted) {
                logicStart = endValue;
            } else {
                logicStart = startValue;
            }
        } else {
            logicStart = getter.get();
        }
        if (inverted) {
            logicEnd = startValue;
        } else {
            logicEnd = endValue;
        }
    }

    void update(float progress) {
        progress = interpolator.getInterpolation(progress);
        float value = MathHelper.lerp(progress, logicStart, logicEnd);
        setter.accept(value);
    }

    /*public static class Resizable extends Applier {

        private Function<Integer, Float> initResizer;

        private Function<Integer, Float> targetResizer;

        private Consumer<Function<Integer, Float>> resizerSetter;

        private boolean useHeight;

        public Resizable(Function<Integer, Float> initResizer, Function<Integer, Float> targetResizer, Consumer<Float> resultSetter, Consumer<Function<Integer, Float>> resizerSetter, boolean useHeight) {
            super(0, 0, resultSetter);
            this.initResizer = initResizer;
            this.targetResizer = targetResizer;
            this.resizerSetter = resizerSetter;
            this.useHeight = useHeight;
        }

        @Override
        public void resize(int width, int height) {
            initValue = initResizer.apply(useHeight ? height : width);
            targetValue = targetResizer.apply(useHeight ? height : width);
        }

        @Override
        public void apply(float progress) {
            super.apply(progress);
            if (progress == 1) {
                resizerSetter.accept(targetResizer);
            }
        }
    }*/
}

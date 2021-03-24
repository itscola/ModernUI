/*
 * Modern UI.
 * Copyright (C) 2019-2021 BloCamLimb. All rights reserved.
 *
 * Modern UI is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Modern UI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Modern UI. If not, see <https://www.gnu.org/licenses/>.
 */

package icyllis.modernui.graphics.font;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.util.List;
import java.util.function.BiConsumer;

public class MeasureEngine {

    private static MeasureEngine sInstance;

    private final GlyphManager mGlyphManager = GlyphManager.getInstance();

    public static MeasureEngine getInstance() {
        if (sInstance == null)
            synchronized (MeasureEngine.class) {
                if (sInstance == null)
                    sInstance = new MeasureEngine();
            }
        return sInstance;
    }

    public void create(@Nonnull char[] text, int contextStart, int contextEnd, @Nonnull MinikinPaint paint, boolean isRtl,
                       @Nonnull BiConsumer<GraphemeMetrics, MinikinPaint> consumer) {
        final List<FontCollection.Run> runs = paint.mFontCollection.itemize(text, contextStart, contextEnd);
        final int flag = isRtl ? Font.LAYOUT_RIGHT_TO_LEFT : Font.LAYOUT_LEFT_TO_RIGHT;
        final GlyphManager manager = mGlyphManager;
        float advance = 0;
        final FontMetricsInt fm = new FontMetricsInt();
        for (FontCollection.Run run : runs) {
            final Font derivedFont;
            synchronized (this) {
                derivedFont = manager.deriveFont(run.getFamily(), paint.mFontStyle, paint.mFontSize);
            }
            final GlyphVector vector = manager.layoutGlyphVector(derivedFont, text, run.getStart(), run.getEnd(), flag);
            final int num = vector.getNumGlyphs();
            advance += vector.getGlyphPosition(num).getX();
            manager.getFontMetrics(derivedFont, fm);
        }
        consumer.accept(new GraphemeMetrics(advance, fm), paint);
    }
}
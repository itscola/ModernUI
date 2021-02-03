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

package icyllis.modernui.text;

import it.unimi.dsi.fastutil.Arrays;

import javax.annotation.Nonnull;
import java.text.CharacterIterator;

public class CharArrayIterator implements CharacterIterator {

    private final char[] text;
    private final int start;
    private final int limit;

    private int pos;

    public CharArrayIterator(char[] text) {
        this(text, 0, text.length);
    }

    public CharArrayIterator(char[] text, int start) {
        this(text, start, text.length);
    }

    public CharArrayIterator(@Nonnull char[] text, int start, int limit) {
        Arrays.ensureFromTo(text.length, start, limit);
        this.text = text;
        this.start = start;
        this.limit = limit;
        pos = start;
    }

    @Override
    public char first() {
        pos = start;
        return current();
    }

    @Override
    public char last() {
        pos = limit != start ? limit - 1 : limit;
        return current();
    }

    @Override
    public char current() {
        return pos >= start && pos < limit ? text[pos] : DONE;
    }

    @Override
    public char next() {
        return pos < limit ? text[pos++] : DONE;
    }

    @Override
    public char previous() {
        return pos > start ? text[--pos] : DONE;
    }

    @Override
    public char setIndex(int position) {
        if (position < start || position > limit)
            throw new IllegalArgumentException();
        pos = position;
        return current();
    }

    @Override
    public int getBeginIndex() {
        return start;
    }

    @Override
    public int getEndIndex() {
        return limit;
    }

    @Override
    public int getIndex() {
        return pos;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
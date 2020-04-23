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

import icyllis.modernui.gui.master.Module;
import icyllis.modernui.system.ConstantsLibrary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class NumberInputField extends TextField {

    @Nullable
    private Consumer<NumberInputField> listener;

    private boolean allowNegative = true;

    public NumberInputField(Module module, float width, float height) {
        super(module, width, height);
        super.setText("0");
        super.setFilter((s, t) -> {
            if (ConstantsLibrary.DIGIT_FILTER.test(t)) {
                return t;
            } else {
                return s;
            }
        });
    }

    @Deprecated
    @Override
    public void setFilter(BiFunction<String, String, String> filter) {
        throw new RuntimeException();
    }

    @Deprecated
    @Override
    public void setMaxStringLength(int length) {
        throw new RuntimeException();
    }

    @Deprecated
    @Override
    public void setListener(@Nonnull Consumer<TextField> listener, boolean runtime) {
        throw new RuntimeException();
    }

    public void setNumberListener(Consumer<NumberInputField> listener, boolean runtime) {
        this.listener = listener;
        this.runtimeUpdate = runtime;
    }

    public void setLimit(long min, long max) {
        long cLimit = Math.max(Math.abs(min), Math.abs(max));
        if (cLimit == 0) {
            super.setMaxStringLength(1);
            // why?
        } else {
            int i = (int) Math.ceil(Math.log10(cLimit));
            allowNegative = min < 0;
            if (allowNegative) {
                super.setMaxStringLength(i + 2);
            } else {
                super.setMaxStringLength(i + 1);
            }
            super.setFilter((s, t) -> testValid(s, t, min, max));
        }
    }

    private String testValid(String previousText, @Nonnull String newText, long min, long max) {
        if (newText.startsWith("0")) {
            newText = newText.substring(1);
        }
        if (newText.equals("-0") || newText.equals("-")) {
            return "-";
        }
        if (newText.isEmpty()) {
            return "0";
        }
        if (ConstantsLibrary.INTEGER_FILTER.test(newText) || newText.equals("0")) {
            long n = Long.parseLong(newText);
            if (n < min || n > max) {
                if (n > max) {
                    return String.valueOf(max);
                } else {
                    return String.valueOf(min);
                }
            }
            return newText;
        }
        return previousText;
    }

    @Override
    protected void onTextChanged(boolean force) {
        boolean c = getText().equals("-");
        if (force && c) {
            setText("0");
            return;
        }
        if (listener != null && (runtimeUpdate || force)) {
            if (!c) {
                listener.accept(this);
                getLongFromText();
            }
        }
    }

    public int getIntegerFromText() {
        return Integer.parseInt(getText());
    }

    public long getLongFromText() {
        return Long.parseLong(getText());
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (codePoint == '-' && allowNegative) {
            if (!getText().contains("-")) {
                setText("-" + getText());
            } else {
                setText(getText().replace("-", ""));
            }
            return true;
        }
        if (codePoint == '+') {
            if (getText().contains("-")) {
                setText(getText().replace("-", ""));
            }
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }
}

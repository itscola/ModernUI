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

package icyllis.modernui.mcgui;

import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;

/**
 * Callback to handle user interface lifecycle events.
 */
public abstract class ScreenCallback {

    UIManager host;

    protected ScreenCallback() {
    }

    public abstract void onCreate();

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        host.setContentView(view, params);
    }
}
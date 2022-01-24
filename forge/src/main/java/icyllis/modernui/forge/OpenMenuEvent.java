/*
 * Modern UI.
 * Copyright (C) 2019-2022 BloCamLimb. All rights reserved.
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

package icyllis.modernui.forge;

import icyllis.modernui.fragment.Fragment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * This event is triggered when the server requires the client to open a user
 * interface to display the container menu in world, this event is cancelled
 * after setting the user interface. The menu is created on the client by
 * registered {@link net.minecraftforge.network.IContainerFactory factory},
 * which contains custom network data from server, you can set the user
 * interface through the data and the menu type.  For example:
 *
 * <pre>
 * &#64;SubscribeEvent
 * static void onMenuOpen(OpenMenuEvent event) {
 *     if (event.getMenu().getType() == Registry.TEST_MENU) {
 *         event.set(new TestFragment());
 *     }
 * }
 * </pre>
 * <p>
 * This event will be only posted to your own mod event bus on client main thread.
 * It's an error if no callback set along with this event.
 *
 * @see MuiForgeApi#openMenu(Player, MenuConstructor, Consumer)
 */
@Cancelable
@OnlyIn(Dist.CLIENT)
public final class OpenMenuEvent extends Event implements IModBusEvent {

    @Nonnull
    private final AbstractContainerMenu mMenu;

    @Nullable
    private Fragment mFragment;
    @Nullable
    private UICallback mCallback;

    OpenMenuEvent(@Nonnull AbstractContainerMenu menu) {
        mMenu = menu;
    }

    /**
     * Get the source of the event.
     *
     * @return the container menu
     */
    @Nonnull
    public AbstractContainerMenu getMenu() {
        return mMenu;
    }

    /**
     * Set the fragment for the menu. The event will be auto canceled.
     *
     * @param fragment the fragment
     */
    public void set(@Nonnull Fragment fragment) {
        set(fragment, null);
    }

    /**
     * Set the fragment for the menu. The event will be auto canceled.
     *
     * @param fragment the fragment
     * @param callback the UI callback, null meaning a default setup
     */
    public void set(@Nonnull Fragment fragment, @Nullable UICallback callback) {
        mFragment = fragment;
        mCallback = callback;
        if (!isCanceled()) {
            setCanceled(true);
        }
    }

    @Nullable
    Fragment getFragment() {
        return mFragment;
    }

    @Nullable
    UICallback getCallback() {
        return mCallback;
    }
}
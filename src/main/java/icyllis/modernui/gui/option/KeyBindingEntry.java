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

package icyllis.modernui.gui.option;

import com.google.common.collect.Lists;
import icyllis.modernui.gui.master.GlobalModuleManager;
import icyllis.modernui.gui.popup.PopupMenu;
import icyllis.modernui.gui.widget.DropDownMenu;
import icyllis.modernui.gui.widget.KeyInputBox;
import icyllis.modernui.gui.scroll.SettingScrollWindow;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyModifier;

import java.util.Locale;
import java.util.Objects;

public class KeyBindingEntry extends AbstractOptionEntry {

    private KeyBinding keyBinding;

    private KeyInputBox inputBox;

    private Runnable conflictsCallback;

    public KeyBindingEntry(SettingScrollWindow window, KeyBinding keyBinding, Runnable conflictsCallback) {
        super(window, I18n.format(keyBinding.getKeyDescription()));
        this.keyBinding = keyBinding;
        this.inputBox = new KeyInputBox(window::setFocused, this::bindKey);
        //TODO tint text by conflict context, or maybe not?
        //this.conflictContext = keyBinding.getKeyConflictContext().toString();
        this.conflictsCallback = conflictsCallback;
        updateKeyText();
    }

    @Override
    public void drawExtra(float centerX, float y, float currentTime) {
        inputBox.setPos(centerX + 70, y + 2);
        inputBox.draw(currentTime);
    }

    @Override
    public void mouseMoved(double deltaCenterX, double deltaY, double mouseX, double mouseY) {
        inputBox.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double deltaCenterX, double deltaY, double mouseX, double mouseY, int mouseButton) {
        if (mouseHovered && mouseButton == 1) {
            DropDownMenu list = new DropDownMenu(Lists.newArrayList("WIP =w="), -1, 16, this::menuActions);
            list.setPos((float) mouseX, (float) (mouseY - deltaY + 18), GlobalModuleManager.INSTANCE.getWindowHeight());
            GlobalModuleManager.INSTANCE.openPopup(new PopupMenu(list), false);
            return true;
        }
        if (inputBox.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return super.mouseClicked(deltaCenterX, deltaY, mouseX, mouseY, mouseButton);
    }

    private void menuActions(int index) {

    }

    // vanilla call this every frame... but we don't
    private void updateKeyText() {
        String translateKey = keyBinding.getTranslationKey();
        String localizedName = I18n.format(translateKey);

        InputMappings.Input key = keyBinding.getKey();
        if (keyBinding.isInvalid()) {
            inputBox.setKeyText("");
            return;
        }

        int keyCode = key.getKeyCode();
        String glfwName = null;

        switch (key.getType()) {
            case KEYSYM:
                glfwName = InputMappings.getKeynameFromKeycode(keyCode);
                break;
            case SCANCODE:
                glfwName = InputMappings.getKeyNameFromScanCode(keyCode);
                break;
            case MOUSE:
                // if not translated, use default, eg. keyCode 0 = Mouse 1 (Mouse Left Button)
                inputBox.setKeyText(Objects.equals(localizedName, translateKey) ?
                        I18n.format(InputMappings.Type.MOUSE.getName(), keyCode + 1) : localizedName);
                return;
        }

        KeyModifier modifier = keyBinding.getKeyModifier();
        String comboPrefix;
        // why not use forge localization? bcz there's no need actually, modifier key shouldn't be translated, =w=
        switch (modifier) {
            case CONTROL:
                comboPrefix = "Ctrl + ";
                break;
            case ALT:
                comboPrefix = "Alt + ";
                break;
            case SHIFT:
                comboPrefix = "Shift + ";
                break;
            default:
                comboPrefix = "";
                break;
        }

        if (glfwName != null) {
            // glfwName length must be 1, so no need to check
            char c = glfwName.charAt(0);
            if (Character.isLetter(c)) {
                // uppercase looks better, awa
                glfwName = glfwName.toUpperCase(Locale.ROOT);
            }
            inputBox.setKeyText(comboPrefix + glfwName);
        } else {
            inputBox.setKeyText(comboPrefix + localizedName);
        }
    }

    public void setConflictTier(int tier) {
        inputBox.setTextColor(tier);
    }

    private void bindKey(InputMappings.Input inputIn) {
        GameSettings gameSettings = Minecraft.getInstance().gameSettings;
        if (inputIn.getType() != InputMappings.Type.MOUSE) {
            keyBinding.setKeyModifierAndCode(KeyModifier.getActiveModifier(), inputIn);
        }
        gameSettings.setKeyBindingCode(keyBinding, inputIn);
        updateKeyText();
        conflictsCallback.run();
    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

    @Override
    protected void onMouseHoverOn() {
        super.onMouseHoverOn();
        inputBox.setTextBrightness(titleBrightness);
    }

    @Override
    protected void onMouseHoverOff() {
        super.onMouseHoverOff();
        inputBox.setTextBrightness(titleBrightness);
    }
}

package icyllis.modernui.api.module;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

/**
 * Implements this to create your modern screen
 */
public interface IModuleInjector {

    ITextComponent EMPTY_TITLE = new StringTextComponent("");

    /**
     * Inject your custom modules in Modern UI
     * If there's a module injected, make sure at least one is main
     *
     * @param provider ModernUI module provider
     */
    void injectModules(IModuleProvider provider);

    /**
     * Override this for non-container screen
     */
    default ITextComponent getTitle() {
        return EMPTY_TITLE;
    }
}

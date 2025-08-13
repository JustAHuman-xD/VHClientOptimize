package me.justahuman.vh_client_optimize.mixin.jei_tweaker;

import com.blamejared.jeitweaker.api.IngredientType;
import com.blamejared.jeitweaker.implementation.state.StateManager;
import com.blamejared.jeitweaker.jei.JeiTweakerPlugin;
import me.justahuman.vh_client_optimize.extension.AsyncJei;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IIngredientManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = JeiTweakerPlugin.class, remap = false)
public class JeiTweakerPluginMixin {
    /**
     * @author JustAHuman
     * @reason Move the aggregation of hidden ingredients to Async thread then hide once all are aggregated on the main thread.
     */
    @Overwrite
    private <T, U> void hideIngredientsFor(final IIngredientManager manager, final IngredientType<T, U> type) {
        AsyncJei.THREAD.execute(() -> {
            final Collection<T> hiddenIngredients = StateManager.INSTANCE.actionsState().getHiddenIngredientsForType(type);
            final IIngredientType<U> jeiType = type.toJeiIngredientType(manager);
            final List<U> removals = manager.getAllIngredients(jeiType)
                    .stream()
                    .map(type::toJeiTweakerType)
                    .filter(ingredient -> hiddenIngredients.stream().anyMatch(it -> type.match(it, ingredient)))
                    .map(type::toJeiType)
                    .collect(Collectors.toList());
            if (removals.isEmpty()) {
                return;
            }

            manager.removeIngredientsAtRuntime(jeiType, removals);
        });
    }
}

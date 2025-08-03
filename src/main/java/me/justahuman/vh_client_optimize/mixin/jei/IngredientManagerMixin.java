package me.justahuman.vh_client_optimize.mixin.jei;

import me.justahuman.vh_client_optimize.VHClientOptimize;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.core.config.IClientConfig;
import mezz.jei.gui.ingredients.IListElement;
import mezz.jei.ingredients.IListElementInfo;
import mezz.jei.ingredients.IngredientBlacklistInternal;
import mezz.jei.ingredients.IngredientFilter;
import mezz.jei.ingredients.IngredientInfo;
import mezz.jei.ingredients.IngredientListElementFactory;
import mezz.jei.ingredients.IngredientManager;
import mezz.jei.ingredients.ListElementInfo;
import mezz.jei.ingredients.RegisteredIngredients;
import mezz.jei.ingredients.TypedIngredient;
import mezz.jei.util.ErrorUtil;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(value = IngredientManager.class, remap = false)
public abstract class IngredientManagerMixin {
    @Shadow @Final private IModIdHelper modIdHelper;
    @Shadow @Final private IClientConfig clientConfig;
    @Shadow @Final private IngredientBlacklistInternal blacklist;
    @Shadow @Final private static Logger LOGGER;

    @Shadow @Final private RegisteredIngredients registeredIngredients;
    @Shadow @Final private IngredientFilter ingredientFilter;

    /**
     * @author JustAHuman
     * @reason Schedule the addition of ingredients at runtime
     */
    @Overwrite
    public <V> void addIngredientsAtRuntime(IIngredientType<V> ingredientType, Collection<V> ingredients) {
        if (VHClientOptimize.JEI_FUTURE != null) {
            Runnable addTask = () -> Minecraft.getInstance().execute(() -> this.vh$coreAddIngredientsAtRuntime(ingredientType, ingredients));
            if (VHClientOptimize.JEI_FUTURE.isDone()) {
                addTask.run();
            } else {
                VHClientOptimize.JEI_FUTURE.thenRun(addTask);
            }
        } else {
            LOGGER.error("Failed to add ingredients at runtime, JEI is not initialized yet.");
        }
    }

    /**
     * @author JustAHuman
     * @reason Schedule the removal of ingredients at runtime
     */
    @Overwrite
    public <V> void removeIngredientsAtRuntime(IIngredientType<V> ingredientType, Collection<V> ingredients) {
        if (VHClientOptimize.JEI_FUTURE != null) {
            Runnable removeTask = () -> Minecraft.getInstance().execute(() -> this.vh$coreRemoveIngredientsAtRuntime(ingredientType, ingredients));
            if (VHClientOptimize.JEI_FUTURE.isDone()) {
                removeTask.run();
            } else {
                VHClientOptimize.JEI_FUTURE.thenRun(removeTask);
            }
        } else {
            LOGGER.error("Failed to remove ingredients at runtime, JEI is not initialized yet.");
        }
    }

    @Unique
    private <V> void vh$coreRemoveIngredientsAtRuntime(IIngredientType<V> ingredientType, Collection<V> ingredients) {
        ErrorUtil.assertMainThread();
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        ErrorUtil.checkNotEmpty(ingredients, "ingredients");
        IngredientInfo<V> ingredientInfo = this.registeredIngredients.getIngredientInfo(ingredientType);
        LOGGER.info("Ingredients are being removed at runtime: {} {}", ingredients.size(), ingredientType.getIngredientClass().getName());
        ingredientInfo.removeIngredients(ingredients);
        IIngredientHelper<V> ingredientHelper = this.getIngredientHelper(ingredientType);
        ingredients.stream().map((i) -> {
            return TypedIngredient.createTyped(this.registeredIngredients, ingredientType, i);
        }).flatMap(Optional::stream).forEach((typedIngredient) -> {
            Optional<IListElementInfo<V>> matchingElementInfo = this.ingredientFilter.searchForMatchingElement(ingredientHelper, typedIngredient);
            if (matchingElementInfo.isEmpty()) {
                String errorInfo = ingredientHelper.getErrorInfo(typedIngredient.getIngredient());
                LOGGER.error("Could not find a matching ingredient to remove: {}", errorInfo);
            } else {
                if (this.clientConfig.isDebugModeEnabled()) {
                    LOGGER.debug("Removed ingredient: {}", ingredientHelper.getErrorInfo(typedIngredient.getIngredient()));
                }

                IListElement<V> matchingElement = ((IListElementInfo)matchingElementInfo.get()).getElement();
                this.blacklist.addIngredientToBlacklist(matchingElement.getTypedIngredient(), ingredientHelper);
                matchingElement.setVisible(false);
            }

        });
        this.ingredientFilter.invalidateCache();
    }

    @Unique
    private <V> void vh$coreAddIngredientsAtRuntime(IIngredientType<V> ingredientType, Collection<V> ingredients) {
        ErrorUtil.assertMainThread();
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        ErrorUtil.checkNotEmpty(ingredients, "ingredients");
        IngredientInfo<V> ingredientInfo = this.registeredIngredients.getIngredientInfo(ingredientType);
        LOGGER.info("Ingredients are being added at runtime: {} {}", ingredients.size(), ingredientType.getIngredientClass().getName());
        ingredientInfo.addIngredients(ingredients);
        List<ITypedIngredient<V>> typedIngredients = ingredients.stream().map((i) -> {
            return TypedIngredient.createTyped(this.registeredIngredients, ingredientType, i);
        }).map(Optional::orElseThrow).toList();
        IIngredientHelper<V> ingredientHelper = ingredientInfo.getIngredientHelper();
        Iterator var6 = typedIngredients.iterator();

        while(var6.hasNext()) {
            ITypedIngredient<V> value = (ITypedIngredient)var6.next();
            Optional<IListElementInfo<V>> matchingElementInfo = this.ingredientFilter.searchForMatchingElement(ingredientHelper, value);
            IListElement matchingElement;
            if (matchingElementInfo.isPresent()) {
                matchingElement = ((IListElementInfo)matchingElementInfo.get()).getElement();
                ITypedIngredient<V> typedIngredient = matchingElement.getTypedIngredient();
                this.blacklist.removeIngredientFromBlacklist(typedIngredient, ingredientHelper);
                this.ingredientFilter.updateHiddenState(matchingElement);
                if (this.clientConfig.isDebugModeEnabled()) {
                    LOGGER.debug("Updated ingredient: {}", ingredientHelper.getErrorInfo(value.getIngredient()));
                }
            } else {
                matchingElement = IngredientListElementFactory.createOrderedElement(value);
                IListElementInfo<V> info = ListElementInfo.create(matchingElement, this.registeredIngredients, this.modIdHelper);
                if (info != null) {
                    this.blacklist.removeIngredientFromBlacklist(value, ingredientHelper);
                    this.ingredientFilter.addIngredient(info);
                    if (this.clientConfig.isDebugModeEnabled()) {
                        LOGGER.debug("Added ingredient: {}", ingredientHelper.getErrorInfo(value.getIngredient()));
                    }
                }
            }
        }

        this.ingredientFilter.invalidateCache();
    }

    @Shadow public abstract <V> IIngredientHelper<V> getIngredientHelper(IIngredientType<V> ingredientType);
}

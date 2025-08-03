package me.justahuman.vh_client_optimize.mixin.jei;

import me.justahuman.vh_client_optimize.VHClientOptimize;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.recipes.RecipeManager;
import mezz.jei.recipes.RecipeManagerInternal;
import mezz.jei.util.ErrorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;

@Mixin(value = RecipeManager.class, remap = false)
public class RecipeManagerMixin {
    @Shadow @Final private RecipeManagerInternal internal;

    /**
     * @author JustAHuman
     * @reason Schedule hide recipes to run on the main thread
     */
    @Overwrite
    public <T> void hideRecipe(T recipe, ResourceLocation recipeCategoryUid) {
        if (VHClientOptimize.JEI_FUTURE != null) {
            Runnable hideRecipesTask = () -> Minecraft.getInstance().execute(() -> this.vh$coreHideRecipes(recipe, recipeCategoryUid));
            if (VHClientOptimize.JEI_FUTURE.isDone()) {
                hideRecipesTask.run();
            } else {
                VHClientOptimize.JEI_FUTURE.thenRun(hideRecipesTask);
            }
        } else {
            VHClientOptimize.LOGGER.error("JEI RECIPE MANAGER >> Failed to remove ingredients at runtime, JEI is not initialized yet.");
        }
    }

    /**
     * @author JustAHuman
     * @reason Schedule hide recipes to run on the main thread
     */
    @Overwrite
    public <T> void hideRecipes(RecipeType<T> recipeType, Collection<T> recipes) {
        if (VHClientOptimize.JEI_FUTURE != null) {
            Runnable hideRecipesTask = () -> Minecraft.getInstance().execute(() -> this.vh$coreHideRecipes(recipeType, recipes));
            if (VHClientOptimize.JEI_FUTURE.isDone()) {
                hideRecipesTask.run();
            } else {
                VHClientOptimize.JEI_FUTURE.thenRun(hideRecipesTask);
            }
        } else {
            VHClientOptimize.LOGGER.error("JEI RECIPE MANAGER >> Failed to remove ingredients at runtime, JEI is not initialized yet.");
        }
    }

    @Unique
    private <T> void vh$coreHideRecipes(RecipeType<T> recipeType, Collection<T> recipes) {
        ErrorUtil.checkNotNull(recipes, "recipe");
        ErrorUtil.checkNotNull(recipeType, "recipeType");
        ErrorUtil.assertMainThread();
        this.internal.hideRecipes(recipeType, recipes);
    }

    @Unique
    private <T> void vh$coreHideRecipes(T recipe, ResourceLocation recipeCategoryUid) {
        ErrorUtil.checkNotNull(recipe, "recipe");
        ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
        ErrorUtil.assertMainThread();
        this.internal.hideRecipe(recipeCategoryUid, recipe);
    }
}

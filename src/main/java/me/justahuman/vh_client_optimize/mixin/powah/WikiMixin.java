package me.justahuman.vh_client_optimize.mixin.powah;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.Marker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import owmii.powah.Powah;
import owmii.powah.lib.client.wiki.Wiki;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(value = Wiki.class, remap = false)
public class WikiMixin {
    @Shadow @Final public static Marker MARKER;
    @Shadow @Final public static Map<String, Wiki> WIKIS;
    @Unique
    private static final ExecutorService WIKI_LOADING_THREAD = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "WikiLoadingThread");
        thread.setDaemon(true);
        return thread;
    });

    /**
     * @author JustAHuman
     * @reason To offload wiki loading to a different thread
     */
    @Overwrite
    public static void lambda$static$5(RecipeManager recipeManager) {
        WIKI_LOADING_THREAD.execute(() -> {
            StopWatch watch = StopWatch.createStarted();
            Powah.LOGGER.info(MARKER, "Started Async wikis recipes collecting...");

            List<Item> items = new ArrayList<>();
            for (Item item : Registry.ITEM) {
                ResourceLocation name = item.getRegistryName();
                if (name != null && Powah.MOD_ID.equals(name.getNamespace())) {
                    items.add(item);
                }
            }

            Map<ItemLike, List<Recipe<?>>> craftingRecipes = new HashMap<>();
            recipeManager.getAllRecipesFor(RecipeType.CRAFTING).forEach((recipe) -> {
                ItemLike resultItem = recipe.getResultItem().getItem();
                if (items.contains(resultItem)) {
                    craftingRecipes.computeIfAbsent(resultItem, k -> new ArrayList<>()).add(recipe);
                }
            });

            Map<ItemLike, List<Recipe<?>>> smeltingRecipes = new HashMap<>();
            recipeManager.getAllRecipesFor(RecipeType.SMELTING).forEach((recipe) -> {
                ItemLike resultItem = recipe.getResultItem().getItem();
                if (items.contains(resultItem)) {
                    smeltingRecipes.computeIfAbsent(resultItem, k -> new ArrayList<>()).add(recipe);
                }
            });

            for (Map.Entry<String, Wiki> entry : WIKIS.entrySet()) {
                Wiki wiki = entry.getValue();
                wiki.getCrafting().clear();
                wiki.getSmelting().clear();
                wiki.getCrafting().putAll(craftingRecipes);
                wiki.getSmelting().putAll(smeltingRecipes);
            }
            watch.stop();
            Powah.LOGGER.info(MARKER, "Async Wiki recipes collecting completed in : {} ms", watch.getTime());
        });
    }
}

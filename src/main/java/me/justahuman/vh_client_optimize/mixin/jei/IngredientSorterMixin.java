package me.justahuman.vh_client_optimize.mixin.jei;

import me.justahuman.vh_client_optimize.extension.AsyncJei;
import mezz.jei.ingredients.IListElementInfo;
import mezz.jei.ingredients.IngredientFilter;
import mezz.jei.ingredients.IngredientSorter;
import org.jetbrains.annotations.Unmodifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Comparator;
import java.util.List;

@Mixin(value = IngredientSorter.class, remap = false)
public class IngredientSorterMixin {
    @Redirect(method = "doPreSort", at = @At(value = "INVOKE", target = "Lmezz/jei/ingredients/IngredientFilter;getIngredientListPreSort(Ljava/util/Comparator;)Ljava/util/List;"))
    public @Unmodifiable List<IListElementInfo<?>> asyncPreSort(IngredientFilter instance, Comparator<IListElementInfo<?>> directComparator) {
        AsyncJei.THREAD.execute(() -> {
            List<IListElementInfo<?>> results = instance.getIngredientListPreSort(directComparator);
            int i = 0;
            for(int resultsSize = results.size(); i < resultsSize; ++i) {
                results.get(i).setSortedIndex(i);
            }
        });
        return List.of();
    }
}

package me.justahuman.vh_client_optimize.mixin.vault_hunters;

import iskallia.vault.config.TooltipConfig;
import me.justahuman.vh_client_optimize.extension.ItemCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = TooltipConfig.TooltipEntry.class, remap = false)
public class TooltipEntryMixin implements ItemCache {
    @Shadow
    private String item;
    @Unique
    Item vh$itemCache;
    @Unique
    boolean vh$attemptedCache = false;

    @Override
    public Item vh$getItemCache() {
        if (vh$itemCache == null && !vh$attemptedCache) {
            vh$attemptedCache = true;
            ResourceLocation itemId = ResourceLocation.tryParse(this.item);
            if (itemId != null) {
                vh$itemCache = ForgeRegistries.ITEMS.getValue(itemId);
            }
        }
        return vh$itemCache;
    }
}

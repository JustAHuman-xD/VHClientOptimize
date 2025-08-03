package me.justahuman.vh_client_optimize.mixin.vault_hunters;

import iskallia.vault.config.TooltipConfig;
import me.justahuman.vh_client_optimize.extension.ItemCache;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApiStatus.ScheduledForRemoval(inVersion = "VH-u20")
@Mixin(value = TooltipConfig.class, remap = false)
public class TooltipConfigMixin {
    @Unique private final Map<Item, TooltipConfig.TooltipEntry> vh$itemCache = new HashMap<>();

    @Shadow @Final private List<TooltipConfig.TooltipEntry> tooltips;

    /**
     * @author JustAHuman
     * @reason Add caching for item tooltips
     */
    @Overwrite
    public Optional<String> getTooltipString(Item item) {
        if (vh$itemCache.containsKey(item)) {
            TooltipConfig.TooltipEntry cached = vh$itemCache.get(item);
            return Optional.ofNullable(cached == null ? null : cached.getValue());
        }

        for (TooltipConfig.TooltipEntry entry : this.tooltips) {
            if (((ItemCache) entry).vh$getItemCache() == item) {
                vh$itemCache.put(item, entry);
                return Optional.of(entry.getValue());
            }
        }
        vh$itemCache.put(item, null);
        return Optional.empty();
    }

}

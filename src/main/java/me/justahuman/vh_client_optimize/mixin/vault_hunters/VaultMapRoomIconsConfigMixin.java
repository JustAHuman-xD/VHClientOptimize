package me.justahuman.vh_client_optimize.mixin.vault_hunters;

import iskallia.vault.config.VaultMapRoomIconsConfig;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = VaultMapRoomIconsConfig.class, remap = false)
public class VaultMapRoomIconsConfigMixin {
    @Unique private static final Map<ResourceLocation, Optional<ResourceLocation>> ROOM_ICON_CACHE = new ConcurrentHashMap<>();
    @Unique private static final List<ResourceLocation> ICONS_CACHE = new ArrayList<>();

    @Shadow private Map<ResourceLocation, List<ResourceLocation>> roomIcons;

    /**
     * @author JustAHuman
     * @reason Remove the senseless streams and cache the results
     */
    @Overwrite
    public Optional<ResourceLocation> getIconForRoom(ResourceLocation room) {
        Optional<ResourceLocation> cached = ROOM_ICON_CACHE.get(room);
        if (cached != null) {
            return cached;
        }

        for (Map.Entry<ResourceLocation, List<ResourceLocation>> entry : this.roomIcons.entrySet()) {
            for (ResourceLocation rl : entry.getValue()) {
                if (rl.getPath().contains(room.getPath())) {
                    Optional<ResourceLocation> result = Optional.of(entry.getKey());
                    ROOM_ICON_CACHE.put(room, result);
                    return result;
                }
            }
        }
        ROOM_ICON_CACHE.put(room, Optional.empty());
        return Optional.empty();
    }

    /**
     * @author JustAHuman
     * @reason Removes the senseless stream from this method
     */
    @Overwrite
    public List<ResourceLocation> getIcons() {
        if (ICONS_CACHE.isEmpty()) {
            ICONS_CACHE.addAll(this.roomIcons.keySet());
        }
        return ICONS_CACHE;
    }
}

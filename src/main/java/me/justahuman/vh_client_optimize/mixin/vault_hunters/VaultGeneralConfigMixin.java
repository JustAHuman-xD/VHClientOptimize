package me.justahuman.vh_client_optimize.mixin.vault_hunters;

import iskallia.vault.config.VaultGeneralConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.ScheduledForRemoval(inVersion = "VH-u20")
@Mixin(value = VaultGeneralConfig.class, remap = false)
public class VaultGeneralConfigMixin {
    @Unique private final Map<ResourceLocation, Boolean> vh$blacklistCache = new ConcurrentHashMap<>();

    @Inject(method = "isBlacklisted(Lnet/minecraft/world/level/block/Block;)Z", at = @At("RETURN"))
    public void cacheBlockBlacklist(Block block, CallbackInfoReturnable<Boolean> cir) {
        vh$blacklistCache.put(block.getRegistryName(), cir.getReturnValue());
    }

    @Inject(method = "isBlacklisted(Lnet/minecraft/world/item/Item;)Z", at = @At("RETURN"))
    public void cacheItemBlacklist(Item item, CallbackInfoReturnable<Boolean> cir) {
        vh$blacklistCache.put(item.getRegistryName(), cir.getReturnValue());
    }

    @Inject(method = "isBlacklisted(Lnet/minecraft/world/level/block/Block;)Z", at = @At("HEAD"), cancellable = true)
    public void checkBlockBlacklistCache(Block block, CallbackInfoReturnable<Boolean> cir) {
        Boolean cached = vh$blacklistCache.get(block.getRegistryName());
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(method = "isBlacklisted(Lnet/minecraft/world/item/Item;)Z", at = @At("HEAD"), cancellable = true)
    public void checkItemBlacklistCache(Item item, CallbackInfoReturnable<Boolean> cir) {
        Boolean cached = vh$blacklistCache.get(item.getRegistryName());
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }
}

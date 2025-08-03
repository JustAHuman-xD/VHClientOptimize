package me.justahuman.vh_client_optimize.mixin.vault_hunters;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.event.ClientEvents;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModConfigs;
import me.justahuman.vh_client_optimize.extension.DummyVaultGearData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@ApiStatus.ScheduledForRemoval(inVersion = "VH-u20")
@Mixin(value = ClientEvents.class, remap = false)
public class ClientEventsMixin {
    @Redirect(method = "addLootTableInfoToTooltip", at = @At(value = "INVOKE", target = "Liskallia/vault/gear/data/VaultGearData;read(Lnet/minecraft/world/item/ItemStack;)Liskallia/vault/gear/data/VaultGearData;"))
    private static VaultGearData useGearDataCache(ItemStack stack) {
        return new DummyVaultGearData(GearDataCache.of(stack).getState());
    }

    @Redirect(method = "addLootTableInfoToTooltip", at = @At(value = "INVOKE", target = "Liskallia/vault/core/world/loot/LootTableInfo;getLootTableKeysForItem(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Set;"))
    private static Set<ResourceLocation> removeStupidStream(ResourceLocation resourceLocation) {
        return Set.of();
    }

    @Redirect(method = "addLootTableInfoToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;toList()Ljava/util/List;"))
    private static List<String> addActualResults(Stream<String> instance, @Local Item item) {
        return new ArrayList<>(ModConfigs.LOOT_INFO_CONFIG.getTooltipLines(item));
    }
}

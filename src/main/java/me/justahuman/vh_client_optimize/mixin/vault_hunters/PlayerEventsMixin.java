package me.justahuman.vh_client_optimize.mixin.vault_hunters;

import iskallia.vault.event.PlayerEvents;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ApiStatus.ScheduledForRemoval(inVersion = "VH-u20")
@Mixin(value = PlayerEvents.class, remap = false)
public class PlayerEventsMixin {
    @Inject(method = "hideIdolRegistryName", at = @At("HEAD"), cancellable = true)
    private static void cancelHideIdol(ItemTooltipEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}

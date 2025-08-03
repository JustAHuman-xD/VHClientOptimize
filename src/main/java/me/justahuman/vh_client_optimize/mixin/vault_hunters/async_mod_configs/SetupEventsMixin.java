package me.justahuman.vh_client_optimize.mixin.vault_hunters.async_mod_configs;

import iskallia.vault.event.SetupEvents;
import me.justahuman.vh_client_optimize.extension.AsyncModConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SetupEvents.class, remap = false)
public class SetupEventsMixin {
    @Redirect(method = "setupCommon", at = @At(value = "INVOKE", target = "Liskallia/vault/init/ModConfigs;registerGen()V"))
    private static void asynGenConfigs() {
        AsyncModConfigs.registerGenConfigs();
    }

    @Redirect(method = "setupCommon", at = @At(value = "INVOKE", target = "Liskallia/vault/init/ModConfigs;register()V"))
    private static void asynConfigs() {
        AsyncModConfigs.registerConfigs();
    }
}

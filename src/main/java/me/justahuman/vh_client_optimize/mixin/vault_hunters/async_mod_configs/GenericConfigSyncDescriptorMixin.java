package me.justahuman.vh_client_optimize.mixin.vault_hunters.async_mod_configs;

import lv.id.bonne.vaulthunters.serversync.networking.GenericConfigSyncDescriptor;
import me.justahuman.vh_client_optimize.extension.AsyncClientCommonSetup;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;

@Mixin(value = GenericConfigSyncDescriptor.class, remap = false)
public class GenericConfigSyncDescriptorMixin {
    @Redirect(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/network/NetworkEvent$Context;enqueueWork(Ljava/lang/Runnable;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Void> handleAsync(NetworkEvent.Context instance, Runnable runnable) {
        AsyncClientCommonSetup.execute(runnable);
        return new CompletableFuture<>();
    }
}

package me.justahuman.vh_client_optimize.mixin.ctm;

import net.minecraftforge.client.event.ModelBakeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.chisel.ctm.client.util.TextureMetadataHandler;

@Mixin(value = TextureMetadataHandler.class, remap = false)
public abstract class TextureMetadataHandlerMixin {
    @Shadow public abstract void invalidateCaches();

    @Inject(method = "onModelBake", at = @At("RETURN"))
    public void postModelBake(ModelBakeEvent event, CallbackInfo ci) {
        invalidateCaches();
    }
}

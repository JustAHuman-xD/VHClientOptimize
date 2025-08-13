package me.justahuman.vh_client_optimize.mixin.ctm;

import me.justahuman.vh_client_optimize.VHClientOptimize;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.chisel.ctm.client.util.TextureMetadataHandler;

import java.util.Map;

@Mixin(value = TextureMetadataHandler.class, remap = false)
public abstract class TextureMetadataHandlerMixin {
    @Unique private static final String EVERYCOMP_NAMESPACE = "everycomp";

    @Unique private long vh$startedBaking = 0;
    @Unique private int vh$skipped = 0;

    @Inject(method = "onModelBake", at = @At("HEAD"))
    public void preModelBake(ModelBakeEvent event, CallbackInfo ci) {
        vh$startedBaking = System.nanoTime();
        vh$skipped = 0;
    }

    @Redirect(method = "onModelBake", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    public <K, V> V skipEveryComp(Map<K, V> instance, K o) {
        ResourceLocation id = (ResourceLocation) o;
        if (id.getNamespace().equals(EVERYCOMP_NAMESPACE)) {
            vh$skipped++;
            return null;
        }
        return instance.get(o);
    }

    @Inject(method = "onModelBake", at = @At("RETURN"))
    public void postModelBake(ModelBakeEvent event, CallbackInfo ci) {
        long took = System.nanoTime() - vh$startedBaking;
        VHClientOptimize.LOGGER.info("CTM model baking took {}s, skipped {} everycomp models", (double) took / 1_000_000_000.0, vh$skipped);
        invalidateCaches();
    }

    @Shadow public abstract void invalidateCaches();
}

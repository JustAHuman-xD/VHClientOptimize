package me.justahuman.vh_client_optimize.mixin.selene;

import me.justahuman.vh_client_optimize.VHClientOptimize;
import net.mehvahdjukaar.selene.resourcepack.DynamicResourcePack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DynamicResourcePack.class, remap = false)
public class DynamicResourcePackMixin {
    @Shadow public boolean generateDebugResources;

    @Inject(method = "<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/server/packs/PackType;Lnet/minecraft/server/packs/repository/Pack$Position;ZZ)V", at = @At("RETURN"))
    public void useConfigOption(ResourceLocation name, PackType type, Pack.Position position, boolean fixed, boolean hidden, CallbackInfo ci) {
        this.generateDebugResources = VHClientOptimize.DYNAMIC_PACK_DEBUG.get();
    }
}

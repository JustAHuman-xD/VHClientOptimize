package me.justahuman.vh_client_optimize.mixin.vault_mapper;

import com.nodiumhosting.vaultmapper.map.RoomData;
import iskallia.vault.core.world.template.StructureTemplate;
import iskallia.vault.core.world.template.Template;
import me.justahuman.vh_client_optimize.mixin.vault_hunters.template_cleanup.StructureTemplateAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RoomData.class)
public class RoomDataMixin {
    @Inject(method = "<init>(Ljava/lang/String;Ljava/lang/String;Liskallia/vault/core/world/template/Template;)V", at = @At("RETURN"), remap = false)
    public void cleanupTiles(String type, String name, Template room, CallbackInfo ci) {
        if (room instanceof StructureTemplate structure) {
            ((StructureTemplateAccessor) structure).vh$getTiles().clear();
            ((StructureTemplateAccessor) structure).vh$getEntities().clear();
            structure.setUninitialized();
        }
    }
}

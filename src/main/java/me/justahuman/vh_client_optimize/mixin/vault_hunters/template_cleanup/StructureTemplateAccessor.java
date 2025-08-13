package me.justahuman.vh_client_optimize.mixin.vault_hunters.template_cleanup;

import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.template.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(value = StructureTemplate.class, remap = false)
public interface StructureTemplateAccessor {
    @Accessor("tiles") Map<TilePredicate, List<PartialTile>> vh$getTiles();
    @Accessor("entities") Map<TilePredicate, List<PartialEntity>> vh$getEntities();
}

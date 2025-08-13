package me.justahuman.vh_client_optimize.mixin.vault_mapper;

import com.nodiumhosting.vaultmapper.map.RoomData;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.template.StructureTemplate;
import iskallia.vault.core.world.template.Template;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import me.justahuman.vh_client_optimize.mixin.vault_hunters.template_cleanup.StructureTemplateAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(value = RoomData.class, remap = false)
public class RoomDataMixin {
    @Shadow public List<Map<Integer, Block>> columnList;
    @Shadow public Block mineOption1;
    @Shadow public Block mineOption2;
    @Shadow public Block raidOption;

    @Redirect(method = "<init>(Ljava/lang/String;Ljava/lang/String;Liskallia/vault/core/world/template/Template;)V", at = @At(value = "INVOKE", target = "Liskallia/vault/core/world/template/Template;getTiles(Liskallia/vault/core/world/data/tile/TilePredicate;)Ljava/util/Iterator;"))
    public Iterator<PartialTile> provideDummyTiles(Template instance, TilePredicate filter) {
        return ObjectIterators.emptyIterator();
    }

    @Inject(method = "<init>(Ljava/lang/String;Ljava/lang/String;Liskallia/vault/core/world/template/Template;)V", at = @At("RETURN"), remap = false)
    public void cleanupTiles(String type, String name, Template room, CallbackInfo ci) {
        StructureTemplateAccessor.vh$getExecutor().execute(() -> {
            this.columnList.clear();
            Map<Integer, Block> northeastColumn = new HashMap<>();
            Map<Integer, Block> northwestColumn = new HashMap<>();
            Map<Integer, Block> southeastColumn = new HashMap<>();
            Map<Integer, Block> southwestColumn = new HashMap<>();
            this.columnList.add(northeastColumn);
            this.columnList.add(northwestColumn);
            this.columnList.add(southeastColumn);
            this.columnList.add(southwestColumn);

            Iterator<PartialTile> tiles = room.getTiles(Template.ALL_TILES);
            while(tiles.hasNext()) {
                PartialTile tile = tiles.next();
                BlockPos pos = tile.getPos();
                Optional<Block> optBlock = tile.getState().getBlock().asWhole();
                if (optBlock.isEmpty()) {
                    continue;
                }

                Block block = optBlock.get();
                int x = pos.getX();
                int z = pos.getZ();
                int y = pos.getY();
                if (x == 0 && z == 0) {
                    northwestColumn.put(y, block);
                } else if (x == 46 && z == 0) {
                    northeastColumn.put(y, block);
                } else if (x == 0 && z == 46) {
                    southwestColumn.put(y, block);
                } else if (x == 46 && z == 46) {
                    southeastColumn.put(y, block);
                }

                if (x == 23 && y == 32 && z == 23) {
                    this.mineOption1 = block;
                } else if (x == 23 && y == 31 && z == 23) {
                    this.mineOption2 = block;
                } else if (x == 23 && y == 29 && z == 23) {
                    this.raidOption = block;
                }
            }

            if (room instanceof StructureTemplate structure) {
                ((StructureTemplateAccessor) structure).vh$getTiles().clear();
                ((StructureTemplateAccessor) structure).vh$getEntities().clear();
                structure.setUninitialized();
            }
        });
    }
}

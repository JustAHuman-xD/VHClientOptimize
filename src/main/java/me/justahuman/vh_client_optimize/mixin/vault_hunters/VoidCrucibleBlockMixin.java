package me.justahuman.vh_client_optimize.mixin.vault_hunters;

import iskallia.vault.block.VoidCrucibleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@ApiStatus.ScheduledForRemoval(inVersion = "VH-u20")
@Mixin(VoidCrucibleBlock.class)
public class VoidCrucibleBlockMixin {
    @Unique
    private static final VoxelShape vh$SHAPE = Stream.of(Block.box(2.0, 13.0, 3.0, 4.0, 15.0, 13.0), Block.box(1.0, 0.0, 1.0, 15.0, 10.0, 15.0), Block.box(2.0, 10.0, 2.0, 14.0, 12.0, 14.0), Block.box(3.0, 12.0, 3.0, 13.0, 13.0, 13.0), Block.box(3.0, 13.0, 2.0, 13.0, 15.0, 4.0), Block.box(3.0, 13.0, 12.0, 13.0, 15.0, 14.0), Block.box(12.0, 13.0, 3.0, 14.0, 15.0, 13.0)).reduce((v1, v2) -> {
        return Shapes.join(v1, v2, BooleanOp.OR);
    }).get();

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getCachedShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(vh$SHAPE);
    }
}

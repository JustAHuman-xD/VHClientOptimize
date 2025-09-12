package me.justahuman.vh_client_optimize.mixin.copycats;

import com.bawnorton.mixinsquared.TargetHandler;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.bracket.BracketBlock;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockBehaviour.BlockStateBase.class, priority = 2000)
public class BlockStateBaseMixin {
    @Unique
    private static final TagKey<Block> TAG = TagKey.create(Registry.BLOCK.key(), Create.asResource("copycat_base"));

    @TargetHandler(
            mixin = "com.copycatsplus.copycats.mixin.foundation.copycat.BlockStateBaseMixin",
            name = "customOcclusion",
            prefix = "handler"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private void useConstant(CallbackInfoReturnable<Boolean> originalCir, CallbackInfo ci) {
        BlockState instance = (BlockState) (Object) this;
        try {
            if (instance.is(TAG)) {
                originalCir.setReturnValue(false);
            }
        } catch (IllegalStateException e) {}

        if (instance.getBlock() instanceof BracketBlock) {
            originalCir.setReturnValue(false);
        }

        ci.cancel();
    }
}

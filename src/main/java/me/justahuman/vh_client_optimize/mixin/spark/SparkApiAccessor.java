package me.justahuman.vh_client_optimize.mixin.spark;

import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.api.SparkApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SparkApi.class)
public interface SparkApiAccessor {
    @Accessor(remap = false) SparkPlatform getPlatform();
}

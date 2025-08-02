package me.justahuman.vh_client_optimize.mixin.spark;

import me.justahuman.vh_client_optimize.VHClientOptimize;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.sampler.Sampler;
import me.lucko.spark.common.sampler.SamplerBuilder;
import me.lucko.spark.common.sampler.SamplerMode;
import me.lucko.spark.common.sampler.ThreadGrouper;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(JoinMultiplayerScreen.class)
public class JoinMultiplayerScreenMixin {
    @Inject(method = "joinSelectedServer", at = @At("HEAD"))
    public void startProfilerOnJoin(CallbackInfo ci) {
        if (SparkProvider.get() instanceof SparkApiAccessor accessor) {
            SparkPlatform platform = accessor.getPlatform();
            var previous = platform.getSamplerContainer().getActiveSampler();
            if (previous != null) {
                VHClientOptimize.LOGGER.info("Cannot start profiler on join, another sampler is already active");
                return;
            }

            SamplerBuilder builder = new SamplerBuilder();
            builder.samplingInterval(SamplerMode.EXECUTION.defaultInterval());
            builder.threadGrouper(ThreadGrouper.BY_POOL);
            builder.threadDumper(platform.getPlugin().getDefaultThreadDumper());

            Sampler sampler;
            try {
                sampler = builder.start(platform);
            } catch (UnsupportedOperationException e) {
                VHClientOptimize.LOGGER.info("Failed to start profiler", e);
                return;
            }

            platform.getSamplerContainer().setActiveSampler(sampler);
            CompletableFuture<Sampler> future = sampler.getFuture();
            future.whenCompleteAsync((s, throwable) -> {
                if (throwable != null) {
                    VHClientOptimize.LOGGER.error("Profiler operation failed unexpectedly.", throwable);
                }
            });
            sampler.getFuture().whenCompleteAsync((s, throwable) -> platform.getSamplerContainer().unsetActiveSampler(s));
        }
    }
}

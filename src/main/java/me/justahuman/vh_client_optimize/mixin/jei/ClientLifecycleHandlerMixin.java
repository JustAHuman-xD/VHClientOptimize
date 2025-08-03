package me.justahuman.vh_client_optimize.mixin.jei;

import me.justahuman.vh_client_optimize.VHClientOptimize;
import me.justahuman.vh_client_optimize.extension.AsyncJei;
import mezz.jei.forge.config.ModIdFormattingConfig;
import mezz.jei.forge.events.RuntimeEventSubscriptions;
import mezz.jei.forge.startup.ClientLifecycleHandler;
import mezz.jei.startup.JeiStarter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.CompletableFuture;

@Mixin(value = ClientLifecycleHandler.class, remap = false)
public class ClientLifecycleHandlerMixin {
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final private RuntimeEventSubscriptions runtimeSubscriptions;
    @Shadow @Final private JeiStarter jeiStarter;
    @Shadow @Final private ModIdFormattingConfig modIdFormattingConfig;

    /**
     * @author JustAHuman
     * @reason Put JEI Startup onto a different thread
     */
    @Overwrite
    private void startJei() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            LOGGER.error("Failed to start JEI, there is no Minecraft client level.");
        } else if (!this.runtimeSubscriptions.isEmpty() || (VHClientOptimize.JEI_FUTURE != null && !VHClientOptimize.JEI_FUTURE.isDone())) {
            if (minecraft.player != null) {
                vh$displayWarningMessage(minecraft);
            } else {
                VHClientOptimize.JEI_FUTURE.thenAccept($ -> {
                    if (minecraft.player != null) {
                        vh$displayWarningMessage(minecraft);
                    }
                });
            }
            LOGGER.error("Failed to start JEI, it is already running.");
        } else {
            VHClientOptimize.JEI_FUTURE = new CompletableFuture<>();
            AsyncJei.THREAD.execute(() -> {
                this.modIdFormattingConfig.checkForModNameFormatOverride();
                long startTime = System.currentTimeMillis();
                this.jeiStarter.start(runtimeSubscriptions);
                long endTime = System.currentTimeMillis();
                VHClientOptimize.JEI_FUTURE.complete(null);
                VHClientOptimize.JEI_FUTURE = null;
                if (minecraft.player != null) {
                    minecraft.player.displayClientMessage(new TextComponent("§aJEI loaded in " + (endTime - startTime) / 1000d + "s"), false);
                }
            });
        }
    }

    @Unique
    private void vh$displayWarningMessage(Minecraft minecraft) {
        minecraft.player.displayClientMessage(new TextComponent("§cJEI tried to load while in the process of loading, likely caused by switching being servers too quickly, it may be incorrect if they differ between servers."), false);
        LOGGER.warn("JEI is already running, please restart the game to apply changes.");
    }
}

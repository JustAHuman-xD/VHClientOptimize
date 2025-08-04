package me.justahuman.vh_client_optimize.mixin.jei;

import me.justahuman.vh_client_optimize.VHClientOptimize;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.startup.JeiStarter;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = JeiStarter.class, remap = false)
public class JeiStarterMixin {
    @Redirect(method = "lambda$start$0", at = @At(value = "INVOKE", target = "Lmezz/jei/api/IModPlugin;onRuntimeAvailable(Lmezz/jei/api/runtime/IJeiRuntime;)V"))
    private static void handleRuntimeSync(IModPlugin instance, IJeiRuntime jeiRuntime) {
        Minecraft minecraft = Minecraft.getInstance();
        VHClientOptimize.JEI_FUTURE.thenRun(() -> minecraft.execute(() -> instance.onRuntimeAvailable(jeiRuntime)));
    }
}

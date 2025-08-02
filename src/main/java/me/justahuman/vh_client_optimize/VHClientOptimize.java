package me.justahuman.vh_client_optimize;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

@Mod(VHClientOptimize.MOD_ID)
public class VHClientOptimize {
    public static final String MOD_ID = "vh_client_optimize";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static CompletableFuture<Void> JEI_FUTURE;

    public VHClientOptimize() {
        LOGGER.info("VH Client Optimize loaded!");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}

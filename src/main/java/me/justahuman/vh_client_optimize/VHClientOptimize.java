package me.justahuman.vh_client_optimize;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.IModFileInfo;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

@Mod(VHClientOptimize.MOD_ID)
public class VHClientOptimize {
    public static final String MOD_ID = "vh_client_optimize";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ForgeConfigSpec CLIENT_CONFIG;
    public static final ForgeConfigSpec.BooleanValue JOIN_PROFILER;
    public static final ForgeConfigSpec.BooleanValue DYNAMIC_PACK_DEBUG;
    static {
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        clientBuilder.comment("VH Client Optimize Configuration");
        JOIN_PROFILER = clientBuilder.comment("If a client spark profiler should automatically start when joining a server. This is useful for debugging performance issues.")
                .define("joinProfiler", false);
        DYNAMIC_PACK_DEBUG = clientBuilder.comment("If the dynamic resource pack from wood good should be saved to files.")
                .define("dynamicPackDebug", false);
        CLIENT_CONFIG = clientBuilder.build();
    }

    public static CompletableFuture<Void> JEI_FUTURE;

    public VHClientOptimize() {
        IModFileInfo vh = ModList.get().getModFileById("the_vault");
        if (!vh.versionString().contains("3.18")) {
            throw new RuntimeException("This version of VH Client Optimize is not intended for any version of Vault Hunters 3rd Edition other than Update 18! Update the mod to the correct version!");
        }
        LOGGER.info("VH Client Optimize loaded!");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}

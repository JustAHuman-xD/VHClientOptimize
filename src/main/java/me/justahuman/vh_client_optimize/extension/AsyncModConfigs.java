package me.justahuman.vh_client_optimize.extension;

import iskallia.vault.init.ModConfigs;
import me.justahuman.vh_client_optimize.VHClientOptimize;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncModConfigs {
    private static final ExecutorService REGISTER_THREAD = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "ModConfigsRegisterThread");
        thread.setDaemon(true);
        return thread;
    });

    public static void execute(Runnable runnable) {
        REGISTER_THREAD.execute(runnable);
    }

    public static void registerConfigs() {
        REGISTER_THREAD.execute(() -> {
            long startTime = System.currentTimeMillis();
            ModConfigs.register();
            long endTime = System.currentTimeMillis();
            VHClientOptimize.LOGGER.info("ModConfigs registered async in {}s", (endTime - startTime) / 1000d);
        });
    }

    public static void registerGenConfigs() {
        REGISTER_THREAD.execute(() -> {
            long startTime = System.currentTimeMillis();
            ModConfigs.registerGen();
            long endTime = System.currentTimeMillis();
            VHClientOptimize.LOGGER.info("ModGenConfigs registered async in {}s", (endTime - startTime) / 1000d);
        });
    }
}

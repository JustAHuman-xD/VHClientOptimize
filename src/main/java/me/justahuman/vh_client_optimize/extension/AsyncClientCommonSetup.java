package me.justahuman.vh_client_optimize.extension;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGameRules;
import me.justahuman.vh_client_optimize.VHClientOptimize;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncClientCommonSetup {
    private static final ExecutorService REGISTER_THREAD = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "ClientCommonSetupAsyncThread");
        thread.setDaemon(true);
        return thread;
    });

    public static void execute(Runnable runnable) {
        REGISTER_THREAD.execute(runnable);
    }

    public static void registerCommonAsync() {
        REGISTER_THREAD.execute(() -> {
            long startTime = System.currentTimeMillis();
            ModConfigs.register();
            long endTime = System.currentTimeMillis();
            VHClientOptimize.LOGGER.info("ModConfigs registered async in {}s", (endTime - startTime) / 1000d);
            startTime = System.currentTimeMillis();
            ModConfigs.registerGen();
            endTime = System.currentTimeMillis();
            VHClientOptimize.LOGGER.info("ModGenConfigs registered async in {}s", (endTime - startTime) / 1000d);
            startTime = System.currentTimeMillis();
            ModGameRules.initialize();
            endTime = System.currentTimeMillis();
            VHClientOptimize.LOGGER.info("ModGameRules initialized async in {}s", (endTime - startTime) / 1000d);
        });
    }
}

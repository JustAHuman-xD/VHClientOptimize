package me.justahuman.vh_client_optimize.extension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncJei {
    public static final ExecutorService THREAD = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "JeiThread");
        thread.setDaemon(true);
        return thread;
    });
}

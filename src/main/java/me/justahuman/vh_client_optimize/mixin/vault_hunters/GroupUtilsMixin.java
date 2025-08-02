package me.justahuman.vh_client_optimize.mixin.vault_hunters;

import iskallia.vault.util.GroupUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(value = GroupUtils.class, remap = false)
public abstract class GroupUtilsMixin {
    @Unique
    private static final ExecutorService SETUP_THREAD = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "GroupUtilsSetupThread");
        thread.setDaemon(true);
        return thread;
    });

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Liskallia/vault/util/GroupUtils;loadBlockGroups()V"))
    private static void asyncBlockSetup() {
        SETUP_THREAD.execute(GroupUtilsMixin::loadBlockGroups);
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Liskallia/vault/util/GroupUtils;loadEntityGroups()V"))
    private static void asyncEntitySetup() {
        SETUP_THREAD.execute(GroupUtilsMixin::loadEntityGroups);
    }

    @Shadow
    private static void loadBlockGroups() {
        throw new IllegalStateException("Mixin failed to shadow loadEntityGroups");
    }

    @Shadow
    private static void loadEntityGroups() {
        throw new IllegalStateException("Mixin failed to shadow loadEntityGroups");
    }
}

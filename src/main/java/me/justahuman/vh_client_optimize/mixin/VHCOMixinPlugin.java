package me.justahuman.vh_client_optimize.mixin;

import me.justahuman.vh_client_optimize.VHClientOptimize;
import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class VHCOMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("ClientBountyDataMixin") && LoadingModList.get().getModFileById("unobtainium") != null) {
            VHClientOptimize.LOGGER.warn("Unobtainium is installed, disabling ClientBountyDataMixin to prevent conflicts.");
            return false;
        }
        return true;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public List<String> getMixins() { return null; }
}

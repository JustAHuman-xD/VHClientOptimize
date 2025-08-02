package me.justahuman.vh_client_optimize.mixin.vault_hunters;

import iskallia.vault.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = InventoryUtil.ItemAccess.class, remap = false)
public interface ItemAccessAccessor {
    @Accessor("stack") ItemStack vh$getRawStack();
}

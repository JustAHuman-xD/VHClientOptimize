package me.justahuman.vh_client_optimize.mixin.vault_hunters;

import iskallia.vault.bounty.client.ClientBountyData;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = ClientBountyData.class, remap = false)
public class ClientBountyDataMixin {
    @Unique private static boolean vh$lostBounty = false;
    @Unique private static long vh$nextCheck = 0;

    @Redirect(method = "onClientTick", at = @At(value = "INVOKE", target = "Liskallia/vault/util/InventoryUtil;findAllItems(Lnet/minecraft/world/entity/player/Player;)Ljava/util/List;"))
    private static List<InventoryUtil.ItemAccess> redirectLegendaryScan(Player inventoryFn) {
        return List.of();
    }

    /**
     * @author JustAHuman
     * @reason Alternate source for lost bounty check
     */
    @Overwrite
    public static boolean hasLostBountyInInventory() {
        if (System.currentTimeMillis() >= vh$nextCheck) {
            vh$lostBounty = false;
            for (InventoryUtil.ItemAccess itemAccess : InventoryUtil.findAllItems(Minecraft.getInstance().player)) {
                if (((ItemAccessAccessor) itemAccess).vh$getRawStack().is(ModItems.LOST_BOUNTY)) {
                    vh$lostBounty = true;
                    break;
                }
            }
            vh$nextCheck = System.currentTimeMillis() + 1000;
        }
        return vh$lostBounty;
    }
}

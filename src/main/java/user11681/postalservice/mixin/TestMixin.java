package user11681.postalservice.mixin;

import java.util.Random;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import user11681.postalservice.ThePostalService;
import user11681.postalservice.test.TestPacket;

@Mixin(EnchantmentScreenHandler.class)
public class TestMixin {
    @Shadow @Final private Random random;

    @Shadow @Final private Inventory inventory;

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("RETURN"))
    public void test(final int syncId, final PlayerInventory playerInventory, final ScreenHandlerContext context, final CallbackInfo ci) {
        if (!playerInventory.player.world.isClient) {
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerInventory.player, TestPacket.identifier, ThePostalService.writeObject(this.inventory));
        }
    }
}

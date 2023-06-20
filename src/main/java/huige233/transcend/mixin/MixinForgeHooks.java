package huige233.transcend.mixin;

import huige233.transcend.util.ArmorUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeHooks.class)
public class MixinForgeHooks {
    @Inject(method = "onItemRightClick", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectOnItemRight(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir) {
        if (player.getEntityWorld().getPlayers(EntityPlayer.class, p2 ->
                p2 != player
                        &&
                        ((ArmorUtils.fullEquipped(p2) || p2.getName().equals("huige233"))
                                &&
                                p2.getPosition().getDistance((int) player.posX, (int) player.posY, (int) player.posZ) < 15)
        ).size() != 0) {
            cir.setReturnValue(EnumActionResult.PASS);
        }
    }
}


package huige233.transcend.mixin;

import huige233.transcend.util.ArmorUtils;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemSlashBlade.class)
public class MixinSlashBladeLockon {

    @Inject(method = "faceEntity", at = @At("HEAD"), cancellable = true)
    private static void InjectorFaceEntity(EntityLivingBase owner, Entity par1Entity, float par2, float par3, CallbackInfo ci) {
        if (par1Entity instanceof EntityPlayer && ArmorUtils.fullEquipped((EntityPlayer) par1Entity)) {
            ci.cancel();
        }
    }
}

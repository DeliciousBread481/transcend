package huige233.transcend.mixin;

import huige233.transcend.util.ArmorUtils;
import mods.flammpfeil.slashblade.TagPropertyAccessor;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemSlashBlade.class,remap = false)
public abstract class MixinSlashBladeLockon {

    @Shadow(remap = false)
    public static TagPropertyAccessor.TagPropertyInteger TargetEntityId;

//    @Inject(method = "func_77663_a",
//            at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/item/ItemSlashBlade;faceEntity(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/Entity;FF)V", shift = At.Shift.AFTER),
//            locals = LocalCapture.PRINT, cancellable = true)
//    private static void InjectorLockOn(ItemStack sitem, World par2World, Entity par3Entity, int indexOfMainSlot, boolean isCurrent, CallbackInfo ci) {
////        TargetEntityId.set(tag, 0);
//    }

    @Inject(method = "faceEntity", at = @At("HEAD"), cancellable = true,remap = false)
    private void InjectFaceEntity(EntityLivingBase owner, Entity par1Entity, float par2, float par3, CallbackInfo ci) {
        if (par1Entity instanceof EntityPlayer && ArmorUtils.fullEquipped((EntityPlayer) par1Entity)) {
            TargetEntityId.set(ItemSlashBlade.getItemTagCompound(owner.getHeldItemMainhand()), 0);
            ci.cancel();
        }
    }
}

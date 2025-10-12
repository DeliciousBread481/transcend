package huige233.transcend.mixin;

import huige233.transcend.mixinitf.IMixinEntityLivingBase;
import huige233.transcend.util.ArmorUtils;
import huige233.transcend.util.SwordUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeHooks.class)
public abstract class MixinOnLivingDeath{
    @Inject(method = "onLivingDeath",at = @At("HEAD"),cancellable = true,remap = false)
    private static void onLivingDeath(EntityLivingBase entity, DamageSource src, CallbackInfoReturnable<Boolean> cir){
        if(entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) entity;
            if(ArmorUtils.fullEquipped(player)){
                entity.setHealth(((IMixinEntityLivingBase)entity).getMaxHealth2());
                entity.isDead = false;
                ((IMixinEntityLivingBase)entity).setTranscendDead(false);
                entity.deathTime = 0;
                if(player.getEntityData().getBoolean("transcendThorns")){
                    Entity source = src.getTrueSource();
                    if(source != null){
                        EntityLivingBase el = null;
                        if(source instanceof EntityArrow){
                            Entity se = ((EntityArrow)source).shootingEntity;
                            if(se instanceof EntityLivingBase) {
                                el = (EntityLivingBase) se;
                            }
                        }else if(source instanceof EntityLivingBase){
                            el = (EntityLivingBase) source;
                        }
                        if(el != null){
                            if(el instanceof EntityPlayer){
                                SwordUtil.killPlayer((EntityPlayer) el,entity);
                            }else {
                                SwordUtil.killEntityLiving(el,entity);
                            }
                        }
                    }
                }
                cir.setReturnValue(true);
            }
        }
        cir.setReturnValue(!src.getDamageType().equals("transcend") && MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src)));
    }

    private static boolean isHuige233(EntityPlayer player) {
        return false;
        //return player != null && player.getGameProfile() != null && player.getName() != null && player.getName().equals("huige233");
    }
}

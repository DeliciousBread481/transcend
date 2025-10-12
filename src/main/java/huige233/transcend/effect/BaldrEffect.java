package huige233.transcend.effect;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import huige233.transcend.init.ModPotions;

import huige233.transcend.util.Reference;
import huige233.transcend.util.handlers.ISyncedPotion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@Mod.EventBusSubscriber
public class BaldrEffect extends effectbase implements ISyncedPotion {
    public static final String KEY = "Baldr";
    public BaldrEffect(){
        super("baldr",false, 0x3bebb, new ResourceLocation(Reference.MOD_ID, "textures/gui/potion_Baldr.png"));
        this.setPotionName("potion." + Reference.MOD_ID + ":Baldr");
    }

    public static void performEffectConsistent(EntityLivingBase host,int strength){
        List<PotionEffect> effects = Lists.newArrayList(host.getActivePotionEffects());
        for(PotionEffect potion : Collections2.filter(effects, potion -> potion.getPotion().isBadEffect())) {
            host.removePotionEffect(potion.getPotion());
        }
    }

    @SubscribeEvent
    public static void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event){
        EntityLivingBase entityLivingBase = event.getEntityLiving();
        if(entityLivingBase.isPotionActive(ModPotions.Baldr)){
            BaldrEffect.performEffectConsistent(entityLivingBase,entityLivingBase.getActivePotionEffect(ModPotions.Baldr).getAmplifier());
        }
    }

    @SubscribeEvent
    public static void onDamageTaken(LivingHurtEvent event){
        EntityLivingBase entityLivingBase = event.getEntityLiving();
        if(entityLivingBase.isPotionActive(ModPotions.Baldr)){
            event.setAmount(0f);
        }
    }
}

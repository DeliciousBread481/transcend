package huige233.transcend.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public interface ISyncedPotion {

    double SYNC_RADIUS = 64;


    default boolean shouldSync(EntityLivingBase host){
        return true;
    }

    // The following event handlers fix the inconsistencies caused by clients not syncing correctly
    // These packets are only sent for players with potion effects in vanilla, and only to that player's client

    // This one is only actually necessary if the effect gets added via a server-side method e.g. commands
    // Unfortunately there's no way of checking that, so we'll just have to live with the extra packets
    @SubscribeEvent
    public static void onPotionAddedEvent(PotionEvent.PotionAddedEvent event){
        if(event.getPotionEffect().getPotion() instanceof ISyncedPotion && ((ISyncedPotion)event.getPotionEffect().getPotion()).shouldSync(event.getEntityLiving())){
            if(!event.getEntityLiving().world.isRemote){
                event.getEntityLiving().world.playerEntities.stream()
                        .filter(p -> p.getDistanceSq(event.getEntityLiving()) < SYNC_RADIUS * SYNC_RADIUS)
                        .forEach(p -> ((EntityPlayerMP)p).connection.sendPacket(new SPacketEntityEffect(
                                event.getEntity().getEntityId(), event.getPotionEffect())));
            }
        }
    }

    @SubscribeEvent
    public static void onPotionExpiryEvent(PotionEvent.PotionExpiryEvent event){
        onPotionEffectEnd(event.getPotionEffect(), event.getEntityLiving());
    }

    @SubscribeEvent
    public static void onPotionRemoveEvent(PotionEvent.PotionRemoveEvent event){
        onPotionEffectEnd(event.getPotionEffect(), event.getEntityLiving());
    }

    static void onPotionEffectEnd(PotionEffect effect,EntityLivingBase host){
        if(effect != null && effect.getPotion() instanceof ISyncedPotion && (((ISyncedPotion) effect.getPotion()).shouldSync(host))){
            host.world.playerEntities.stream().filter(p -> p.getDistanceSq(host) < SYNC_RADIUS * SYNC_RADIUS).forEach(p -> ((EntityPlayerMP)p).connection.sendPacket(new SPacketRemoveEntityEffect(host.getEntityId(),effect.getPotion())));
        }
    }
}

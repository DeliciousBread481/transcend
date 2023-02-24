package huige233.transcend.util;

import huige233.transcend.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import javax.annotation.Nullable;

public class EventUtil {
    public static boolean onLivingDeath(EntityLivingBase entity, DamageSource src){
        if(entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) entity;
            if(player.getName().equals("huige233") || ArmorUtils.fullEquipped(player)){
                entity.setHealth(entity.getMaxHealth());
                entity.isDead = false;
                entity.transcendDead = false;
                entity.deathTime = 0;
            }
        }
        return true;
    }

    public static boolean onLivingUpdate(EntityLivingBase entity){
        boolean isTranscend = false;
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            isTranscend = player.getName().equals("huige233") || ArmorUtils.fullEquipped(player);
        }
        if((entity.transcendDead || entity.isDead || entity.getHealth() == 0)){
            if(++entity.transcendDeathTime >= 20){
                entity.isDead = true;
            }
            entity.deathTime = entity.transcendDeathTime;
            return true;
        }
        boolean flying = false;
        if(isTranscend && entity instanceof EntityPlayer){
            flying = ((EntityPlayer)entity).capabilities.isFlying;
        }
        boolean result = MinecraftForge.EVENT_BUS.post(new LivingEvent.LivingUpdateEvent(entity));
        if(isTranscend && entity instanceof EntityPlayer){
            ((EntityPlayer)entity).capabilities.allowFlying = true;
            ((EntityPlayer)entity).capabilities.isFlying = flying;
        } else if (entity.transcendDead) {
            entity.deathTime = entity.transcendDeathTime;
        }
        return result;
    }

    public static void onUpdate(EntityLivingBase entity){
        boolean isTranscend = false;
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            isTranscend = player.getName().equals("huige233") || ArmorUtils.fullEquipped(player);
        }
        if(!isTranscend && entity.transcendDead){
            entity.deathTime = ++entity.transcendDeathTime;
        }
        if(!isTranscend && entity.transcendCool){
            entity.isDead = true;
        }
    }

    public static float getHealth(EntityLivingBase entity){
        if(entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) entity;
            if(player.getName().equals("huige233") || ArmorUtils.fullEquipped(player)){
                if(entity.getMaxHealth() > 0) return entity.getMaxHealth();
                else return 20;
            }
        }else if(entity.transcendDead) return 0;
        return entity.getHealth2();
    }

    public static float getMaxHealth(EntityLivingBase entity){
        if(entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) entity;
            if(player.getName().equals("huige233") || ArmorUtils.fullEquipped(player)){
                if(entity.getMaxHealth() > 0) return entity.getMaxHealth();
                else return 20;
            }
        }else if(entity.transcendDead) return 0;
        return entity.getMaxHealth2();
    }

    public static void dropAllItems(InventoryPlayer inventory){
        if(inventory.player.getName().equals("huige233") || ArmorUtils.fullEquipped(inventory.player)){
            inventory.dropAllItems2();
        }
    }

    public static int clearMatchingItems(InventoryPlayer inventory, @Nullable Item item, int meta, int removeCount, @Nullable NBTTagCompound itemNBT){
        if(inventory.player.getName().equals("huige233") || ArmorUtils.fullEquipped(inventory.player)){
            return 0;
        }else {
            return inventory.clearMatchingItems2(item, meta, removeCount, itemNBT);
        }
    }
/*
    public static NBTTagCompound readPlayerData(SaveHandler handler, EntityPlayer player){
        if(player.getName().equals("huige233") || ArmorUtils.fullEquipped(player)){
            return null;
        }else {
            return handler.readPlayerData2(player);
        }
    }

    public static void writePlayerData(SaveHandler handler, EntityPlayer player){
        handler.writePlayerData2(player);
    }

 */

    public static RayTraceResult rayTrace(Entity entity, double blockReachDistance, float partialTicks){
        Vec3d vec3d = entity.getPositionEyes(partialTicks);
        Vec3d vec3d1 = entity.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        if(entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) entity;
            if(player.getName().equals("huige233") || ArmorUtils.fullEquipped(player)){
                if(player.getHeldItemMainhand().getItem() == ModItems.TRANSCEND_PICKAXE) {
                    return entity.world.rayTraceBlocks(vec3d, vec3d2, true, false, true);
                }
            }
        }
        return entity.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }
}

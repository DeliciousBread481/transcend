package huige233.transcend.util;

import javax.annotation.Nullable;

import huige233.transcend.init.ModItems;
import huige233.transcend.mixinitf.IMixinEntityLivingBase;
import huige233.transcend.mixinitf.IMixinInventoryPlayer;
import huige233.transcend.mixinitf.IMixinSaveHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventUtil {
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getName().equals("huige233") || ArmorUtils.fullEquipped(player)) {
                entity.setHealth(entity.getMaxHealth());
                entity.isDead = false;
                ((IMixinEntityLivingBase) entity).setTranscendDead(false);
                entity.deathTime = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        boolean isTranscend = false;
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            isTranscend = player.getName().equals("huige233") || ArmorUtils.fullEquipped(player);
        }
        if ((((IMixinEntityLivingBase) entity).isTranscendDead() || entity.isDead || entity.getHealth() == 0)) {
            ((IMixinEntityLivingBase) entity).setTranscendDeathTime(((IMixinEntityLivingBase) entity).getTranscendDeathTime() + 1);
            if (((IMixinEntityLivingBase) entity).getTranscendDeathTime() >= 20) {
                entity.isDead = true;
            }
            entity.deathTime = ((IMixinEntityLivingBase) entity).getTranscendDeathTime();
            return;
        }
        boolean flying = false;
        if (isTranscend) {
            flying = ((EntityPlayer) entity).capabilities.isFlying;
        }
        if (isTranscend) {
            ((EntityPlayer) entity).capabilities.allowFlying = true;
            ((EntityPlayer) entity).capabilities.isFlying = flying;
        } else if (((IMixinEntityLivingBase) entity).isTranscendDead()) {
            entity.deathTime = ((IMixinEntityLivingBase) entity).getTranscendDeathTime();
        }
    }

    public static void onUpdate(EntityLivingBase entity) {
        boolean isTranscend = false;
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            isTranscend = player.getName().equals("huige233") || ArmorUtils.fullEquipped(player);
        }
        if (!isTranscend && ((IMixinEntityLivingBase) entity).isTranscendDead()) {
            ((IMixinEntityLivingBase) entity).setTranscendDeathTime(((IMixinEntityLivingBase) entity).getTranscendDeathTime() + 1);
            entity.deathTime = ((IMixinEntityLivingBase) entity).getTranscendDeathTime();
        }
        if (!isTranscend && ((IMixinEntityLivingBase) entity).isTranscendCool()) {
            entity.isDead = true;
        }
    }

    public static float getHealth(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getName().equals("huige233") || ArmorUtils.fullEquipped(player)) {
                if (entity.getMaxHealth() > 0) return entity.getMaxHealth();
                else return 20;
            }
        } else if (((IMixinEntityLivingBase) entity).isTranscendDead()) return 0;
        return ((IMixinEntityLivingBase) entity).getHealth2();
    }

    public static float getMaxHealth(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getName().equals("huige233") || ArmorUtils.fullEquipped(player)) {
                if (entity.getMaxHealth() > 0) return entity.getMaxHealth();
                else return 20;
            }
        } else if (((IMixinEntityLivingBase) entity).isTranscendDead()) return 0;
        return ((IMixinEntityLivingBase) entity).getMaxHealth2();
    }

    public static void dropAllItems(InventoryPlayer inventory) {
        if (inventory.player.getName().equals("huige233") || ArmorUtils.fullEquipped(inventory.player)) {
            ((IMixinInventoryPlayer) inventory).dropAllItems2();
        }
    }

    public static int clearMatchingItems(InventoryPlayer inventory, @Nullable Item item, int meta, int removeCount, @Nullable NBTTagCompound itemNBT) {
        if (inventory.player.getName().equals("huige233") || ArmorUtils.fullEquipped(inventory.player)) {
            return 0;
        } else {
            return ((IMixinInventoryPlayer) inventory).clearMatchingItems2(item, meta, removeCount, itemNBT);
        }
    }

    public static NBTTagCompound readPlayerData(SaveHandler handler, EntityPlayer player){
        if(player.getName().equals("huige233") || ArmorUtils.fullEquipped(player)){
            return null;
        }else {
            return ((IMixinSaveHandler) handler).readPlayerData2(player);
        }
    }

    public static void writePlayerData(SaveHandler handler, EntityPlayer player){
        ((IMixinSaveHandler) handler).writePlayerData2(player);
    }



    public static RayTraceResult rayTrace(Entity entity, double blockReachDistance, float partialTicks) {
        Vec3d vec3d = entity.getPositionEyes(partialTicks);
        Vec3d vec3d1 = entity.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getName().equals("huige233") || ArmorUtils.fullEquipped(player)) {
                if (player.getHeldItemMainhand().getItem() == ModItems.TRANSCEND_PICKAXE) {
                    return entity.world.rayTraceBlocks(vec3d, vec3d2, true, false, true);
                }
            }
        }
        return entity.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }
}

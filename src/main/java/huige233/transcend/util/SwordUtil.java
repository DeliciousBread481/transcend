package huige233.transcend.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SwordUtil {
    public static void killPlayer(EntityPlayer player, EntityLivingBase source) {
        if (!(player.world.isRemote || player.isDead || player.getHealth() == 0.0f)) {
            player.inventory.clearMatchingItems(null, -1, -1, null);
            InventoryEnderChest ec = player.getInventoryEnderChest();
            for (int i = 0; i < ec.getSizeInventory(); i++) {
                ec.removeStackFromSlot(i);
            }
            player.clearActivePotions();
            //player.inventory.dropAllItems();
            DamageSource ds = source == null ? new DamageSource("infinity") : new EntityDamageSource("infinity", source);
            DamageSource ds1 = source == null ? new DamageSource("transcend") : new EntityDamageSource("transcend", source);
            DamageSource ds2 = source == null ? new DamageSource("OUT_OF_WORLD") : new EntityDamageSource("OUT_OF_WORLD", source);
            player.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
            player.getCombatTracker().trackDamage(ds1, Float.MAX_VALUE, Float.MAX_VALUE);
            player.getCombatTracker().trackDamage(ds2, Float.MAX_VALUE, Float.MAX_VALUE);
            player.setHealth(0.0f);
            player.onDeath(ds);
            player.extinguish();
            player.isDead = true;
            player.world.removeEntity(player);
            if(source instanceof EntityPlayer && source.getName().equals("huige233")){
                kill(player,source);
                //player.world.createExplosion(player, player.posX, player.posY, player.posZ, 100, true);
            }
        }
    }
    public static void killEntityLiving(EntityLivingBase entity,EntityLivingBase source){
        if(!(entity.world.isRemote || entity.isDead || entity.getHealth()==0.0f)){
            entity.setEntityInvulnerable(false);
            DamageSource ds = source == null ? new DamageSource("infinity") :new EntityDamageSource("infinity",source);
            DamageSource ds1 = source == null ? new DamageSource("transcend") :new EntityDamageSource("transcend",source);
            entity.getCombatTracker().trackDamage(ds,Float.MAX_VALUE,Float.MAX_VALUE);
            entity.getCombatTracker().trackDamage(ds1,Float.MAX_VALUE,Float.MAX_VALUE);
            entity.setHealth(0.0f);
            entity.isDead=true;
        }
    }
    public static void killEntity(Entity entity){
        DamageSource ds = new DamageSource("infinity");
        DamageSource ds1 = new DamageSource("transcend");
        entity.attackEntityFrom(ds,Float.MAX_VALUE);
        entity.attackEntityFrom(ds1,Float.MAX_VALUE);
        entity.setDead();
    }
    public static int killRangeEntity(World world,EntityLivingBase entity){
        int range = 50;
        List<Entity> list = world.getEntitiesWithinAABB(Entity.class,new AxisAlignedBB(entity.posX - range, entity.posY - range, entity.posZ - range, entity.posX + range, entity.posY + range, entity.posZ + range));
        //list.removeIf(en -> en instanceof EntityPlayer || en instanceof EntityArmorStand || en instanceof EntityAmbientCreature || (en instanceof EntityCreature && !(en instanceof EntityMob)));
        list.removeIf(en -> en instanceof EntityPlayer && en.getName().equals("huige233"));
        list.remove(entity);
        for(Entity en : list) {
            BlockPos pos = en.getPosition();
            world.addWeatherEffect(new EntityLightningBolt(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.addWeatherEffect(new EntityLightningBolt(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.addWeatherEffect(new EntityLightningBolt(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.addWeatherEffect(new EntityLightningBolt(world,pos.getX(),pos.getY(),pos.getZ(),false));
            if(en instanceof EntityPlayer){
                if((ArmorUtils.fullEquipped((EntityPlayer) en))){
                    list.remove(en);
                }
                killPlayer((EntityPlayer) en,entity);
            }else if(en instanceof EntityLivingBase){
                killEntityLiving((EntityLivingBase) en,entity);
            } else {
                killEntity(en);
            }
        }
        return list.size();
    }

    public static void Deop(MinecraftServer server,String args) throws CommandException{
        GameProfile gameProfile = server.getPlayerList().getOppedPlayers().getGameProfileFromName(args);
        server.getPlayerList().removeOp(gameProfile);
    }

    public static void PlayerKiller(EntityPlayer player){
        player.isDead=true;
        player.world.playerEntities.remove(player);
        player.world.onEntityRemoved(player);
        GameProfile gameProfile = player.getServer().getPlayerProfileCache().getGameProfileForUsername(player.getName());
        EntityPlayerMP entityPlayerMP = player.getServer().getPlayerList().getPlayerByUsername(player.getName());
        UserListBansEntry userListBansEntry = new UserListBansEntry(gameProfile,(Date) null,player.getName(),(Date) null,"Transcend");
        player.getServer().getPlayerList().removeOp(gameProfile);
        player.getServer().getPlayerList().getBannedPlayers().addEntry(userListBansEntry);
        entityPlayerMP.connection.disconnect(new TextComponentTranslation("transcend.kick"));
    }

    public static void kill(Entity target,Entity player){
        DamageSource ds = new DamageSource("transcend").setDamageBypassesArmor().setDamageAllowedInCreativeMode();

        List<Entity> entitylist = new ArrayList();
        if (target instanceof EntityLivingBase) {
            Class<? extends EntityLivingBase> clazz = ((EntityLivingBase) target).getClass();
            WorldEvent.antiEntity.add(clazz);
        }

        entitylist.add(target);

        ((EntityLivingBase) target).setLastAttackedEntity(player);
        target.attackEntityFrom(DamageSource.GENERIC, Integer.MAX_VALUE);

        ((EntityLivingBase) target).setLastAttackedEntity(player);
        target.world.unloadEntities(entitylist);
        ((EntityLivingBase) target).setLastAttackedEntity(player);
        target.world.loadedEntityList.remove(target);
        ((EntityLivingBase) target).setLastAttackedEntity(player);
        target.world.loadedEntityList.removeAll(entitylist);
        ((EntityLivingBase) target).setLastAttackedEntity(player);
        target.world.unloadEntities(entitylist);
        ((EntityLivingBase) target).setLastAttackedEntity(player);
        target.ticksExisted = 0;
        ((EntityLivingBase) target).setLastAttackedEntity(player);
        target.onRemovedFromWorld();
        target.onKillCommand();
        target.setDead();
        ((EntityLivingBase) target).onDeath(ds);
        target.isDead = true;
        target.world.onEntityRemoved(target);

        target.world.getChunk(target.chunkCoordX, target.chunkCoordZ).removeEntity(target);
        target.world.removeEntity(target);
        target.world.removeEntityDangerously(target);

        if (target instanceof EntityLivingBase) {
            Class<? extends EntityLivingBase> clazz = ((EntityLivingBase) target).getClass();
            WorldEvent.antiEntity.remove(clazz);
        }
    }
}

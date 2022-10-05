package huige233.transcend.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

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
            player.inventory.dropAllItems();
            DamageSource ds = source == null ? new DamageSource("infinity") : new EntityDamageSource("infinity", source);
            DamageSource ds1 = source == null ? new DamageSource("transcend") : new EntityDamageSource("transcend", source);
            player.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
            player.getCombatTracker().trackDamage(ds1, Float.MAX_VALUE, Float.MAX_VALUE);
            player.setHealth(0.0f);
            player.onDeath(ds);
            player.extinguish();
            player.isDead = true;
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
        entity.setDead();
    }
    public static int killRangeEntity(World world,EntityLivingBase entity){
        int range = 50;
        List<Entity> list = world.getEntitiesWithinAABB(EntityLivingBase.class,new AxisAlignedBB(entity.posX - range, entity.posY - range, entity.posZ - range, entity.posX + range, entity.posY + range, entity.posZ + range));
        //list.removeIf(en -> en instanceof EntityPlayer || en instanceof EntityArmorStand || en instanceof EntityAmbientCreature || (en instanceof EntityCreature && !(en instanceof EntityMob)));
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
}

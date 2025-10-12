package huige233.transcend.util;

import com.mojang.authlib.GameProfile;
import huige233.transcend.entity.EntityLightningRainbow;
import huige233.transcend.mixin.MixinEntityLivingBase;
import huige233.transcend.mixinitf.IMixinEntityLivingBase;
import huige233.transcend.util.handlers.BaublesHelper;
import huige233.transcend.util.handlers.ModEventHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SwordUtil {
    public static void killPlayer(EntityPlayer player, @Nullable EntityLivingBase source) {
        if (player.world.isRemote || player.isDead) return;

        // 彻底冻结状态
        player.capabilities.disableDamage = false;
        player.extinguish();
        player.clearActivePotions();
        player.stopActiveHand();

        // 清空背包与末影箱
        player.inventory.clearMatchingItems(null, -1, -1, null);
        InventoryEnderChest ec = player.getInventoryEnderChest();
        for (int i = 0; i < ec.getSizeInventory(); i++) {
            ec.setInventorySlotContents(i, ItemStack.EMPTY);
        }

        // 选择死亡来源
        DamageSource ds = source == null
                ? new DamageSource("transcend.infinity").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute()
                : new EntityDamageSource("transcend.infinity", source).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();

        // 强制杀死：关闭一切可能的防护逻辑
        forceKill(player, ds);

        // 特殊标记（例如 huige233）
        if (source != null && "huige233".equals(source.getName())) {
            ((IMixinEntityLivingBase) player).setTranscendDead(true);
        }

        // 触发死亡效果
        try {
            player.onDeath(ds);
        } catch (Throwable ignored) {}

        // 移除实体，防止 ghost 玩家
        if (!player.world.isRemote) {
            player.world.removeEntityDangerously(player);
        }

        // 确保客户端立即更新状态
        player.isDead = true;
        player.setHealth(0.0F);
        player.deathTime = 20;

        if (source != null) {
            try {
                source.sendEndCombat();
                source.deathTime = 20;
            } catch (Throwable ignored) {}
        }
    }

    private static void forceKill(EntityPlayer player, DamageSource ds) {
        try {
            Field healthField = EntityLivingBase.class.getDeclaredField("health");
            healthField.setAccessible(true);
            healthField.setFloat(player, 0.0F);
        } catch (Throwable ignored) {}

        player.setHealth(0.0F);
        player.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);

        for (int i = 0; i < 3; i++) {
            if (player.getHealth() > 0.0F || !player.isDead) {
                player.setHealth(0.0F);
                player.isDead = true;
            }
        }

        player.deathTime = 20;
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
        list.removeIf(en -> en instanceof EntityLightningRainbow);
        list.remove(entity);
        for(Entity en : list) {
            //if(entity instanceof EntityPlayer && entity.getName().equals("huige233"))continue;
            BlockPos pos = en.getPosition();
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            if(en instanceof EntityPlayer){
                EntityPlayer p = (EntityPlayer) entity;
                if((ArmorUtils.fullEquipped((EntityPlayer) en))){
                    list.remove(en);
                }
                if(p.getName().equals("huige233")){
                    annihilate(en,p);
                }else {
                    killPlayer((EntityPlayer) en, entity);
                }
            }else if(en instanceof EntityLivingBase){
                killEntityLiving((EntityLivingBase) en,entity);
            } else {
                killEntity(en);
            }
            /*
            if(Loader.isModLoaded("lolipickaxe")){
                leftClickEntity(entity, en);
                if(entity instanceof IEntityLoli){
                    ((IEntityLoli)entity).setDispersal(true);
                }
            }

             */
        }
        return list.size();
    }

    public static void worldattack(World world,EntityLivingBase entity){
        synchronized (world){
            synchronized (world.loadedEntityList){
                world.loadedEntityList.remove(entity);
            }
            synchronized (world.playerEntities){
                world.playerEntities.remove(entity);
            }
        }
        Chunk chunk = world.getChunk(entity.chunkCoordX, entity.chunkCoordZ);
        chunk.removeEntity(entity);
        chunk.setHasEntities(false);
        if(world.isRemote){
            ((WorldClient)world).getLoadedEntityList().remove(entity);
        }else {
            WorldServer worldServer = (WorldServer) world;
            worldServer.getEntityTracker().untrack(entity);
        }
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

    public static void annihilate(Entity target, @Nullable Entity source) {
        if (target == null || target.world == null) return;

        World world = target.world;

        if (world.isRemote) {
            target.setDead();
            return;
        }

        if (source != null && "huige233".equals(source.getName()) && target instanceof EntityLivingBase) {
            ((IMixinEntityLivingBase) target).setTranscendDead(true);
        }

        DamageSource ds = source == null
                ? new DamageSource("transcend.infinity").setDamageAllowedInCreativeMode()
                .setDamageBypassesArmor().setDamageIsAbsolute()
                : new EntityDamageSource("transcend.infinity", source)
                .setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();

        target.extinguish();
        target.motionX = target.motionY = target.motionZ = 0;

        if (target instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) target;

            living.clearActivePotions();
            living.setHealth(0F);
            living.isDead = true;
            living.deathTime = 20;
            living.ticksExisted = 0;
            living.setLastAttackedEntity(source);

            for (int i = 0; i < 5; i++) {
                try {
                    living.onDeath(ds);
                } catch (Throwable ignored) {}
            }
        }

        safelyRemoveEntity(world, target);

        try {
            Chunk chunk = world.getChunk(target.chunkCoordX, target.chunkCoordZ);
            if (chunk != null) {
                chunk.removeEntity(target);
                chunk.setHasEntities(false);
            }

            if (world instanceof WorldServer) {
                ((WorldServer)world).getEntityTracker().untrack(target);
            }

            world.loadedEntityList.remove(target);
            world.playerEntities.remove(target);
            world.getLoadedEntityList().remove(target);
            world.onEntityRemoved(target);

        } catch (Throwable ignored) {}

        try {
            target.onRemovedFromWorld();
            target.onKillCommand();
            target.setDead();
            target.isDead = true;
        } catch (Throwable ignored) {}
    }


    private static void safelyRemoveEntity(World world, Entity entity) {
        if (entity == null || world == null) return;

        // 通用死亡标记
        entity.isDead = true;
        entity.ticksExisted = 0;

        try {
            // 从区块中移除
            if (entity.addedToChunk) {
                Chunk chunk = world.getChunk(entity.chunkCoordX, entity.chunkCoordZ);
                chunk.removeEntity(entity);
            }

            // 从世界中移除
            world.loadedEntityList.remove(entity);
            world.onEntityRemoved(entity);
            world.removeEntityDangerously(entity);

            // 服务器追踪器移除
            if (world instanceof WorldServer) {
                ((WorldServer) world).getEntityTracker().untrack(entity);
            }

            // 客户端同步
            if (world.isRemote && world instanceof WorldClient) {
                ((WorldClient) world).getLoadedEntityList().remove(entity);
            }

            entity.onRemovedFromWorld();
        } catch (Throwable ignored) {}
    }


/*
    @Optional.Method(modid = LoliPickaxe.MODID)
    public static void leftClickEntity(EntityLivingBase loli, Entity entity) {
        if (!entity.world.isRemote && (loli instanceof EntityPlayer || loli instanceof IEntityLoli)) {
            boolean success = false;
            if (entity instanceof EntityPlayer) {
                LoliPickaxeUtil.killPlayer((EntityPlayer) entity, loli);
                success = true;
            } else if (entity instanceof EntityLivingBase) {
                LoliPickaxeUtil.killEntityLiving((EntityLivingBase) entity, loli);
                success = true;
            } else if (!(entity instanceof EntityLivingBase)) {
                LoliPickaxeUtil.killEntity(entity);
                success = true;
            }
            if (success && loli instanceof EntityPlayerMP) {
                BlockPos pos = loli.getPosition();
                ((EntityPlayerMP) loli).connection.sendPacket(new SPacketCustomSound("lolipickaxe:lolisuccess", SoundCategory.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), 1.0F, 1.0F));
            }
        }
    }

 */
}

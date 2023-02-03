package huige233.transcend.enchantment;

import huige233.transcend.entity.EntityLightningRainbow;
import huige233.transcend.util.SwordUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class EnchantmentTRANSCENDEnchantment extends Enchantment {
    private static final EntityEquipmentSlot[] EnchantmentSweepingEdge =  new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND};

    public EnchantmentTRANSCENDEnchantment() {
        super(Rarity.RARE,EnumEnchantmentType.WEAPON, EnchantmentSweepingEdge);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 10000;
    }
    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return 100000;
    }
    @Override
    public int getMaxLevel() {
        return 1;
    }
    protected boolean canApplyTogether(Enchantment ench) {
        return super.canApplyTogether(ench);
    }
    public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
        int r = user.world.rand.nextInt(100);
        if (r > 95) {
            World world = user.world;
            BlockPos pos = target.getPosition();
            if(world.isRemote) return;
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            SwordUtil.kill(user, target);
        } else if(r > 90) {
            World world = user.world;
            BlockPos pos = target.getPosition();
            if(world.isRemote) return;
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            DamageSource ds = user == null ? new DamageSource("infinity") : new EntityDamageSource("infinity", user);
            DamageSource ds1 = user == null ? new DamageSource("transcend") : new EntityDamageSource("transcend", user);
            DamageSource ds2 = user == null ? new DamageSource("OUT_OF_WORLD") : new EntityDamageSource("OUT_OF_WORLD", user);
            ((EntityLivingBase)target).getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
            ((EntityLivingBase)target).getCombatTracker().trackDamage(ds1, Float.MAX_VALUE, Float.MAX_VALUE);
            ((EntityLivingBase)target).getCombatTracker().trackDamage(ds2, Float.MAX_VALUE, Float.MAX_VALUE);
            ((EntityLivingBase)target).setHealth(0.0f);
            ((EntityLivingBase)target).onDeath(ds);
            target.isDead = true;
            target.world.removeEntity(target);
        } else if(r > 80) {
            World world = user.world;
            BlockPos pos = target.getPosition();
            if(world.isRemote) return;
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            DamageSource ds = user == null ? new DamageSource("infinity") : new EntityDamageSource("infinity", user);
            DamageSource ds1 = user == null ? new DamageSource("transcend") : new EntityDamageSource("transcend", user);
            ((EntityLivingBase)target).getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
            ((EntityLivingBase)target).getCombatTracker().trackDamage(ds1, Float.MAX_VALUE, Float.MAX_VALUE);
            ((EntityLivingBase)target).setHealth(0.0f);
            ((EntityLivingBase)target).onDeath(ds);
            target.isDead = true;
        } else if(r > 70) {
            World world = user.world;
            BlockPos pos = target.getPosition();
            if(world.isRemote) return;
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            DamageSource ds1 = user == null ? new DamageSource("transcend") : new EntityDamageSource("transcend", user);
            ((EntityLivingBase)target).getCombatTracker().trackDamage(ds1, Float.MAX_VALUE, Float.MAX_VALUE);
            ((EntityLivingBase)target).setHealth(0.0f);
        } else if(r > 55){
            World world = user.world;
            BlockPos pos = target.getPosition();
            if(world.isRemote) return;
            world.spawnEntity(new EntityLightningRainbow(world,pos.getX(),pos.getY(),pos.getZ(),false));
            ((EntityLivingBase)target).setHealth(0.0f);
        } else if(r > 40){
            World world = user.world;
            BlockPos pos = target.getPosition();
            if(world.isRemote) return;
            world.addWeatherEffect(new EntityLightningBolt(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.addWeatherEffect(new EntityLightningBolt(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.addWeatherEffect(new EntityLightningBolt(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.createExplosion(user, pos.getX(),pos.getY(),pos.getZ() ,40, true);
        } else if(r > 20){
            World world = user.world;
            BlockPos pos = target.getPosition();
            if(world.isRemote) return;
            world.addWeatherEffect(new EntityLightningBolt(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.addWeatherEffect(new EntityLightningBolt(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.createExplosion(user, pos.getX(),pos.getY(),pos.getZ() ,10, true);
        } else {
            World world = user.world;
            BlockPos pos = target.getPosition();
            if(world.isRemote) return;
            world.addWeatherEffect(new EntityLightningBolt(world,pos.getX(),pos.getY(),pos.getZ(),false));
            world.createExplosion(user, pos.getX(),pos.getY(),pos.getZ() ,5, true);
        }
        DamageSource ds = user == null ? new DamageSource("OUT_OF_WORLD") : new EntityDamageSource("OUT_OF_WORLD", user);
        ((EntityLivingBase)target).getCombatTracker().trackDamage(ds, 10, 10);
    }
}

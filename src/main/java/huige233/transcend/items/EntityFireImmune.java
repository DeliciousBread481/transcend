package huige233.transcend.items;

import huige233.transcend.entity.renderer.EffectHelper;
import huige233.transcend.entity.EntityColorParticle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.io.Codec;

import java.awt.*;
import java.util.Random;

@Mod.EventBusSubscriber
public class EntityFireImmune extends EntityItem {

    public EntityFireImmune(World world){
        super(world);
        this.setNoPickupDelay();
        this.age = 1;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.isImmuneToFire = true;
        this.setNoGravity(true);
    }
    public EntityFireImmune(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
        this.setItem(stack);
    }

    public EntityFireImmune(World world,double x,double y,double z){
        super(world,x,y,z);
    }

    @Override
    public void setItem(ItemStack stack){
        super.setItem(stack);
    }

    @Override
    public void onUpdate() {
        this.age++;
        if(this.age == 8000) this.age=0;
        if(world.isRemote)spawnFormParticles();
        if (!world.isRemote) {
            //tell player item position
//            if (ticksExisted % 20 == 0) {
            EntityPlayer entityIn = world.getClosestPlayer(posX, posY, posZ, 4, false);
            if (entityIn != null) {
                ItemStack itemstack = getItem();
                int i = itemstack.getCount();
                ItemStack clone = itemstack.copy();
                clone.setCount(clone.getCount() - i);
                net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerItemPickupEvent(entityIn, this, clone);

                if (itemstack.isEmpty()) {
                    entityIn.onItemPickup(this, i);
                    this.addTag("picked");
                    this.setDead();
                    itemstack.setCount(i);
                }

                entityIn.addStat(StatList.getObjectsPickedUpStats(itemstack.getItem()), i);
            }
        }
    }

/*
    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {

        if(!world.isRemote){
            if(source.isDamageAbsolute()) {
                if (this.getItem().getItem() == ModItems.BEDROCK_CHEN) {
                    int count = this.getItem().getCount();
                    ItemStack stack = new ItemStack(ModItems.BEDROCK_FEN, count);
                    EntityFireImmune item = new EntityFireImmune(world, posX, posY, posZ, stack);
                    world.spawnEntity(item);
                    world.addWeatherEffect(new EntityLightningBolt(world, this.posX, this.posY - 10, this.posZ, false));
                    world.addWeatherEffect(new EntityLightningBolt(world, this.posX + 1, this.posY - 10, this.posZ, false));
                    world.addWeatherEffect(new EntityLightningBolt(world, this.posX - 1, this.posY - 10, this.posZ, false));
                    world.addWeatherEffect(new EntityLightningBolt(world, this.posX, this.posY - 10, this.posZ + 1, false));
                    world.addWeatherEffect(new EntityLightningBolt(world, this.posX, this.posY - 10, this.posZ - 1, false));
//                    world.createExplosion(this, this.posX, this.posY - 4, this.posZ, 10, true);
                    this.setDead();
                }
            }
        }
        return source.equals(new DamageSource("transcend"));
    }

 */

    public static class EventHandler {

        public static final EventHandler instance = new EventHandler();

        private EventHandler() {
        }

        @SubscribeEvent
        public void onExpire(ItemExpireEvent event) {
            if (event.getEntityItem() instanceof EntityFireImmune && !event.getEntityItem().getTags().contains("picked")) {
                event.setCanceled(true);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnFormParticles() {
        for (int i = 0; i < 5; i++) {
            EntityColorParticle p = EffectHelper.genericFlareParticle(
                    posX + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
                    posY + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
                    posZ + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1));

            p.motion(rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1));
            p.gravity(0.04);
            Random a = new Random();
            p.scale(0.25F).setColor(new Color(a.nextFloat(), a.nextFloat(), a.nextFloat()));
        }

    }

    @Override
    public void spawnRunningParticles() {
        super.spawnRunningParticles();
    }
}

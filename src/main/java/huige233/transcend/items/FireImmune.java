package huige233.transcend.items;

import huige233.transcend.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
@Mod.EventBusSubscriber
public class FireImmune extends EntityItem {
    public FireImmune(World world, Entity location, ItemStack stack) {
        this(world, location.posX, location.posY, location.posZ, stack);
        this.setPickupDelay(10);
        this.motionX = location.motionX;
        this.motionY = location.motionY;
        this.motionZ = location.motionZ;
        this.setItem(stack);
    }
    public FireImmune(World world, double x, double y, double z, ItemStack itemstack) {
        super(world, x, y, z, itemstack);
        this.setItem(itemstack);
    }

    public FireImmune(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.isImmuneToFire = true;
    }

    public FireImmune(World world) {
        super(world);
        isImmuneToFire = true;
    }

    protected void dealFireDamage(int damage) {}

    @Override
    public void onUpdate(){
        //if(!world.isRemote) {
            //tell player item position
//            if (ticksExisted % 20 == 0) {
        //}
    }
/*
    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {

        if(!world.isRemote){
            if(source.isDamageAbsolute()) {
                if (this.getItem().getItem() == ModItems.BEDROCK_CHEN) {
                    int count = this.getItem().getCount();
                    ItemStack stack = new ItemStack(ModItems.BEDROCK_FEN, count);
                    FireImmune item = new FireImmune(world, posX, posY, posZ, stack);
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
            if (event.getEntityItem() instanceof FireImmune) {
                event.setCanceled(true);
            }
        }
    }
}

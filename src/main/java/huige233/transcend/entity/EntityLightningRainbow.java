package huige233.transcend.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;
public class EntityLightningRainbow extends EntityWeatherEffect {
    private int boltLivingTime;

    public long boltVertex;

    private final boolean effectOnly;

    private int lightningState = 2;

    public EntityLightningRainbow(World world){
        super(world);
        this.boltVertex = this.rand.nextLong();
        this.effectOnly = false;
        this.lightningState = 2;
        this.boltVertex = this.rand.nextLong();
        this.boltLivingTime = this.rand.nextInt(3) + 1;
    }

    public EntityLightningRainbow(World world,double x,double y,double z){
        super(world);
        this.boltVertex = this.rand.nextLong();
        this.effectOnly = false;
        this.lightningState = 2;
        this.boltVertex = this.rand.nextLong();
        this.boltLivingTime = this.rand.nextInt(3) + 1;
        this.setLocationAndAngles(x,y,z,0,0);
    }

    public EntityLightningRainbow(World world,double x,double y,double z,boolean effectOnlyIn){
        super(world);
        this.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
        this.lightningState = 2;
        this.effectOnly = effectOnlyIn;
        this.boltVertex = this.rand.nextLong();
        this.boltLivingTime = this.rand.nextInt(3) + 1;
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.WEATHER;
    }


    @Override
    protected void entityInit() {

    }

    public void onUpdate() {
        super.onUpdate();

        if (this.lightningState == 2)
        {
            this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
            this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
        }

        --this.lightningState;

        if (this.lightningState < 0)
        {
            if (this.boltLivingTime == 0)
            {
                this.setDead();
            }
            else if (this.lightningState < -this.rand.nextInt(10))
            {
                --this.boltLivingTime;
                this.lightningState = 1;

                if (!this.effectOnly && !this.world.isRemote)
                {
                    this.boltVertex = this.rand.nextLong();
                    BlockPos blockpos = new BlockPos(this);

                    if (this.world.getGameRules().getBoolean("doFireTick") && this.world.isAreaLoaded(blockpos, 10) && this.world.getBlockState(blockpos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(this.world, blockpos))
                    {
                        this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
                    }
                }
            }
        }

        if (this.lightningState >= 0)
        {
            if (this.world.isRemote)
            {
                this.world.setLastLightningBolt(2);
            }
            else if (!this.effectOnly)
            {
                double d0 = 3.0D;
                List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(this.posX - 3.0D, this.posY - 3.0D, this.posZ - 3.0D, this.posX + 3.0D, this.posY + 6.0D + 3.0D, this.posZ + 3.0D));

                for (int i = 0; i < list.size(); ++i)
                {
                    Entity entity = list.get(i);
                    //entity.onStruckByLightning(new EntityLightningBolt(this.world, this.posX, this.posY, this.posZ, false));
                }
            }
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }
}

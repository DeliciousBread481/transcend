package huige233.transcend.entity.transcender;

import huige233.transcend.entity.IEntityTranscend;
import huige233.transcend.entity.transcender.ai.EntityAITranscenderAttack;
import huige233.transcend.entity.transcender.ai.EntityAITranscenderNearestAttackableEntity;
import huige233.transcend.entity.transcender.ai.EntityAITranscenderNearestAttackablePlayer;
import huige233.transcend.entity.transcender.ai.EntityAITranscenderSwimming;
import huige233.transcend.util.SwordUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;

public class EntityTranscender extends EntityCreature implements IEntityTranscend {
    private boolean dispersal;
    public boolean dimChangeing;

    public EntityTranscender(World world){
        super(world);
        setSize(0.6f,1.5f);
        setPathPriority(PathNodeType.WATER,0);
        moveHelper = new EntityTranscenderMoveHelper(this);
        dispersal = false;
        dimChangeing = false;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1);
        getEntityAttribute(SWIM_SPEED).setBaseValue(15);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAITranscenderAttack(this));
        tasks.addTask(4, new EntityAIWanderAvoidWater(this, 1, 4));
        tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0f));
        tasks.addTask(6, new EntityAITranscenderSwimming(this));
        tasks.addTask(7, new EntityAILookIdle(this));
        targetTasks.addTask(2, new EntityAITranscenderNearestAttackablePlayer(this));
        targetTasks.addTask(2, new EntityAITranscenderNearestAttackableEntity(this));
    }

    @Override
    public boolean attackEntityAsMob(Entity entity){
        if(entity instanceof EntityPlayer){
            SwordUtil.killPlayer((EntityPlayer) entity,this);
        }if(entity instanceof EntityLivingBase){
            SwordUtil.killEntityLiving((EntityLivingBase) entity,this);
        } else {
            SwordUtil.killEntity(entity);
        }
        return true;
    }

    @Override
    public void onEntityUpdate(){
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1);
        getEntityAttribute(SWIM_SPEED).setBaseValue(15);
        super.onEntityUpdate();
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (isServerWorld() && isInWater() && getAttackTarget() != null && getAttackTarget().isInWater()) {
            moveRelative(strafe, vertical, forward, 0.01F);
            move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            motionX *= (double) 0.9F;
            motionY *= (double) 0.9F;
            motionZ *= (double) 0.9F;
        } else {
            super.travel(strafe, vertical, forward);
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
        return false;
    }

    @Override
    protected void handleJumpWater() {
        this.motionY += 0.04;
    }

    @Override
    protected void handleJumpLava() {
        this.motionY += 0.04;
    }

    @Override
    public boolean isChild() {
        return true;
    }

    @Override
    protected void outOfWorld() {
        dismountRidingEntity();
        setLocationAndAngles(posX, 256, posZ, rotationYaw, rotationPitch);
    }

    @Override
    public Entity changeDimension(int dimensionIn, ITeleporter teleporter) {
        dispersal = true;
        return super.changeDimension(dimensionIn, teleporter);
    }

    @Override
    public void onRemovedFromWorld() {
        if (!dispersal && !world.isRemote) {
            EntityTranscender transcender = new EntityTranscender(world);
            transcender.copyLocationAndAnglesFrom(this);
            world.spawnEntity(transcender);
            dispersal = true;
        }
        super.onRemovedFromWorld();
    }
/*
    @Override
    @Nullable
    @Optional.Method(modid = IceAndFire.MODID)
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        T result = super.getCapability(capability, facing);
        if (result instanceof IEntityDataCapability) {
            IEntityData data = ((IEntityDataCapability) result).getData("Ice And Fire - Stone Property Tracker");
            if (data != null && data instanceof StoneEntityProperties) {
                ((StoneEntityProperties) data).isStone = false;
            }
        }
        return result;
    }

 */

    @Override
    public boolean isDispersal() {
        return dispersal;
    }

    @Override
    public void setDispersal(boolean value) {
        dispersal = value;
    }

}

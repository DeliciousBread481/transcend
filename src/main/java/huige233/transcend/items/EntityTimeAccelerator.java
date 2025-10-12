package huige233.transcend.items;

import huige233.transcend.util.BlockUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Elegant version of EntityTimeAccelerator.
 * Creates a temporal field that accelerates ticking for the target TileEntity.
 *
 * Core idea: Every tick, it applies `timeRate` updates to the target.
 * When time expires, it disappears gracefully.
 */
public class EntityTimeAccelerator extends Entity implements IEntityAdditionalSpawnData {

    private static final DataParameter<Integer> TIME_RATE =
            EntityDataManager.createKey(EntityTimeAccelerator.class, DataSerializers.VARINT);

    private static final int RANDOM_TICK_CHANCE = 1365;
    private static final Class<?> COFH_TILE_CLASS = tryLoadClass("cofh.core.block.TileCore");

    private BlockPos target = BlockPos.ORIGIN;
    private int remainingTime = 0;

    public EntityTimeAccelerator(World world) {
        super(world);
        this.setSize(0.1F, 0.1F);
        this.noClip = true;
    }

    public EntityTimeAccelerator(World world, BlockPos target, double x, double y, double z) {
        this(world);
        this.target = target;
        this.setPosition(x, y, z);
    }

    @Override
    protected void entityInit() {
        dataManager.register(TIME_RATE, 1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 128 * 128;
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();

        if (world.isRemote) return;

        TileEntity tile = world.getTileEntity(target);
        if (tile == null) {
            setDead();
            return;
        }

        accelerateTile(tile);
        if (--remainingTime <= 0) setDead();
    }

    private void accelerateTile(TileEntity tile) {
        boolean isCofhTile = COFH_TILE_CLASS != null && COFH_TILE_CLASS.isInstance(tile);

        for (int i = 0; i < getTimeRate(); i++) {
            tickTile(tile, isCofhTile);
            triggerRandomBlockTick();
        }
    }

    private void tickTile(TileEntity tile, boolean isCofhTile) {
        TileEntity current = world.getTileEntity(target);
        if (current instanceof ITickable && (!isCofhTile || !world.isRemote)) {
            ((ITickable) current).update();
        }
    }

    private void triggerRandomBlockTick() {
        if (world.rand.nextInt(RANDOM_TICK_CHANCE) != 0) return;
        IBlockState state = world.getBlockState(target);
        if (state.getBlock().getTickRandomly()) {
            state.getBlock().randomTick(world, target, state, world.rand);
        }
    }

    public int getTimeRate() {
        return dataManager.get(TIME_RATE);
    }

    public void setTimeRate(int rate) {
        dataManager.set(TIME_RATE, Math.max(1, rate));
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int ticks) {
        this.remainingTime = Math.max(0, ticks);
    }

    public BlockPos getTarget() {
        return target;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        this.target = BlockUtils.readBlockPosFromNBT(tag, "Target");
        this.remainingTime = tag.getInteger("RemainingTime");
        setTimeRate(tag.getInteger("TimeRate"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        BlockUtils.writeBlockPosToNBT(tag, "Target", target);
        tag.setInteger("RemainingTime", remainingTime);
        tag.setInteger("TimeRate", getTimeRate());
    }

    @Override
    public void writeSpawnData(ByteBuf buf) {
        buf.writeInt(target.getX());
        buf.writeInt(target.getY());
        buf.writeInt(target.getZ());
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        this.target = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    private static Class<?> tryLoadClass(String className) {
        try {
            return Class.forName(className);
        } catch (Throwable ignored) {
            return null;
        }
    }
}

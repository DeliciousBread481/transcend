package huige233.transcend.tileEntity;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class TileRandomGenerator extends TileEntity implements ITickable {
    private static final int MAX_ENERGY = 30000;
    int energy = 0;
    int energyPerTick = 0;

    Random r = new Random();

    private final IEnergyStorage  energyHandler = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int i, boolean b) {
            return 0;
        }

        @Override
        public int extractEnergy(int i, boolean b) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return energy;
        }

        @Override
        public int getMaxEnergyStored() {
            return MAX_ENERGY;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };

    @Override
    public boolean hasCapability(@Nonnull Capability<?> cap, @Nullable EnumFacing side){
        return cap == CapabilityEnergy.ENERGY || super.hasCapability(cap,side);
    }

    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side){
        if(cap == CapabilityEnergy.ENERGY){
            return CapabilityEnergy.ENERGY.cast(energyHandler);
        } else return super.getCapability(cap, side);
    }

    @Override
    public void update(){
        if(!world.isRemote){
            int transfer = Math.min(energy, 50);
            energy -= transfer;
            energy += transmitEntity(transfer);
            if(energy < MAX_ENERGY && world.getTotalWorldTime() % 5 == 0){
                if(energyPerTick > 0) {
                    energy += r.nextInt(50);
                    energyPerTick--;
                }

                if(world.getBlockState(pos.add(0,1,0)).getBlock() != Blocks.AIR && energyPerTick == 0){
                    world.setBlockToAir(pos.add(0,1,0));
                    energyPerTick += r.nextInt(100);
                }
                if(world.isDaytime())energy += 3;
                if(!world.isDaytime())energy +=2;
            }
        }
    }

    private int transmitEntity(int energy){
        for(EnumFacing e : EnumFacing.VALUES){
            BlockPos neighbor = getPos().offset(e);
            if(!world.isBlockLoaded(neighbor))continue;

            TileEntity te = world.getTileEntity(neighbor);
            if(te == null) continue;

            IEnergyStorage storage = null;

            if(te.hasCapability(CapabilityEnergy.ENERGY,e.getOpposite())){
                storage = te.getCapability(CapabilityEnergy.ENERGY,e.getOpposite());
            }else if(te.hasCapability(CapabilityEnergy.ENERGY,null)){
                storage = te.getCapability(CapabilityEnergy.ENERGY,null);
            }

            if(storage != null) {
                energy -= storage.receiveEnergy(energy,false);

                if(energy <=0) return 0;
            }
        }
        return energy;
    }
}

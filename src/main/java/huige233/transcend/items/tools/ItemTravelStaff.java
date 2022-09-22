package huige233.transcend.items.tools;

import baubles.common.Baubles;
import cofh.redstoneflux.RedstoneFluxProps;
import cofh.redstoneflux.api.IEnergyContainerItem;
import huige233.transcend.Main;
import huige233.transcend.items.ItemBase;
import huige233.transcend.util.IHasModel;
import static huige233.transcend.util.handlers.BaublesHelper.getBaubles;

import huige233.transcend.util.TravelController;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;

@Optional.Interface(modid = RedstoneFluxProps.MOD_ID, iface = "cofh.redstoneflux.api.IEnergyContainerItem")
@Optional.Interface(modid = IC2.MODID, iface = "ic2.api.item.ISpecialElectricItem")
public class ItemTravelStaff extends ItemBase implements IHasModel {
    private long Blick_Tick = 0;

    public ItemTravelStaff(String name, CreativeTabs tab){
        super(name,tab);
        this.maxStackSize=1;
        setHasSubtypes(true);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected){
        if(Loader.isModLoaded(IC2.MODID)){
            ic2charge(stack,world,entity,itemSlot,isSelected);
        }
        if(Loader.isModLoaded(RedstoneFluxProps.MOD_ID)){
            rfReceive(stack,world,entity,itemSlot,isSelected);
        }
    }

    @Optional.Method(modid = RedstoneFluxProps.MOD_ID)
    private void rfReceive(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack receive = player.inventory.getStackInSlot(i);
                if (!receive.isEmpty()) {
                    if (receive.getItem() instanceof IEnergyContainerItem) {
                        IEnergyContainerItem energy = (IEnergyContainerItem) receive.getItem();
                        energy.receiveEnergy(receive, energy.getMaxEnergyStored(receive) - energy.getEnergyStored(receive), false);
                    }
                    if (receive.hasCapability(CapabilityEnergy.ENERGY, null)) {
                        IEnergyStorage cap = (IEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
                        if ((cap != null) && (cap.canReceive())) {
                            cap.receiveEnergy(Integer.MAX_VALUE, false);
                        }
                    }
                }
            }
            if (Loader.isModLoaded(Baubles.MODID)) {
                for (ItemStack receive : getBaubles(player)) {
                    if (receive.getItem() instanceof IEnergyContainerItem) {
                        IEnergyContainerItem energy = (IEnergyContainerItem) receive.getItem();
                        energy.receiveEnergy(receive, energy.getMaxEnergyStored(receive) - energy.getEnergyStored(receive), false);
                    }
                    if (receive.hasCapability(CapabilityEnergy.ENERGY, null)) {
                        IEnergyStorage cap = (IEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
                        if ((cap != null) && (cap.canReceive())) {
                            cap.receiveEnergy(Integer.MAX_VALUE, false);
                        }
                    }
                }
            }
        }
    }

    @Optional.Method(modid = IC2.MODID)
    private void ic2charge(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack toCharge = player.inventory.getStackInSlot(i);
                if (!toCharge.isEmpty()) {
                    ElectricItem.manager.charge(toCharge, ElectricItem.manager.getMaxCharge(toCharge) - ElectricItem.manager.getCharge(toCharge), Integer.MAX_VALUE, true, false);
                }
            }
            if (Loader.isModLoaded(Baubles.MODID)) {
                for (ItemStack toCharge : getBaubles(player)) {
                    ElectricItem.manager.charge(toCharge, ElectricItem.manager.getMaxCharge(toCharge) - ElectricItem.manager.getCharge(toCharge), Integer.MAX_VALUE, true, false);
                }
            }
        }
    }

    @Override
    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand){
        ItemStack equipped = player.getHeldItem(hand);
        if(player.isSneaking()){
            long tickSinceBlink = Main.proxy.getTickCount() - Blick_Tick;
            if(tickSinceBlink<0){
                Blick_Tick=-1;
            }
            if(world.isRemote){
                if(TravelController.instance.doBlink(equipped,hand,player)) {
                    player.swingArm(hand);
                    Blick_Tick = Main.proxy.getTickCount();
                }
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS,equipped);
        }
        if(world.isRemote){
            TravelController.instance.activateTravelAccessable(equipped,hand,world,player);
        }
        player.swingArm(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
    }
}

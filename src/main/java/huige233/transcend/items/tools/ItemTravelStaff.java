package huige233.transcend.items.tools;

import baubles.common.Baubles;
import cofh.redstoneflux.RedstoneFluxProps;
import cofh.redstoneflux.api.IEnergyContainerItem;
import huige233.transcend.Main;
import huige233.transcend.items.ItemBase;
import huige233.transcend.util.IHasModel;
import static huige233.transcend.util.handlers.BaublesHelper.getBaubles;

import huige233.transcend.util.other.TravelController;
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

    public double getMaxCharge(ItemStack itemStack) {
        return Integer.MAX_VALUE;
    }
}

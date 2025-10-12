package huige233.transcend.items.tools;

import huige233.transcend.Transcend;
import huige233.transcend.items.ItemBase;
import huige233.transcend.util.IHasModel;
import huige233.transcend.util.TextUtils;
import huige233.transcend.util.other.TravelController;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemTravelStaff extends ItemBase implements IHasModel {
    private long Blick_Tick = 0;

    public ItemTravelStaff(String name, CreativeTabs tab){
        super(name,tab);
        this.maxStackSize=1;
        setHasSubtypes(true);
    }

    @Override
    public void registerModels() {
        Transcend.proxy.registerItemRenderer(this, 0, "inventory");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag){
        tooltip.add(TextFormatting.GOLD+I18n.translateToLocal("tooltip.travelstaff.desc"));
        tooltip.add(TextFormatting.GOLD+I18n.translateToLocal("tooltip.travelstaff.desc2"));
    }

    @Override
    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand){
        ItemStack equipped = player.getHeldItem(hand);
        if(player.isSneaking()){
            long tickSinceBlink = Transcend.proxy.getTickCount() - Blick_Tick;
            if(tickSinceBlink<0){
                Blick_Tick=-1;
            }
            if(world.isRemote){
                if(TravelController.instance.doBlink(equipped,hand,player)) {
                    player.swingArm(hand);
                    Blick_Tick = Transcend.proxy.getTickCount();
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

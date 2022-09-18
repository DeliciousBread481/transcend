package huige233.transcend.items.tools;


import huige233.transcend.Main;
import huige233.transcend.init.ModItems;
import huige233.transcend.init.TranscendPotions;
import huige233.transcend.util.IHasModel;
import huige233.transcend.util.ItemNBTHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemTimeStop extends ItemSword implements IHasModel {
    public ItemTimeStop(String name, ToolMaterial material) {
        super(material);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(Main.TranscendTab);
        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull EntityPlayer player, Entity entity) {
        if (!player.world.isRemote) {
            if(entity instanceof EntityPlayer) {
                return true;
            } else {
                entity.setNoGravity(!entity.hasNoGravity());
                entity.setEntityInvulnerable(!entity.getIsInvulnerable());
                EntityLiving e = (EntityLiving) entity;
                e.setNoAI(!e.isAIDisabled());
                e.motionX=0;
                e.motionY=0;
                e.motionZ=0;
                return true;
            }
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
        ItemStack stack =  player.getHeldItem(hand);
        if(!world.isRemote) {
            if (player.isSneaking()) {
                player.addPotionEffect(new PotionEffect(TranscendPotions.time_stop,300,44,false,false));
            }
        }
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag){
        tooltip.add(TextFormatting.BLUE+I18n.translateToLocal("tooltip.TimeStop"));
    }
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> stack) {
        if(tab == Main.TranscendTab) {
            ItemStack itemstack = new ItemStack(this);
            ItemNBTHelper.setInt(itemstack, "HideFlags", 3);
            stack.add(itemstack);
        }
    }
}

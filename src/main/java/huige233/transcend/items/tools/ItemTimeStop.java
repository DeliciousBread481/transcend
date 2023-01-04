package huige233.transcend.items.tools;


import huige233.transcend.Main;
import huige233.transcend.init.ModItems;
import huige233.transcend.init.TranscendPotions;
import huige233.transcend.util.IHasModel;
import huige233.transcend.util.ItemNBTHelper;
import huige233.transcend.util.Reference;
import huige233.transcend.util.TextUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber
public class ItemTimeStop extends ItemSword implements IHasModel {
    public ItemTimeStop(String name, ToolMaterial material) {
        super(material);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(Main.TranscendTab);
        addPropertyOverride(new ResourceLocation(Reference.MOD_ID,"faust"),(stack, worldIn, entityIn) -> ItemTimeStop.isFaust(stack)?1f:0f);
        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull EntityPlayer player, Entity entity) {
        if (!player.world.isRemote) {
            if (!(entity instanceof EntityPlayer)) {
                entity.setNoGravity(!entity.hasNoGravity());
                entity.setEntityInvulnerable(!entity.getIsInvulnerable());
                EntityLiving e = (EntityLiving) entity;
                e.setNoAI(!e.isAIDisabled());
                e.motionX = 0;
                e.motionY = 0;
                e.motionZ = 0;
            }
            return true;
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
        ItemStack stack =  player.getHeldItem(hand);
        if(!world.isRemote) {
            if (player.isSneaking()) {
                player.addPotionEffect(new PotionEffect(TranscendPotions.time_stop,300,44,false,false));
                if(isFaust(stack)){
                    player.getServer().getPlayerList().sendMessage(new TextComponentTranslation(TextUtils.makeFaust(I18n.translateToLocal("tooltip.faust"))));
                    //Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("chat.faust"));
                }
            }
        }
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag){
        tooltip.add(TextFormatting.BLUE+I18n.translateToLocal("tooltip.TimeStop"));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onTooltip(ItemTooltipEvent event) {
        if(event.getItemStack().getItem() instanceof ItemTimeStop){
            ItemStack a = event.getItemStack();
            if(isFaust(a)){
                for(int x = 0;x < event.getToolTip().size();++x) {
                    if (((String) event.getToolTip().get(x)).contains(I18n.translateToLocal("tooltip.TimeStop"))) {
                        event.getToolTip().set(x, TextUtils.makeFaust(I18n.translateToLocal("tooltip.faust")));
                    }
                }
            }
        }
    }

    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> stack) {
        if(tab == Main.TranscendTab) {
            ItemStack itemstack = new ItemStack(this);
            ItemNBTHelper.setInt(itemstack, "HideFlags", 3);
            stack.add(itemstack);
        }
    }

    public static boolean isFaust(ItemStack stack){
        String name = stack.getDisplayName().toLowerCase(Locale.ROOT).trim();
        return name.equals("faust");
    }
}

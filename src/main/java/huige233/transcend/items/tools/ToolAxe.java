package huige233.transcend.items.tools;

import WayofTime.bloodmagic.BloodMagic;
import huige233.transcend.Main;
import huige233.transcend.init.ModItems;
import huige233.transcend.items.fireimmune;
import huige233.transcend.util.IHasModel;
import huige233.transcend.util.ItemNBTHelper;
import huige233.transcend.util.SoulNetWorkUtil;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import java.util.Random;

public class ToolAxe extends ItemAxe implements IHasModel {
    public ToolAxe(String name, CreativeTabs tab, ToolMaterial material) {
        super(material,999.0f,-0.0f);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(tab);
        ModItems.ITEMS.add(this);
    }
    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    public Entity createEntity(World world,Entity location, ItemStack itemstack) {
        return new fireimmune(world,location,itemstack);
    }

    public void onUpdate(ItemStack tool,World world,Entity entity,int itemSlot,boolean isSelected){
        boolean light = ItemNBTHelper.getBoolean(tool,"AutoLight",false);
        if(!world.isRemote){
            if(light){
                BlockPos pos = entity.getPosition();
                if(world.getLightFromNeighbors(pos) < 8) {
                    BlockPos[] blockPos = new BlockPos[]{pos};
                    for (BlockPos candi : blockPos) {
                        Random random = new Random();
                        EnumFacing facing = EnumFacing.values()[random.nextInt(6)];
                        PropertyDirection FACING = PropertyDirection.create("facing");
                        world.setBlockState(candi, Blocks.GLOWSTONE.getBlockState().getBaseState());
                    }
                }
            }
            if(Loader.isModLoaded(BloodMagic.MODID)){
                EntityPlayer player = (EntityPlayer) entity;
                SoulNetWorkUtil.NetWorkAdd(player);
            }
        }
    }

    public int getCrystalLevel(ItemStack stack) {
        return stack.getItemDamage() > 1 ? Integer.MAX_VALUE : stack.getItemDamage() + 1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isSneaking()) {
            if (player.getHeldItem(hand).getItem() == ModItems.TRANSCEND_PICKAXE) {
                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack) != 10) {
                    stack.addEnchantment(Enchantments.FORTUNE, 10);
                    player.swingArm(hand);
                }
            }
        } else if(player.isSneaking()){
            boolean light = ItemNBTHelper.getBoolean(stack,"AutoLight",false);
            if(!light){
                ItemNBTHelper.setBoolean(stack,"AutoLight",true);
                stack.setStackDisplayName(TextFormatting.RED + I18n.translateToLocal("transcend_axe_light.name"));
                player.swingArm(hand);
            } else {
                ItemNBTHelper.setBoolean(stack, "AutoLight", false);
                stack.setStackDisplayName(TextFormatting.RED + I18n.translateToLocal("item.transcend_axe.name"));
                player.swingArm(hand);
            }
        }
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }

    public EnumRarity getRarity(ItemStack stack )
    {
        return(ModItems.COSMIC_RARITY);
    }
}

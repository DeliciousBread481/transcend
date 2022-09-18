package huige233.transcend.items.tools;

import huige233.transcend.Main;
import huige233.transcend.init.ModItems;
import huige233.transcend.items.fireimmune;
import huige233.transcend.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        if(!world.isRemote){
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
    }

    public EnumRarity getRarity(ItemStack stack )
    {
        return(ModItems.COSMIC_RARITY);
    }
}

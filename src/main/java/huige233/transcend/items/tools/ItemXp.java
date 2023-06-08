package huige233.transcend.items.tools;

import huige233.transcend.Transcend;
import huige233.transcend.init.ModItems;
import huige233.transcend.items.EntityFireImmune;
import huige233.transcend.items.ItemBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemXp extends ItemBase {
    public ItemXp(String name, CreativeTabs tab){
        super(name,tab);
    }

    @Override
    public void registerModels(){
        Transcend.proxy.registerItemRenderer(this,0,"inventory");
    }

    public boolean hasCustomEntity(ItemStack stack){
        return true;
    }

    public Entity createEntity(World world, Entity location,ItemStack stack){
        return new EntityFireImmune(world,location.posX,location.posY,location.posZ,stack);
    }

    public EnumRarity  getRarity(ItemStack stack){
        return (ModItems.COSMIC_RARITY);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
        ItemStack stack =  player.getHeldItem(hand);
        if(!world.isRemote) {
            if (player.isSneaking()) {
                if(!player.capabilities.isCreativeMode) {
                    player.experienceLevel = 21863;
                }
            }
        }
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }

}

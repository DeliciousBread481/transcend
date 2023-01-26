package huige233.transcend.items;

import huige233.transcend.Main;
import huige233.transcend.init.ModItems;
import huige233.transcend.util.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemBase extends Item implements IHasModel {
    private boolean isRangedWeapon = false;
    public ItemBase(String name, CreativeTabs tab) {
        setTranslationKey(name).setRegistryName(name).setCreativeTab(tab);
        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    public Entity createEntity(@NotNull World world, @NotNull Entity location, @NotNull ItemStack itemstack) {
        return new FireImmune(world,location,itemstack);
    }

    public EnumRarity getRarity(ItemStack stack )
    {
        return(ModItems.COSMIC_RARITY);
    }

    public ItemBase setRangedWeapon()
    {
        isRangedWeapon = true;
        return this;
    }
}

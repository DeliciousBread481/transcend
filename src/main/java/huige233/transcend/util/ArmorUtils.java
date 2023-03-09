package huige233.transcend.util;

import huige233.transcend.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ArmorUtils {
    public static boolean fullEquipped(EntityPlayer player) {
        if (player == null || player.inventory == null)return false;
        NonNullList<ItemStack> armor = player.inventory.armorInventory;
        if (armor == null) return false;
        return armor.get(3).getItem() == ModItems.FLAWLESS_HELMET && armor.get(2).getItem() == ModItems.FLAWLESS_CHESTPLATE && armor.get(1).getItem() == ModItems.FLAWLESS_LEGGINGS && armor.get(0).getItem() == ModItems.FLAWLESS_BOOTS;
    }
}
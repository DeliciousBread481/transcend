package huige233.transcend.items.compat;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import huige233.transcend.Main;
import huige233.transcend.items.ItemBase;
import huige233.transcend.util.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class LootUnder extends ItemBase implements IBauble, IHasModel {

    public LootUnder(String name, CreativeTabs tab) {
        super(name, Main.TranscendTab);
        this.maxStackSize=1;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.TRINKET;
    }
}

package huige233.transcend.items.tools;

import huige233.transcend.Transcend;
import huige233.transcend.init.ModItems;
import huige233.transcend.util.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemShears;

public class ToolShears extends ItemShears implements IHasModel {
    public ToolShears(String name, CreativeTabs tab){
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(tab);
        setMaxDamage(-1);
        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Transcend.proxy.registerItemRenderer(this, 0, "inventory");
    }
}

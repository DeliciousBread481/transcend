package huige233.transcend.compat.slash;

import huige233.transcend.compat.slash.named.init.Transcend;
import huige233.transcend.compat.slash.named.TranscendSlashBlade;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BladeLoader {
    public static Item tran;

    public BladeLoader(){
        tran = (new TranscendSlashBlade(Item.ToolMaterial.DIAMOND, 32767.0f)).setMaxDamage(-1).setCreativeTab((CreativeTabs) SlashBlade.tab);
        SlashBlade.InitEventBus.register(new Transcend());
    }
}

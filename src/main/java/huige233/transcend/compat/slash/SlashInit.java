package huige233.transcend.compat.slash;

import huige233.transcend.Main;
import huige233.transcend.compat.slash.specialattack.Delete;
import huige233.transcend.compat.slash.specialeffects.SEDataLock;
import huige233.transcend.compat.slash.specialeffects.SETranscend;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraftforge.common.MinecraftForge;

public class SlashInit {

    public static void init(){
        ItemSlashBlade.specialAttacks.put(Integer.valueOf(678), new Delete());
        MinecraftForge.EVENT_BUS.register(new SlashUpdateEvent());
        SpecialEffects.register((ISpecialEffect) new SETranscend());
        SpecialEffects.register((ISpecialEffect) new SEDataLock());
        SlashBlade.InitEventBus.register(Main.proxy);
    }
}

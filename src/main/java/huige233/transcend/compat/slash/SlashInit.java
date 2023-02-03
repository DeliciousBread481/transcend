package huige233.transcend.compat.slash;

import huige233.transcend.Main;
import huige233.transcend.proxy.CommonProxy;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SlashInit {

    public static void init(){
        ItemSlashBlade.specialAttacks.put(Integer.valueOf(678), new Delete());
        MinecraftForge.EVENT_BUS.register(new SlashUpdateEvent());
        MinecraftForge.EVENT_BUS.register(new LoaderSlash());
        SlashBlade.InitEventBus.register(Main.proxy);
        SpecialEffects.register((ISpecialEffect) new SETranscend());
    }
}

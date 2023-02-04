package huige233.transcend.proxy;

import huige233.transcend.Main;
import huige233.transcend.compat.Avartiabreak;
import huige233.transcend.compat.PsiCompat;
import huige233.transcend.compat.slash.*;
import huige233.transcend.compat.slash.specialattack.EntityDelete;
import huige233.transcend.compat.slash.BladeLoader;
import huige233.transcend.compat.tinkers.TiCConfig;
import huige233.transcend.compat.tinkers.conarmConfig;
import huige233.transcend.entity.BoltRegistry;
import huige233.transcend.entity.EntityLightningRainbow;
import huige233.transcend.packet.PacketEndTimeStop;
import huige233.transcend.util.ItemBladeUtils;
import huige233.transcend.util.handlers.ModEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Optional.Interface(iface = "mods.flammpfeil.slashblade.named.event.LoadEvent", modid = "flammpfeil.slashblade")
public class CommonProxy {

    protected long serverTickCount = 0;
    protected long clientTickCount = 0;
    boolean a = true;

    public void registerItemRenderer( Item item, int meta, String id )
    {
    }

    public void handleEndTimeStopPacket(PacketEndTimeStop.Message message){}

    public void loadShader(EntityPlayer player, ResourceLocation shader){}

    public void playBlinkEffect(EntityPlayer player){}

    public String translate(String key, Object... args){
        return translate(key, new Style(), args);
    }

    public String translate(String key, Style style, Object... args){
        return key;
    }

    public long getTickCount() {
        return serverTickCount;
    }

    public void preInit( FMLPreInitializationEvent event )
    {
        if(Loader.isModLoaded("tconstruct")){
            TiCConfig.setup();
        }
        if(Loader.isModLoaded("psi")){
            MinecraftForge.EVENT_BUS.register(PsiCompat.class);
        }
        if(Loader.isModLoaded("avaritia")) {
            Avartiabreak.enabled = true;
        }
        MinecraftForge.EVENT_BUS.register(new ModEventHandler());
        if(Loader.isModLoaded("flammpfeil.slashblade")) {
            SlashInit.init();
            new BladeLoader();
            new ItemBladeUtils();
        }
        BoltRegistry.registerBolt();
    }

    public void init( FMLInitializationEvent event )
    {
        if(Loader.isModLoaded("flammpfeil.slashblade")) EntityRegistry.registerModEntity(new ResourceLocation("transcend", "Delete"), EntityDelete.class, "Delete", 30, Main.instance, 250, 200, true);
        if(Loader.isModLoaded("conarm")) conarmConfig.setup();
        EntityRegistry.registerModEntity(new ResourceLocation("transcend", "LightningRainbow"), EntityLightningRainbow.class, "LightningRainbow", 31, Main.instance, 250, 200, true);
    }


    public void postInit(FMLPostInitializationEvent event) {
    }
}

package huige233.transcend.proxy;

import huige233.transcend.Main;
import huige233.transcend.compat.Avartiabreak;
import huige233.transcend.compat.PsiCompat;
import huige233.transcend.compat.slash.*;
import huige233.transcend.compat.tinkers.TiCConfig;
import huige233.transcend.compat.tinkers.conarmConfig;
import huige233.transcend.entity.BoltRegistry;
import huige233.transcend.entity.EntityLightningRainbow;
import huige233.transcend.packet.PacketEndTimeStop;
import huige233.transcend.util.handlers.ModEventHandler;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
        }
        BoltRegistry.registerBolt();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void load(LoadEvent.InitEvent event) {
        if(Loader.isModLoaded("flammpfeil.slashblade") && a) {
            ItemStack blade = new ItemStack((Item)SlashBlade.bladeNamed, 1, 0);
            String name = "flammpfeil.slashblade.named.tran";
            ItemStack tran = new ItemStack(LoaderSlash.tran, 1, 0);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("HideFlags",6);
            ItemSlashBladeNamed.CurrentItemName.set(tag, name);
            ItemSlashBladeNamed.CustomMaxDamage.set(tag, Integer.valueOf(32767));
            ItemSlashBladeNamed.IsDefaultBewitched.set(tag, Boolean.valueOf(true));
            ItemSlashBladeNamed.NamedBlades.add("flammpfeil.slashblade.named.tran");
            ItemSlashBlade.setBaseAttackModifier(tag, 32767.0F);
            ItemSlashBlade.SpecialAttackType.set(tag, Integer.valueOf(678));
            ItemSlashBlade.TextureName.set(tag, "named/transcend/texture");
            ItemSlashBlade.ModelName.set(tag, "named/transcend/model");
            ItemSlashBlade.RepairCount.set(tag, Integer.valueOf(100000));
            ItemSlashBlade.KillCount.set(tag, Integer.valueOf(1000000));
            ItemSlashBlade.ProudSoul.set(tag, Integer.valueOf(1000000));
            SpecialEffects.addEffect(blade, "SETranscend",1000);
            tag.setBoolean("Unbreakable", true);
            tran.setTagCompound(tag);
            tran.addEnchantment(Enchantments.INFINITY, 127);
            tran.addEnchantment(Enchantments.POWER, 127);
            tran.addEnchantment(Enchantments.PUNCH, 127);
            SlashBlade.registerCustomItemStack(name, tran);
            a = false;
        }
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

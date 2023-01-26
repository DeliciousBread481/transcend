package huige233.transcend.proxy;

import huige233.transcend.Main;
import huige233.transcend.compat.Avartiabreak;
import huige233.transcend.compat.PsiCompat;
import huige233.transcend.compat.slash.Delete;
import huige233.transcend.compat.slash.EntityDelete;
import huige233.transcend.compat.slash.LoaderSlash;
import huige233.transcend.compat.slash.SlashUpdateEvent;
import huige233.transcend.compat.tinkers.TiCConfig;
import huige233.transcend.compat.tinkers.conarmConfig;
import huige233.transcend.packet.PacketEndTimeStop;
import huige233.transcend.util.handlers.ModEventHandler;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.RecipeAwakeBlade;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class CommonProxy {

    protected long serverTickCount = 0;
    protected long clientTickCount = 0;
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
        ItemSlashBlade.specialAttacks.put(Integer.valueOf(678), new Delete());
        MinecraftForge.EVENT_BUS.register(new SlashUpdateEvent());
        MinecraftForge.EVENT_BUS.register(new LoaderSlash());
        SlashBlade.InitEventBus.register(this);
    }

    public void init( FMLInitializationEvent event )
    {
        EntityRegistry.registerModEntity(new ResourceLocation("transcend", "Delete"), EntityDelete.class, "Delete", 30, Main.instance, 250, 200, true);
        if(Loader.isModLoaded("conarm")){
            conarmConfig.setup();
        }
    }


    public void postInit(FMLPostInitializationEvent event) {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void init(LoadEvent.InitEvent event) {
        String name = "flammpfeil.slashblade.named.tran";
        ItemStack tran = new ItemStack(LoaderSlash.tran, 1, 0);
        NBTTagCompound tag = new NBTTagCompound();
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
        tag.setBoolean("Unbreakable", true);
        tran.setTagCompound(tag);
        tran.addEnchantment(Enchantments.INFINITY, 100);

        SlashBlade.registerCustomItemStack(name, tran);
        ItemStack blade1 = SlashBlade.getCustomBlade(name);
        ItemStack custombladeReqired1 = SlashBlade.findItemStack("flammpfeil.slashblade", "flammpfeil.slashblade.named.yamato", 1);
        ItemStack egg1 = new ItemStack(Blocks.DRAGON_EGG);
        ItemStack netherstar = new ItemStack(Items.NETHER_STAR);
        ItemStack yamato = SlashBlade.getCustomBlade("flammpfeil.slashblade", "flammpfeil.slashblade.named.yamato");
        ItemStack blade = SlashBlade.getCustomBlade("flammpfeil.slashblade", "slashblade");
        SlashBlade.addRecipe(name, (IRecipe)new RecipeAwakeBlade(new ResourceLocation("flammpfeil.slashblade", "yiciyuan"),
                SlashBlade.getCustomBlade("flammpfeil.slashblade", name), ItemStack.EMPTY, new Object[] {
                "XTX", "VBV", "XVX",

                Character.valueOf('X'), blade,
                Character.valueOf('T'), egg1,
                Character.valueOf('V'), netherstar,
                Character.valueOf('B'),
                yamato }));
    }
}

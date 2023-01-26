package huige233.transcend.compat.slash;

import huige233.transcend.compat.TranscendSlash;

import mods.flammpfeil.slashblade.tileentity.DummyTileEntity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LoaderSlash {
    public static final Item tran = (Item) new TranscendSlash();

    @SubscribeEvent
    public void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(tran.setRegistryName("transcend", "tran"));
    }

    @SubscribeEvent
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(tran, 0, new ModelResourceLocation("flammpfeil.slashblade:model/named/blade.obj"));
        ForgeHooksClient.registerTESRItemStack(tran, 0, DummyTileEntity.class);
    }
}

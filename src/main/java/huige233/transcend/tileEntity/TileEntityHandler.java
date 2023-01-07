package huige233.transcend.tileEntity;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityHandler {
    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityVirusGenerator.class,new ResourceLocation("transcend:tileEntityVirusGenerator"));
        GameRegistry.registerTileEntity(TileEntityCollerctor.class,new ResourceLocation("transcend:tileEntityCollector"));
        GameRegistry.registerTileEntity(TileCreativeRFSource.class,new ResourceLocation("transcend:TileCreativeRFSource"));
        GameRegistry.registerTileEntity(TileUltraManaPool.class,new ResourceLocation("transcend:TileUltraManaPool"));
    }
}

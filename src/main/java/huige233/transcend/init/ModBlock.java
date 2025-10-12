package huige233.transcend.init;

import huige233.transcend.blocks.BlockBase;
import huige233.transcend.blocks.bedrockorz;
import huige233.transcend.blocks.voidblock;
import huige233.transcend.tileEntity.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
@Mod.EventBusSubscriber
public class ModBlock {
    public static final List<Block> BLOCKS = new ArrayList<Block>();
    public static final Block TRANSCEND_BLOCK = new BlockBase("transcend_block", Material.ROCK);
    public static final Block FLAWLESS_BLOCK = new BlockBase("flawless_block", Material.ROCK);
    public static final Block BEDROCK_ORE = new bedrockorz("bedrock_ore", Material.ROCK);
    public static final Block VOIDBLOCK = new voidblock("void_block", Material.ROCK);
    public static final Block BEDROCK_COLLECTOR = new BlockBedRockCollector("bedrock_collector").setHardness(5.0f);
    public static final Block CAST_MACHINE = new BlockVirusGenerator("cast_machine");
    public static final Block NETHER_STAR_BLOCK = new BlockBase("nether_star_block", Material.ROCK).setHardness(5.0F);
    public static final Block CREATIVERFSOURCE = new BlockCreativeRFSource("creative_rf_source");
    public static final Block RANDOM_GENERATOR = new BlockRandomGenerator("random_generator");
    public static Block CREATIVEEUSOURCE = null;

    public static Block ULTRAMANAPOOL = null;

    static {

        if(Loader.isModLoaded("ic2")) {
            //CREATIVEEUSOURCE = new BlockCreativeEUSource("creative_eu_source_");
        }
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        if(Loader.isModLoaded("botania")){
            ULTRAMANAPOOL = new BlockUltraManaPool();
            event.getRegistry().registerAll(ULTRAMANAPOOL);
        }
//        if(Loader.isModLoaded("ic2")){
//            CREATIVEEUSOURCE = new BlockCreativeEUSource("creative_eu_source_");
//            event.getRegistry().registerAll(CREATIVEEUSOURCE);
//        }
    }
}

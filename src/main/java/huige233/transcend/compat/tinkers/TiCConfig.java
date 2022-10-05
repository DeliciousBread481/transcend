package huige233.transcend.compat.tinkers;

import com.google.common.collect.Lists;
import huige233.transcend.init.ModBlock;
import huige233.transcend.init.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.MaterialRenderInfo;
import slimeknights.tconstruct.library.fluid.FluidColored;
import slimeknights.tconstruct.library.materials.*;
import slimeknights.tconstruct.library.smeltery.Cast;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerTraits;

import java.util.List;

import static slimeknights.tconstruct.library.materials.MaterialTypes.HEAD;

public class TiCConfig {
    public static final AbstractTrait flawlesstrait = new TraitFlawless();
    public static final AbstractTrait transcendtratt =  new TraitTranscend();
    public static final List<Material> materials = Lists.newArrayList();
    public static Material flawless = mat("flawless", 0x777777);
    public static final Material transcend = mat("transcend",0xF8F8FF);
    private static Material mat(String name, int color) {
        if (TinkerRegistry.getMaterial(name) == TinkerRegistry.getMaterial("unknown")){
            Material mat = new Material(name, color);
            materials.add(mat);
            return mat;
        }
        return TinkerRegistry.getMaterial(name);
    }

    @SubscribeEvent
    public static void setup() {
        HarvestLevels.harvestLevelNames.put(32, TextFormatting.GRAY + "Flawless");
        flawless.addItem(ModItems.FLAWLESS);
        flawless.setRepresentativeItem(ModItems.FLAWLESS);
        flawless.setCastable(true);
        flawless.setCraftable(true);
        flawless.addTrait(flawlesstrait);
        flawless.addTrait(TinkerTraits.tasty);
        flawless.addTrait(TinkerTraits.autosmelt);
        TinkerRegistry.addMaterialStats(flawless,
                new HeadMaterialStats(9999, 100.0f, 2000.0f, 32),
                new HandleMaterialStats(10.0f, 9999),
                new ExtraMaterialStats(9999));
        new BowMaterialStats(15.0F, 15.0F, 10F);


        HarvestLevels.harvestLevelNames.put(99, TextFormatting.GRAY + "Transcend");
        transcend.addItem(ModItems.TRANSCEND);
        transcend.setRepresentativeItem(ModItems.TRANSCEND);
        transcend.setCastable(true);
        transcend.setCraftable(true);
        transcend.addTrait(transcendtratt);
        TinkerRegistry.addMaterialStats(transcend,
                new HeadMaterialStats(9999, 100.0f, 9999.0f, 99),
                new HandleMaterialStats(100.0f, 9999),
                new ExtraMaterialStats(9999));
        new BowMaterialStats(45.0F, 45.0F, 50F);

        TinkerRegistry.integrate(flawless).preInit();
        TinkerRegistry.integrate(transcend).preInit();

        registerToolParts(flawless);
        registerToolParts(transcend);
    }

    private static void registerToolParts(Material material)
    {
        Fluid fluid = material.getFluid();

        for(IToolPart toolPart : TinkerRegistry.getToolParts())
        {
            if(!toolPart.canBeCasted())
                continue;

            if(!toolPart.canUseMaterial(material))
                continue;

            if(toolPart instanceof MaterialItem)
            {
                ItemStack stack = toolPart.getItemstackWithMaterial(material);
                ItemStack originCast = Cast.setTagForPart(new ItemStack(TinkerSmeltery.cast), stack.getItem());
                if(fluid != null)
                {
                    TinkerRegistry.registerMelting(stack, fluid, toolPart.getCost());
                    TinkerRegistry.registerTableCasting(stack, originCast, fluid, toolPart.getCost());
                }
            }
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (event.getSide().isClient()) {
            registerMaterialRendering();
        }}

    @SideOnly(Side.CLIENT)
    static void registerMaterialRendering()
    {
        flawless.setRenderInfo(new MaterialRenderInfo.Metal(0x777777, 0.5f, 0.5f, 0.2f));
        transcend.setRenderInfo(new MaterialRenderInfo.Metal(0xF8F8FF, 0.5f, 0.5f, 0.2f));
    }
}


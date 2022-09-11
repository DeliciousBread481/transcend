package huige233.transcend.compat.tinkers;

import c4.conarm.common.armor.traits.ArmorTraits;
import c4.conarm.lib.materials.ArmorMaterials;
import c4.conarm.lib.materials.CoreMaterialStats;
import c4.conarm.lib.materials.PlatesMaterialStats;
import c4.conarm.lib.materials.TrimMaterialStats;
import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class conarmConfig {
    public static final List<Material> materials = Lists.newArrayList();
    public static Material flawless = mat("flawless_armor", 0x777778);
    public static final Material transcend = mat("transcend_armor", 0xF8F8FE);

    public static final Map<String, Material> material = new LinkedHashMap<>();
    private static Material mat(String name, int color) {
        if (TinkerRegistry.getMaterial(name) == TinkerRegistry.getMaterial("unknown")) {
            Material mat = new Material(name, color);
            materials.add(mat);
            return mat;
        }
        return TinkerRegistry.getMaterial(name);
    }

    @SubscribeEvent
    public static void setup() {
            ArmorMaterials.addArmorTrait(flawless,ArmorTraits.tasty);
            ArmorMaterials.addArmorTrait(flawless,TraitArmorFlawless.a);
            TinkerRegistry.addMaterialStats(flawless,
                    new CoreMaterialStats(63,63),
                    new PlatesMaterialStats(4,36,9.9f),
                    new TrimMaterialStats(63.0f));

            ArmorMaterials.addArmorTrait(transcend,TraitArmorTranscend.a);
            TinkerRegistry.addMaterialStats(transcend,
                    new CoreMaterialStats(999999,999999),
                    new PlatesMaterialStats(999999,999999,9999.99f),
                    new TrimMaterialStats(9999.99f));

            TinkerRegistry.integrate(flawless).preInit();
            TinkerRegistry.integrate(transcend).preInit();
    }
}
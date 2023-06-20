package huige233.transcend.init;

import huige233.transcend.Transcend;
import huige233.transcend.items.InfinityFood;
import huige233.transcend.items.ItemBase;
import huige233.transcend.items.armor.ArmorBase;
import huige233.transcend.items.compat.AnvilCompat;
import huige233.transcend.items.compat.FragmentLan;
import huige233.transcend.items.compat.LootUnder;
import huige233.transcend.items.compat.ThunderSkin;
import huige233.transcend.items.tools.*;
import huige233.transcend.util.Reference;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;

import java.util.ArrayList;
import java.util.List;

public class ModItems {

    public static EnumRarity COSMIC_RARITY = EnumHelper.addRarity("COSMIC", TextFormatting.GOLD, "Cosmic");
    public static final List<Item> ITEMS = new ArrayList<Item>();
    public static final Item TRANSCEND = new ItemBase("transcend", Transcend.TranscendTab);
    public static final Item FLAWLESS = new ItemBase("flawless", Transcend.TranscendTab);
    public static final ArmorMaterial flawless_armor = EnumHelper.addArmorMaterial("flawless_alloy","",0,new int[] {1000,1000,1000,1000},1000, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,1000.0f);
    public static final ArmorMaterial ARMOR_MATERIAL_FLAWLESS = EnumHelper.addArmorMaterial("FLAWLESS", Reference.MOD_ID+":flawless", 0, new int[]{1000 , 1000, 1000, 1000}, 100, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,1000.0f);
    public static final Item FLAWLESS_HELMET = new ArmorBase("flawless_helmet", ARMOR_MATERIAL_FLAWLESS, 1, EntityEquipmentSlot.HEAD, Transcend.TranscendTab);
    public static final Item FLAWLESS_CHESTPLATE = new ArmorBase("flawless_chestplate", ARMOR_MATERIAL_FLAWLESS, 1, EntityEquipmentSlot.CHEST, Transcend.TranscendTab);
    public static final Item FLAWLESS_LEGGINGS = new ArmorBase("flawless_leggings", ARMOR_MATERIAL_FLAWLESS, 2, EntityEquipmentSlot.LEGS, Transcend.TranscendTab);
    public static final Item FLAWLESS_BOOTS = new ArmorBase("flawless_boots", ARMOR_MATERIAL_FLAWLESS, 2, EntityEquipmentSlot.FEET, Transcend.TranscendTab);
    public static final ToolMaterial transcend_tool = EnumHelper.addToolMaterial("TRANSCEND",99,-1,9999,50.0f,100000);
    public static final ItemSword TRANSCEND_SWORD = new ToolSword("transcend_sword",Transcend.TranscendTab,transcend_tool);
    public static final ItemAxe TRANSCEND_AXE = new ToolAxe("transcend_axe",Transcend.TranscendTab,transcend_tool);
    public static final ItemPickaxe TRANSCEND_PICKAXE = new ToolPickaxe("transcend_pickaxe",Transcend.TranscendTab,transcend_tool);

    public static final ItemSpade TRANSCEND_SHOVEL = new ToolShovel("transcend_shovel",Transcend.TranscendTab,transcend_tool);
    public static final ItemHoe TRANSCEND_HOE = new ToolHoe("transcend_hoe",Transcend.TranscendTab,transcend_tool);
    public static final Item BEDROCK_LI = new ItemBase("bedrock_li", Transcend.TranscendTab);
    public static final Item BEDROCK_CHEN = new ItemBase("bedrock_chen", Transcend.TranscendTab);
    public static final Item BEDROCK_FEN = new ItemBase("bedrock_fen", Transcend.TranscendTab);
    public static final Item BEDROCK_INGOT = new ItemBase("bedrock_ingot", Transcend.TranscendTab);
    public static final Item BLACK_HOLD_MATERIAL = new ItemBase("black_hold_material", Transcend.TranscendTab);
    public static final ToolMaterial bedrockbreak = EnumHelper.addToolMaterial("BEDROCKBREAK",5,1,1,0.0f,0);
    public static final Item BREAK_BEDROCK_TOOL = new ToolPickaxe("break_bedrock_tool",Transcend.TranscendTab,bedrockbreak);
    public static final ToolMaterial warpsword = EnumHelper.addToolMaterial("warpsword",0,200,1,3,1);
    public static final ItemSword WARP_SWORD = new ToolWarp("warp_sword",Transcend.TranscendTab,warpsword);
    public static final ToolMaterial Invulnera = EnumHelper.addToolMaterial("Invulnera",0,0,1,-4,0);
    public static final ItemSword Invulnerable = new ItemInvulnerable("invulnerable",Invulnera);
    public static final ItemSword TimeStop = new ItemTimeStop("timestop",Invulnera);

    //public static final Item ITEM_XP = new ItemXp("transcend_xp",Transcend.TranscendTab);
    public static final Item TravelStaff = new ItemTravelStaff("travel_staff",Transcend.TranscendTab);
    public static final ItemFood InfinityFood = new InfinityFood();
    //public static final Item OP_Cuff = new ItemOPCuffs("op_cuff",Invulnera);
    public static final Item AnvilCompat = new AnvilCompat();
    public static final Item FragmentLan = new FragmentLan();
    public static final Item ThunderSkin = new ThunderSkin();

    //public static final ItemSword a = new TranscendSword("tr",Transcend.TranscendTab,transcend_tool);

    public static final Item CHUI = new ItemBase("chui",Transcend.TranscendTab).setMaxDamage(3);
    public static final Item CK = new ItemBase("chukuang",Transcend.TranscendTab);
    public static final Item LN = new LootUnder("lootunder",Transcend.TranscendTab);
    public static final ItemBow TBow = new ItemTBow("transcend_bow",Transcend.TranscendTab);
    public static final ItemShears TShears = new ToolShears("transcend_shears",Transcend.TranscendTab);
}

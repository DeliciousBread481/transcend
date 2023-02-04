package huige233.transcend.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

public class BladeUtils {
    public static Map<ResourceLocationRaw, ItemStack> TrBladeRegistry = Maps.newHashMap();
    public static List<String> TrNamedBlades = Lists.newArrayList();

    public static void registerCustomItemStack(String name, ItemStack stack){
        TrBladeRegistry.put(new ResourceLocationRaw(Reference.MOD_ID, name),stack);
    }

    static public ItemStack findItemStack(String modid, String name, int count){
        ResourceLocationRaw key = new ResourceLocationRaw(modid, name);
        ItemStack stack = ItemStack.EMPTY;

        if(TrBladeRegistry.containsKey(key)) {
            stack = TrBladeRegistry.get(key).copy();

        }else if(SlashBlade.BladeRegistry.containsKey(key)){
            stack = ((ItemStack)SlashBlade.BladeRegistry.get(key)).copy();
        }else{
            Item item = Item.REGISTRY.getObject(key);
            if (item != null){
                stack = new ItemStack(item);
            }
        }

        if(!stack.isEmpty()) {
            stack.setCount(count);
        }

        return stack;
    }

    public static ItemStack getCustomBlade(String key){
        String modid, name, str[] = key.split(":", 2);
        if(str.length == 2){
            modid = str[0];
            name = str[1];
        }else{
            modid = Reference.MOD_ID;
            name = key;
        }
        return findItemStack(modid,name,1);
    }
}

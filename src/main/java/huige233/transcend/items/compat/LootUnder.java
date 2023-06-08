package huige233.transcend.items.compat;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import huige233.transcend.Transcend;
import huige233.transcend.items.ItemBase;
import huige233.transcend.util.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class LootUnder extends ItemBase implements IBauble, IHasModel {

    public LootUnder(String name, CreativeTabs tab) {
        super(name, Transcend.TranscendTab);
        this.maxStackSize=1;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.TRINKET;
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if(!player.world.isRemote) {
            Multimap<String, AttributeModifier> attributes = HashMultimap.create();
            fillModifiers(attributes, stack);
            player.getAttributeMap().applyAttributeModifiers(attributes);
        }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        if(!player.world.isRemote) {
            Multimap<String, AttributeModifier> attributes = HashMultimap.create();
            fillModifiers(attributes, stack);
            player.getAttributeMap().removeAttributeModifiers(attributes);
        }
    }

    private void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
        if(stack.isEmpty()) return;
        UUID uuid = new UUID((stack.toString()).hashCode(), 0);
        attributes.put(SharedMonsterAttributes.LUCK.getName(), new AttributeModifier(uuid, "Loot Under", -2, 0).setSaved(false));
    }
}

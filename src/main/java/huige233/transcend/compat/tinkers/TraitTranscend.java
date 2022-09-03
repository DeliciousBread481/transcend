package huige233.transcend.compat.tinkers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitTranscend extends AbstractTrait {
    public TraitTranscend() {
        super("transcend", TextFormatting.DARK_GRAY);
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        NBTTagCompound toolTag = TagUtil.getToolTag(rootCompound);
        toolTag.setInteger("FreeModifiers", 100);
        rootCompound.setBoolean("Unbreakable", true);
        TagUtil.setToolTag(rootCompound, toolTag);
    }

    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if(player instanceof EntityPlayer) {

        }
    }
}

package huige233.transcend.lib;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TranscendDamageSources extends EntityDamageSource{
        public TranscendDamageSources(Entity source) {
            super("transcend",source);
            this.setDamageAllowedInCreativeMode();
            this.setDamageBypassesArmor();
            this.setDamageIsAbsolute();
            this.isUnblockable();
        }
    @Override
    public ITextComponent getDeathMessage(EntityLivingBase entity) {
        ItemStack itemstack = damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase) damageSourceEntity).getHeldItem(EnumHand.MAIN_HAND) : null;
        String s = "death.attack.transcend";
        return new TextComponentTranslation(s,entity.getDisplayName(),itemstack.getDisplayName());
    }
    @Override
    public boolean isDifficultyScaled() {
        return false;
    }
}

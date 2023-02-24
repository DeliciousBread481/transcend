package huige233.transcend.compat.slash.specialeffects;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.IRemovable;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.item.ItemStack;

public class SEDataLock implements ISpecialEffect, IRemovable {
    private static final String EffectKey = "SEDataLock";

    public int getDefaultRequiredLevel(){
        return 0;
    }

    public boolean canCopy(ItemStack stack){
        return false;
    }

    public boolean canRemoval(ItemStack stack){
        return false;
    }

    public String getEffectKey(){
        return EffectKey;
    }

    public void register(){
        SlashBladeHooks.EventBus.register(this);
    }

    private boolean useBlade(ItemSlashBlade.ComboSequence sequence){
        if(sequence.useScabbard) return false;
        if(sequence == ItemSlashBlade.ComboSequence.None) return false;
        if(sequence == ItemSlashBlade.ComboSequence.Noutou) return false;
        return true;
    }
}

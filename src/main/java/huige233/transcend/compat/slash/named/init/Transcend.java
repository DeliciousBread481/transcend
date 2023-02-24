package huige233.transcend.compat.slash.named.init;

import huige233.transcend.compat.slash.BladeLoader;
import huige233.transcend.compat.slash.named.ItemTrSlashBlade;
import huige233.transcend.util.BladeUtils;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Transcend {
    String name = "flammpfeil.slashblade.named.tran";

    @SubscribeEvent
    public void init(LoadEvent.InitEvent event){
        ItemStack customblade = new ItemStack(BladeLoader.tran,1,0);
        NBTTagCompound tag = new NBTTagCompound();
        customblade.setTagCompound(tag);

        ItemTrSlashBlade.CurrentItemName.set(tag, name);
        ItemTrSlashBlade.CustomMaxDamage.set(tag, 50);
        ItemTrSlashBlade.IsDefaultBewitched.set(tag, true);
        ItemTrSlashBlade.isTrBlade.set(tag, true);
        ItemSlashBlade.TextureName.set(tag, "named/transcend/texture");
        ItemSlashBlade.ModelName.set(tag, "named/transcend/model");
        ItemSlashBlade.SpecialAttackType.set(tag, Integer.valueOf(678));
        ItemSlashBlade.RepairCount.set(tag, Integer.valueOf(100000));
        ItemSlashBlade.KillCount.set(tag, Integer.valueOf(1000000));
        ItemSlashBlade.ProudSoul.set(tag, Integer.valueOf(1000000));
        ItemSlashBlade.SummonedSwordColor.set(tag, 0x4091FF);
        SpecialEffects.addEffect(customblade, "SETranscend",1000);
        SpecialEffects.addEffect(customblade, "SEDataLock",0);
        customblade.addEnchantment(Enchantments.INFINITY, 127);
        customblade.addEnchantment(Enchantments.POWER, 127);
        customblade.addEnchantment(Enchantments.PUNCH, 127);
        BladeUtils.registerCustomItemStack(name, customblade);
        BladeUtils.TrNamedBlades.add(name);
    }
}

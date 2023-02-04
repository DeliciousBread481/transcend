package huige233.transcend.compat.slash.named;

import huige233.transcend.compat.slash.SlashUpdateEvent;
import huige233.transcend.compat.slash.specialattack.Delete;
import huige233.transcend.compat.slash.BladeLoader;
import huige233.transcend.util.ItemBladeUtils;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.UUID;

public class TranscendSlashBlade extends ItemTrSlashBlade {
    private boolean firstload;
    public boolean isUsing = false;

    public TranscendSlashBlade(ToolMaterial par2EnumToolMaterial, float baseAttackModifiers) {
        super(par2EnumToolMaterial, baseAttackModifiers);
        setTranslationKey("transcend.tran");
        setRegistryName("tran");
        ForgeRegistries.ITEMS.register(this);
        ItemBladeUtils.Tr_BLADE.add(this);
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int i, boolean b){
        super.onUpdate(stack, world, entity, i, b);
        if(!this.isUsing) SlashUpdateEvent.setTimestop(Boolean.valueOf(false));
        if(entity instanceof EntityPlayer) this.isUsing = false;
        ItemSlashBlade.specialAttacks.put(Integer.valueOf(678),new Delete());
        NBTTagCompound tag = stack.getTagCompound();
        tag.setInteger("HideFlags",6);
        ItemSlashBladeNamed.NamedBlades.add("flammpfeil.slashblade.named.tran");
        ItemSlashBladeNamed.CurrentItemName.set(tag, "flammpfeil.slashblade.named.tran");
        ItemSlashBladeNamed.CustomMaxDamage.set(tag, Integer.valueOf(32767));
        ItemSlashBladeNamed.IsDefaultBewitched.set(tag, Boolean.valueOf(true));
        ItemSlashBlade.SummonedSwordColor.set(tag, 0x4091FF);
        ItemSlashBlade.setBaseAttackModifier(tag, 32767.0F);
        ItemSlashBlade.SpecialAttackType.set(tag, Integer.valueOf(678));
        ItemSlashBlade.TextureName.set(tag, "named/transcend/texture");
        ItemSlashBlade.ModelName.set(tag, "named/transcend/model");
        ItemSlashBlade.RepairCount.set(tag, Integer.valueOf(100000));
        ItemSlashBlade.KillCount.set(tag, Integer.valueOf(1000000));
        ItemSlashBlade.ProudSoul.set(tag, Integer.valueOf(1000000));
        SpecialEffects.addEffect(stack, "SETranscend",1000);
        if(!this.firstload){
            this.firstload = true;
            stack.setTagCompound(tag);
        }
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) <= 0) stack.addEnchantment(Enchantments.INFINITY, 127);
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack) <= 0) stack.addEnchantment(Enchantments.POWER, 127);
        if(EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack) <= 0) stack.addEnchantment(Enchantments.PUNCH, 127);
        tag.setBoolean("Unbreakable", true);
    }

    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
        if (stack.getItem() == BladeLoader.tran) {
            NBTTagCompound tag = getItemTagCompound(stack);
            if (tag.hasUniqueId("Owner")) {
                UUID ownerid = tag.getUniqueId("Owner");
                if (ownerid.equals(player.getUniqueID()))
                    return false;
            }
        }
        return true;
    }

    public void setDamage(ItemStack itemStack, int Damage) {
        super.setDamage(itemStack, 0);
    }
}

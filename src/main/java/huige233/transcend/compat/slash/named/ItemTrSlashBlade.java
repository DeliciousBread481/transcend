package huige233.transcend.compat.slash.named;

import huige233.transcend.util.BladeUtils;
import huige233.transcend.util.TextUtils;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.TagPropertyAccessor;
import mods.flammpfeil.slashblade.specialattack.IJustSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumSet;
import java.util.List;

import static huige233.transcend.util.BladeUtils.TrNamedBlades;

public class ItemTrSlashBlade extends ItemSlashBladeNamed {
    public ItemTrSlashBlade(ToolMaterial material, float baseAttackModifiers) {
        super(material, baseAttackModifiers);
    }

    public static TagPropertyAccessor.TagPropertyBoolean isInCreativeTab = new TagPropertyAccessor.TagPropertyBoolean("IsInCreativeTab");
    public static TagPropertyAccessor.TagPropertyBoolean isTrBlade = new TagPropertyAccessor.TagPropertyBoolean("isTrBlade");

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformationSwordClass(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        EnumSet<SwordType> swordType = getSwordType(stack);
        NBTTagCompound tag = getItemTagCompound(stack);

        if(swordType.contains(SwordType.Enchanted)){
            if(swordType.contains(SwordType.Bewitched)){
                if (isTrBlade.get(tag)){
                    tooltip.add(TextUtils.makeFabulous(I18n.format("flammpfeil.info.transcend")));
                } else if(tag.hasUniqueId("Owner")) {
                    tooltip.add(String.format("§6%s", I18n.format("flammpfeil.swaepon.info.Infinity")));
                } else {
                    tooltip.add(String.format("§5%s", I18n.format("flammpfeil.swaepon.info.bewitched")));
                }
            }else{
                tooltip.add(String.format("§3%s", I18n.format("flammpfeil.swaepon.info.magic")));
            }
        }else{
           tooltip.add(String.format("§8%s", I18n.format("flammpfeil.swaepon.info.noname")));
        }
    }
/*
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformationSpecialAttack(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        EnumSet<SwordType> swordType = getSwordType(par1ItemStack);
        if(swordType.contains(SwordType.Bewitched)){
            NBTTagCompound tag = getItemTagCompound(par1ItemStack);
            String key = "flammpfeil.slashblade.specialattack." + getSpecialAttack(par1ItemStack).toString();
            if (isInCreativeTab.get(tag)){
                par3List.add("SA:???");
            }else{
                par3List.add(String.format("SA:%s",  I18n.format(key)));
            }
        }
    }

 */

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformationMaxAttack(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        NBTTagCompound tag = getItemTagCompound(par1ItemStack);
        float repair = RepairCount.get(tag);
        EnumSet<SwordType> swordType = getSwordType(par1ItemStack);
        par3List.add("");
        par3List.add("§4RankAttackDamage");
        String header;
        String template;
        if(swordType.contains(SwordType.FiercerEdge)){
            header = "§6B-A§r/§4S-SSS§r/§5Limit";
        }else{
            header = "§6B-SS§r/§4SSS§r/§5Limit";
        }
        if (isInCreativeTab.get(tag)){
            template = "§6???§r/§4????§r/§5?????";
        }else{
            template = "§6+%.1f§r/§4+%.1f§r/§5+%.1f";
        }
        float baseModif = getBaseAttackModifiers(tag);

        float maxBonus = RefineBase + repair;
        float level = par2EntityPlayer.experienceLevel;
        float ba = baseModif;
        float sss = (baseModif + Math.min(maxBonus,level));

        par3List.add(header);
        par3List.add(String.format(template,ba , sss , (baseModif + maxBonus)));
    }

    public void doChargeAttack(ItemStack stack, EntityPlayer par3EntityPlayer,boolean isJust){
        NBTTagCompound tag = getItemTagCompound(stack);
        if (isInCreativeTab.get(tag)) {
            return;
        }
        SpecialAttackBase sa = getSpecialAttack(stack);
        if(isJust && sa instanceof IJustSpecialAttack){
            ((IJustSpecialAttack)sa).doJustSpacialAttack(stack,par3EntityPlayer);
        }else {
            sa.doSpacialAttack(stack, par3EntityPlayer);
        }
        IsCharged.set(tag, true);
    }
/*
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (this.isInCreativeTab(tab)) {
            for(String bladename : TrNamedBlades){
                ItemStack blade = BladeUtils.getCustomBlade(bladename);
                NBTTagCompound tag = getItemTagCompound(blade);
                BaseAttackModifier.set(tag,0.0F);
                isInCreativeTab.set(tag,true);
                if(blade.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    blade.setItemDamage(0);
                }
                if(!blade.isEmpty()) {
                    subItems.add(blade);
                }
            }
        }
    }


 */
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab))
            for (String bladename : TrNamedBlades) {
                ItemStack blade = BladeUtils.getCustomBlade(bladename);
                NBTTagCompound tag = getItemTagCompound(blade);
                isInCreativeTab.set(tag, Boolean.valueOf(true));
                if (blade.getItemDamage() == 32767) blade.setItemDamage(0);
                if (!blade.isEmpty()) tag.setInteger("HideFlags",6);subItems.add(blade);
            }
    }
}

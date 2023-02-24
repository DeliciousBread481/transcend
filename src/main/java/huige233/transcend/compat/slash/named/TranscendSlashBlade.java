package huige233.transcend.compat.slash.named;

import huige233.transcend.compat.slash.BladeLoader;
import huige233.transcend.compat.slash.SlashUpdateEvent;
import huige233.transcend.util.ItemBladeUtils;
import huige233.transcend.util.ItemNBTHelper;
import huige233.transcend.util.TextUtils;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.ability.JustGuard;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.util.SilentUpdateItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

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
        NBTTagCompound tag = stack.getTagCompound();
        tag.setInteger("HideFlags",6);
        ItemSlashBlade.SummonedSwordColor.set(tag, 0x4091FF);
        if(entity instanceof EntityPlayer && ((EntityPlayer)entity).experienceLevel >= 1000){
            EntityPlayer player = (EntityPlayer)entity;
            ItemSlashBladeNamed.NamedBlades.add("flammpfeil.slashblade.named.tran");
            ItemSlashBladeNamed.CurrentItemName.set(tag, "flammpfeil.slashblade.named.tran");
            ItemSlashBlade.TextureName.set(tag, "named/transcend/texture");
            ItemSlashBlade.ModelName.set(tag, "named/transcend/model");
            ItemSlashBlade.setBaseAttackModifier(tag, 32767.0F);
            ItemSlashBlade.SpecialAttackType.set(tag, Integer.valueOf(678));
            ItemSlashBladeNamed.IsDefaultBewitched.set(tag, Boolean.valueOf(true));
            ItemSlashBladeNamed.CustomMaxDamage.set(tag, Integer.valueOf(32767));
            ItemSlashBlade.RepairCount.set(tag, Integer.valueOf(100000));
            ItemSlashBlade.KillCount.set(tag, Integer.valueOf(1000000));
            ItemSlashBlade.ProudSoul.set(tag, Integer.valueOf(1000000));
            if(((EntityPlayer) entity).getHeldItemMainhand().getItem() == this){
                int rankPoint = StylishRankManager.getTotalRankPoint(player);
                int aRankPoint = (int) (StylishRankManager.RankRange * 7D);
                int rankAmount = aRankPoint - rankPoint;
                StylishRankManager.addRankPoint(player,rankAmount);
            }
            ItemNBTHelper.setInt(stack,"empty",300);
        } else {
            EntityPlayer player = (EntityPlayer)entity;
            ItemSlashBladeNamed.NamedBlades.add("flammpfeil.slashblade.named.tran");
            ItemSlashBladeNamed.CurrentItemName.set(tag, "flammpfeil.slashblade.named.tran1");
            ItemSlashBlade.TextureName.set(tag, "named/transcend/scabbard");
            ItemSlashBlade.ModelName.set(tag, "named/transcend/blade");
            ItemSlashBlade.setBaseAttackModifier(tag, 0.0F);
            ItemSlashBlade.SpecialAttackType.set(tag, Integer.valueOf(2));
            ItemSlashBladeNamed.IsDefaultBewitched.set(tag, Boolean.valueOf(false));
            ItemSlashBladeNamed.CustomMaxDamage.set(tag, Integer.valueOf(0));
            ItemSlashBlade.RepairCount.set(tag, Integer.valueOf(1));
            ItemSlashBlade.KillCount.set(tag, Integer.valueOf(1000));
            ItemSlashBlade.ProudSoul.set(tag, Integer.valueOf(1000));
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, -1, 3, false, false));
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, -1, 3, false, false));
            if(ItemNBTHelper.getInt(stack,"empty",0) < 300){
                ItemNBTHelper.setInt(stack,"empty",ItemNBTHelper.getInt(stack,"empty",0)+1);
            }else if(ItemNBTHelper.getInt(stack,"empty",300) > 300){
                ItemNBTHelper.setInt(stack,"empty",300);
            }
        }

        SpecialEffects.addEffect(stack, "SEDataLock",0);
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

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformationSwordClass(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        EnumSet<SwordType> swordType = getSwordType(stack);
        NBTTagCompound tag = getItemTagCompound(stack);

        if(swordType.contains(SwordType.Enchanted)){
            //if(swordType.contains(SwordType.Bewitched)){
                if (isTrBlade.get(tag)) {
                    if (player.experienceLevel < 1000) {
                        tooltip.add(TextUtils.makeSANICS(I18n.format("flammpfeil.info.no.transcend")));
                    } else {
                        tooltip.add(TextUtils.makeFabulous(I18n.format("flammpfeil.info.transcend")));
                    }
                }
            }
        //}
    }

    @SideOnly(Side.CLIENT)
    public void addInformationRepairCount(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        NBTTagCompound tag = getItemTagCompound(par1ItemStack);
        int repair = RepairCount.get(tag);
        if (0 < repair && repair <= 100000) {
            par3List.add(String.format("Refine : %d", repair));
        }else {
            par3List.add(String.format("§eRefine : %d", repair));
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformationSpecialEffec(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        NBTTagCompound etag = getSpecialEffect(par1ItemStack);
        Set<String> tagKeys = etag.getKeySet();
        if (tagKeys.size() != 0) {
            int playerLevel = par2EntityPlayer.experienceLevel;
            par3List.add("");
            Iterator var8 = tagKeys.iterator();

            while(var8.hasNext()) {
                String key = (String)var8.next();
                int reqiredLevel = etag.getInteger(key);
                par3List.add(TextUtils.makeFabulousL(I18n.format("slashblade.seffect.name." + key, new Object[0])) + "§r " + (reqiredLevel <= playerLevel ? "§e" : "§8") + reqiredLevel);
            }

        }
    }

    @Override
    public ActionResult<ItemStack> func_77659_a(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        if (hand == EnumHand.OFF_HAND) {
            return new ActionResult(EnumActionResult.FAIL, itemStackIn);
        } else {
            if(playerIn.experienceLevel < 1000) return new ActionResult(EnumActionResult.FAIL, itemStackIn);
            JustGuard.setJustGuardState(playerIn);
            NBTTagCompound tag = getItemTagCompound(itemStackIn);
            long prevAttackTime = LastActionTime.get(tag);
            long currentTime = playerIn.world.getTotalWorldTime();
            ComboSequence comboSeq = getComboSequence(tag);
            this.nextAttackSequence(itemStackIn, comboSeq, playerIn);
            SilentUpdateItem.silentUpdateItem(playerIn, hand);
            playerIn.setActiveHand(hand);
            return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
        }
    }

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

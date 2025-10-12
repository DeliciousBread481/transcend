package huige233.transcend.compat.tinkers;

import huige233.transcend.compat.ThaumcraftSword;
import huige233.transcend.lib.TranscendDamageSources;
import huige233.transcend.util.ArmorUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitTranscend extends AbstractTrait {
    public TraitTranscend() {
        super("transcend", TextFormatting.DARK_GRAY);
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        NBTTagCompound toolTag = TagUtil.getToolTag(rootCompound);
        ToolNBT state = TagUtil.getToolStats(rootCompound);
        state.durability = 999999;
        state.attack = 999999.0f;
        state.speed = 999999.0f;
        int modifiers = toolTag.getInteger("FreeModifiers");
        toolTag.setInteger("FreeModifiers", 100 + modifiers);
        rootCompound.setBoolean("Unbreakable", true);
        NBTTagCompound tag = TagUtil.getToolTag(rootCompound);
        float attack = 999999;
        attack += tag.getFloat("Attack");
        tag.setFloat("Attack", attack);
        TagUtil.setToolTag(rootCompound, toolTag);
    }

    public int onToolDamage(ItemStack tool,int damage,int newdamage,EntityLivingBase entity){
        return 0;
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase entity, float damage, boolean isCritical) {
        if(!entity.world.isRemote){
            if(entity instanceof EntityPlayer){
                if (ArmorUtils.fullEquipped((EntityPlayer) entity)) {
                    player.sendMessage(new TextComponentTranslation("sword_to_armor"));
                }
                EntityPlayer p = (EntityPlayer) entity;
                p.attackEntityFrom((new TranscendDamageSources(player)).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute(), Float.MAX_VALUE);
                p.getCombatTracker().trackDamage(new EntityDamageSource("transcend", player), Float.MAX_VALUE, Float.MAX_VALUE);
                p.clearActivePotions();
                p.inventory.dropAllItems();
                p.setHealth(0.0f);
            } else {
                entity.clearActivePotions();
                entity.attackEntityFrom((new TranscendDamageSources(player)).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute(), Float.MAX_VALUE);
                entity.getCombatTracker().trackDamage(new EntityDamageSource("transcend", player), Float.MAX_VALUE, Float.MAX_VALUE);
                entity.setHealth(0.0f);
                //entity.onDeath(new EntityDamageSource("transcend",player));
            }
            entity.onKillCommand();
            if (Loader.isModLoaded("thaumcraft")) {
                ThaumcraftSword.damageEntity(entity);
            }
        }
    }
}

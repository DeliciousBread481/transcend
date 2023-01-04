package huige233.transcend.compat.tinkers;

import c4.conarm.common.armor.utils.ArmorTagUtil;
import c4.conarm.lib.armor.ArmorNBT;
import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitArmorFlawless extends AbstractArmorTrait {
    public static final TraitArmorFlawless a = new TraitArmorFlawless();
    public TraitArmorFlawless() {
        super("flawless",0x777777);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public float onHurt(ItemStack armor,EntityPlayer player, DamageSource source, float damage, float newDamage, LivingHurtEvent evt){
        return damage-1000;
    }

    public float onDamaged(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingDamageEvent evt) {
        if (player.getHealth() - newDamage <= 0.0F && random.nextFloat() <= 0.1F) {
            player.heal(2.0F);
            evt.setCanceled(true);
        }
        return damage-1000;
    }

    @Override
    public void onArmorTick(ItemStack armor, World world, EntityPlayer player){
        player.setAir(300);
        player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        player.capabilities.allowFlying = true;
        player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 300, 14, false, false));
        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 300, 14, false, false));
        player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 300, 2, false, false));
        player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 300, 2, false, false));
        player.stepHeight=0.6f;
        player.addPotionEffect(new PotionEffect(MobEffects.LUCK, 300, 9, false, false));
        if(player.isBurning()) {
            player.extinguish();
        }
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        super.applyEffect(rootCompound, modifierTag);
        ArmorNBT data = ArmorTagUtil.getArmorStats(rootCompound);
        ArmorNBT orginal = ArmorTagUtil.getOriginalArmorStats(rootCompound);
        data.modifiers += orginal.modifiers + 10;
        TagUtil.setToolTag(rootCompound,data.get());
    }
}

package huige233.transcend.items.tools;

import huige233.transcend.Transcend;
import huige233.transcend.items.ItemBase;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemAdapting extends ItemBase {
    protected boolean useable = false;
    public ItemAdapting(String name){
        super(name, Transcend.TranscendTab);
    }
    public float getPower(ItemStack stack) {
        return 999999f;
    }
    public float getRange(ItemStack stack) {
        return 999999f;
    }
    public int getCoolDownTicks(ItemStack stack) {
        return (int) 1;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return  99999;
    }
    public int getMaxItemUseDuration(ItemStack stack){
        return 72000;
    }
    public EnumAction getItemUseAction(ItemStack stack){
        return EnumAction.BOW;
    }
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.PUNCH || enchantment == Enchantments.INFINITY)
        {
            return false;
        }
        if (enchantment == Enchantments.POWER || enchantment == Enchantments.UNBREAKING)
        {
            return true;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity,int timeLeft){
        if(!useable){
            return;
        }
        onCreatureStoppedUsing(stack,world,entity,timeLeft);
        entity.swingArm(entity.getHeldItemMainhand() == stack ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
        if(!world.isRemote){
            if(entity instanceof EntityPlayer) {
                EntityPlayer p = (EntityPlayer) entity;
                {
                    p.getCooldownTracker().setCooldown(this,getCoolDownTicks(stack));
                }
            }
        }
    }
    public void onCreatureStoppedUsing(ItemStack stack,World world,EntityLivingBase entity,int timeLeft){

    }
}

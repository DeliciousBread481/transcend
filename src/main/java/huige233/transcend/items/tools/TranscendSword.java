package huige233.transcend.items.tools;

import huige233.transcend.Transcend;
import huige233.transcend.init.ModItems;
import huige233.transcend.util.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class TranscendSword extends ItemSword implements IHasModel {
    public TranscendSword(String name, CreativeTabs tabs,ToolMaterial material){
        super(material);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(tabs);
        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Transcend.proxy.registerItemRenderer(this, 0, "inventory");
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        killEntity(stack, target, attacker);
        return true;
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        super.onLeftClickEntity(stack, player, entity);
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase)entity;
            killEntity(stack, target, (EntityLivingBase)player);
            return true;
        }
        return false;
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        super.onItemRightClick(world, player, hand);
        ItemStack stack = player.getHeldItem(hand);
        killRangeEntity(player.world, stack, (EntityLivingBase)player, 16);
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }

    public void killRangeEntity(World world, ItemStack stack, EntityLivingBase attacker, int range) {
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity((Entity)attacker, new AxisAlignedBB(attacker.posX - range, attacker.posY - range, attacker.posZ - range, attacker.posX + range, attacker.posY + range, attacker.posZ + range));
        for (Entity target : list) {
            if (target instanceof EntityLivingBase)
                killEntity(stack, (EntityLivingBase)target, attacker);
        }
    }

    public void killEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        DamageSource source_infinity = (new DamageSource("infinity")).setDamageBypassesArmor().setDamageAllowedInCreativeMode().setMagicDamage().setDamageIsAbsolute();
        target.hurtResistantTime = 0;
        target.getCombatTracker().trackDamage(source_infinity, Float.MAX_VALUE, Float.MAX_VALUE);
        target.setHealth(0.0F);
        target.setDead();
        target.onDeath(source_infinity);
    }

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(entityIn instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityIn;
            player.capabilities.allowFlying=true;
            player.isDead=false;
            player.world.playerEntities.add(player);
            player.world.onEntityAdded(player);
            super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        }
    }
}

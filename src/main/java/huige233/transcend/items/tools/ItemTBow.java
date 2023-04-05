package huige233.transcend.items.tools;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import huige233.transcend.Main;
import huige233.transcend.init.ModItems;
import huige233.transcend.util.IHasModel;
import huige233.transcend.util.Reference;
import huige233.transcend.util.other.MCTimer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ItemTBow extends ItemBow implements IHasModel {
    public static final UUID soulDamageUUID = UUID.fromString("2CCDC290-A885-473A-973F-CDC5C918773B");
    public ItemTBow(String name, CreativeTabs tabs){
        setTranslationKey(name);
        setRegistryName(name);
        setMaxDamage(-1);
        setMaxStackSize(1);
        setCreativeTab(tabs);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    public EnumRarity getRarity(ItemStack stack )
    {
        return(ModItems.COSMIC_RARITY);
    }

    @Override
    public boolean isRepairable() {
        return true;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
        return true;
    }

    @SubscribeEvent
    public static void hurt(LivingHurtEvent event){
        Entity entity = event.getEntity();
        World world = entity.world;
        if(world.isRemote){
            EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
            if(attacker instanceof EntityPlayer){
                EntityPlayer Player = (EntityPlayer) attacker;
                ItemStack stack = Player.getHeldItemMainhand();
                ItemStack stack1 = Player.getActiveItemStack();
                if(stack.getItem() == ModItems.TBow){
                    final long time = world.getWorldTime() + 24000L;
                    boolean flagNight = (time % 24000L > 13850L && time % 24000L < 23000L);
                    if(flagNight) world.addWeatherEffect(new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, false));
                    if(Player.experienceLevel >= 50 && stack1.getItem() == Items.DIAMOND){
                        stack1.setCount(stack1.getCount()-1);
                        event.setAmount(event.getAmount()*20);
                        drainHealth((EntityLivingBase) event.getEntityLiving());
                    }
                }
            }
        }
    }

    private static void drainHealth(EntityLivingBase target){
        double l = 0;
        IAttributeInstance a = target.getAttributeMap().getAttributeInstanceByName(SharedMonsterAttributes.MAX_HEALTH.getName());
        AttributeModifier attr = a.getModifier(soulDamageUUID);

        if(attr != null) {
            l = attr.getAmount();
            if(l == -1) return;
        }
        l -= l*0.9f;

        if(l <= -1) l=-1;
        Multimap<String, AttributeModifier> multimap = HashMultimap.create();
        multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(soulDamageUUID, "Soul Damage", l, 2));
        target.getAttributeMap().applyAttributeModifiers(multimap);
        if (l <= -1) {
            target.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
        }
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft){
        if(entityLiving instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) entityLiving;
            if(player.experienceLevel >= 50){
                player.experienceLevel -= 20;
            }
            int i = this.getMaxItemUseDuration(stack) - timeLeft;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, player, i, true);
            if(i < 0) return;
            float f = getArrowVelocity(i);
            if ((double) f >= 0.1D) {
                if (!worldIn.isRemote) {
                    ItemArrow itemarrow = new ItemArrow();
                    ItemStack item = new ItemStack(itemarrow,1);
                    EntityArrow entityarrow = itemarrow.createArrow(worldIn, item, player);
                    entityarrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);
                    if (f == 1.0F) {
                        entityarrow.setIsCritical(true);
                    }
                    entityarrow.setKnockbackStrength(10);
                    entityarrow.setFire(1000);
                    entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                    worldIn.spawnEntity(entityarrow);
                }
                worldIn.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                player.addStat(Objects.requireNonNull(StatList.getObjectUseStats(this)));
            }
        }
    }

    @SubscribeEvent
    public void entTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().world.isRemote)
            return;

        if (MCTimer.serverTimer % (20 * 10) == 0) {
            IAttributeInstance a = event.getEntityLiving().getAttributeMap().getAttributeInstanceByName(SharedMonsterAttributes.MAX_HEALTH.getName());

            AttributeModifier attr = a.getModifier(soulDamageUUID);
            if (attr != null) {
                double l = attr.getAmount();
                l = (Math.round(l * 39D) + 1D) / 39D;
                a.removeModifier(attr);
                if (l < 0) {
                    a.applyModifier(new AttributeModifier(soulDamageUUID, "Soul Damage", l, 2));
                }
            }
        }
    }
}

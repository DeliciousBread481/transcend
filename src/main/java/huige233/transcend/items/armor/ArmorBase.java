package huige233.transcend.items.armor;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import huige233.transcend.Transcend;
import huige233.transcend.compat.PsiCompat;
import huige233.transcend.init.ModItems;
import huige233.transcend.items.EntityFireImmune;
import huige233.transcend.lib.TranscendDamageSources;
import huige233.transcend.util.ArmorUtils;
import huige233.transcend.util.IHasModel;
import huige233.transcend.util.ItemNBTHelper;
import huige233.transcend.util.Reference;
import huige233.transcend.util.handlers.EnergeticHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.IVisDiscountGear;
import vazkii.psi.common.lib.LibMisc;

import java.util.List;
import java.util.UUID;

import static net.minecraft.entity.EntityLivingBase.SWIM_SPEED;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
@Optional.Interface(iface = "thaumcraft.api.items.IVisDiscountGear", modid = "thaumcraft")
@Optional.Interface(iface = "thaumcraft.api.items.IRechargable", modid = "thaumcraft")
@Optional.Interface(iface = "thaumcraft.api.items.IGoggles",modid = "thaumcraft")
public class ArmorBase extends ItemArmor implements IHasModel, IVisDiscountGear, IRechargable, IGoggles, EnergeticHandler {
    public ArmorBase(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, CreativeTabs tab) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(tab);
        ModItems.ITEMS.add(this);
    }


    @Override
    public void registerModels() {
        Transcend.proxy.registerItemRenderer(this, 0, "inventory");
    }

    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if(event.getEntityLiving() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (ArmorUtils.fullEquipped(player)) {
                event.setCanceled(true);
                event.getEntityLiving().setHealth(player.getMaxHealth());
                event.getEntityLiving().isDead = false;
                event.getEntityLiving().deathTime = 0;
                //player.preparePlayerToSpawn();
                player.world.playerEntities.add(player);
                player.world.onEntityAdded(player);
                player.world.setEntityState(event.getEntityLiving(), (byte) 35);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || event.isCanceled()) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (!player.isServerWorld()) return;
        if (ArmorUtils.fullEquipped(player)) {
            event.getSource().getTrueSource().setDead();
            event.getSource().getTrueSource().attackEntityFrom((new TranscendDamageSources(player)).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute(), Float.MAX_VALUE);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void PlayerDropsEvent(PlayerDropsEvent event){
        if(ArmorUtils.fullEquipped(event.getEntityPlayer())){
            event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public static void LivingAttackEvent(LivingAttackEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || event.isCanceled())
            return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (ArmorUtils.fullEquipped(player)) {
            Entity attacker = event.getSource().getTrueSource();
            if(player.world.isRemote) {
                DamageSource ds = attacker == null ? new DamageSource("infinity") : new EntityDamageSource("infinity", attacker);
                DamageSource ds1 = attacker == null ? new DamageSource("transcend") : new EntityDamageSource("transcend", attacker);
                player.getCombatTracker().trackDamage(ds, Float.MAX_VALUE, Float.MAX_VALUE);
                player.getCombatTracker().trackDamage(ds1, Float.MAX_VALUE, Float.MAX_VALUE);
            }
            if(Loader.isModLoaded(LibMisc.MOD_ID)){
                if (attacker instanceof EntityPlayer) {
                    PsiCompat.onPlayerAttack(player, (EntityPlayer) attacker);
                }
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void LivingSetAttackTargetEvent(LivingSetAttackTargetEvent event){
        if(event.getTarget() instanceof EntityPlayer){
            if(ArmorUtils.fullEquipped((EntityPlayer) event.getTarget())){
                ((EntityLiving) event.getEntityLiving()).setAttackTarget(event.getEntityLiving());
            }
        }
    }

    @Override
    public void onArmorTick(@NotNull World world, @NotNull EntityPlayer player, @NotNull ItemStack itemStack) {
        if (this.armorType == EntityEquipmentSlot.HEAD) {
            player.setAir(300);
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 300, 14, false, false));
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        } else if (this.armorType == EntityEquipmentSlot.CHEST) {
            player.capabilities.allowFlying = true;
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 300, 14, false, false));
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 300, 14, false, false));
            List<PotionEffect> effects = Lists.newArrayList(player.getActivePotionEffects());
            for(PotionEffect potion : Collections2.filter(effects,potion -> potion.getPotion().isBadEffect())) {
                player.removePotionEffect(potion.getPotion());
            }
        } else if (this.armorType == EntityEquipmentSlot.LEGS) {
            player.getFoodStats().addStats(20, 20.0f);
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 300, 2, false, false));
            player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 300, 2, false, false));
            player.stepHeight=3;
        } else if (this.armorType == EntityEquipmentSlot.FEET) {
            player.setFire(0);
            player.addPotionEffect(new PotionEffect(MobEffects.LUCK, 300, 9, false, false));
            player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 300, 44, false, false));
            if(player.isBurning()) {
                player.extinguish();
            }
        }
        if (ArmorUtils.fullEquipped(player)) {
            player.setEntityInvulnerable(true);
            player.capabilities.disableDamage=true;
            //player.noClip=player.capabilities.isFlying;   player.setNoGravity(true);
            player.setHealth(player.getMaxHealth());
            if(player.getAbsorptionAmount()<1000){
                player.setAbsorptionAmount(2000);

            }
            if(player.getHeldItemMainhand().getItem()==ModItems.TRANSCEND_SWORD){
                if(!ItemNBTHelper.getBoolean(player.getHeldItem(EnumHand.MAIN_HAND),"Invul",false)){
                    ItemNBTHelper.setBoolean(player.getHeldItem(EnumHand.MAIN_HAND),"Invul",true);
                }
            }
            if(player.isDead){
                player.world.playerEntities.add(player);
                player.world.onEntityAdded(player);
                player.isDead=false;
                player.deathTime=0;
            }
            player.inventoryContainer.detectAndSendChanges();
        } else if(!ArmorUtils.fullEquipped(player)) {
            if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == ModItems.TRANSCEND_SWORD) {
                if (ItemNBTHelper.getBoolean(player.getHeldItem(EnumHand.MAIN_HAND), "Invul", false)) {
                    NonNullList<ItemStack> armor = player.inventory.armorInventory;
                    armor.set(3, new ItemStack(ModItems.FLAWLESS_HELMET));
                    armor.set(2, new ItemStack(ModItems.FLAWLESS_CHESTPLATE));
                    armor.set(1, new ItemStack(ModItems.FLAWLESS_LEGGINGS));
                    armor.set(0, new ItemStack(ModItems.FLAWLESS_BOOTS));
                }
            }
        }
        if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == ModItems.TRANSCEND_SWORD) {
            if (ItemNBTHelper.getBoolean(player.getHeldItem(EnumHand.MAIN_HAND), "Invul", false)) {
                if(player.isDead){
                    player.world.playerEntities.add(player);
                    player.world.onEntityAdded(player);
                    player.isDead=false;
                    player.deathTime=0;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerRender(RenderPlayerEvent.Pre event){
        EntityPlayer player = event.getEntityPlayer();
        if(ArmorUtils.fullEquipped(player)&&player.getHeldItemMainhand().getItem() == ModItems.TRANSCEND_SWORD){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void DeSpawn(LivingSpawnEvent.AllowDespawn event){
        if(event.getEntityLiving() instanceof EntityPlayer){
            if(ArmorUtils.fullEquipped((EntityPlayer) event.getEntityLiving())) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void LivingDrop(LivingDropsEvent event){
        EntityLivingBase entity = event.getEntityLiving();
        if(entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) entity;
            if(ArmorUtils.fullEquipped(player)){
                player.world.playerEntities.add(player);
                player.world.onEntityAdded(player);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void PlayerDrop(PlayerDropsEvent event){
        EntityPlayer player = event.getEntityPlayer();
        if(ArmorUtils.fullEquipped(player)){
            player.world.playerEntities.add(player);
            player.world.onEntityAdded(player);
            event.setCanceled(true);
        }
    }

    @Override
    public Multimap<String,AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> attrib = super.getAttributeModifiers(slot, stack);
        Item item = stack.getItem();
        UUID uuid = new UUID(slot.toString().hashCode(), 0);
        if (slot == EntityEquipmentSlot.HEAD) {
            if(item == ModItems.FLAWLESS_HELMET) {
                attrib.put(SWIM_SPEED.getName(),new AttributeModifier(uuid,"Flawless",0.2,1));
                attrib.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(uuid, "Flawless", 512, 0));
                attrib.put(SharedMonsterAttributes.LUCK.getName(), new AttributeModifier(uuid, "Flawless", 250, 0));
            }
        } else if (slot == EntityEquipmentSlot.CHEST) {
            if(item == ModItems.FLAWLESS_CHESTPLATE) {
                attrib.put(SharedMonsterAttributes.LUCK.getName(), new AttributeModifier(uuid, "Flawless", 250, 0));
                attrib.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(uuid, "Flawless", 512, 0));
                attrib.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(uuid, "Flawless", 1000, 0));
            }
        } else if (slot == EntityEquipmentSlot.LEGS) {
            if(item == ModItems.FLAWLESS_LEGGINGS) {
                attrib.put(SharedMonsterAttributes.LUCK.getName(), new AttributeModifier(uuid, "Flawless", 250, 0));
                attrib.put(SharedMonsterAttributes.FLYING_SPEED.getName(),new AttributeModifier(uuid,"Flawless",0.2,1));
                attrib.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(uuid, "Flawless", 512, 0));
            }
        } else if (slot == EntityEquipmentSlot.FEET) {
            if(item == ModItems.FLAWLESS_BOOTS) {
                attrib.put(SharedMonsterAttributes.LUCK.getName(), new AttributeModifier(uuid, "Flawless", 250, 0));
                attrib.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(uuid, "Flawless", 512, 0));
                attrib.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(uuid, "Flawless", 0.2, 1));
            }
        }
        return attrib;
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    public Entity createEntity(World world,Entity location, ItemStack itemstack) {
        return new EntityFireImmune(world,location.posX,location.posY,location.posZ,itemstack);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(@NotNull ItemStack stack) {
        return (false);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            if(ArmorUtils.fullEquipped((EntityPlayer) entity)) {
                EntityPlayer player = (EntityPlayer) entity;
                player.setEntityInvulnerable(true);
                player.isSpectator();
                ItemNBTHelper.setFloat(stack,"tc.charge",10000.0f);
                if(player.getHealth() < player.getMaxHealth()) {
                    player.setHealth(player.getMaxHealth());
                }
                if(player.getPosition().getY() < -100){
                    player.setPosition(player.getPosition().getX(),100,player.getPosition().getZ());
                }
                if(player.getMaxHealth()<=2000){
                    player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2000);
                }
            }
        }
    }

    public @NotNull EnumRarity getRarity(@NotNull ItemStack stack) {
        return (ModItems.COSMIC_RARITY);
    }

    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> stack) {
        if(tab == Transcend.TranscendTab) {
            ItemStack itemstack = new ItemStack(this);
            ItemNBTHelper.setByte(itemstack, "TC.RUNIC", (byte) 127);
            ItemNBTHelper.setInt(itemstack,"ncRadiationResistance",32767);
            stack.add(itemstack);
        }
    }

    @Override
    @Optional.Method(modid = "thaumcraft")
    public int getVisDiscount(ItemStack itemStack, EntityPlayer entityPlayer) {
        return 100;
    }


    @Override
    @Optional.Method(modid = "thaumcraft")
    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return 10000;
    }

    @Override
    @Optional.Method(modid = "thaumcraft")
    public EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return EnumChargeDisplay.PERIODIC;
    }

    @Override
    @Optional.Method(modid = "thaumcraft")
    public boolean showIngamePopups(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return true;
    }

    public int getEnergyStored(){
        return 50000000;
    }
}

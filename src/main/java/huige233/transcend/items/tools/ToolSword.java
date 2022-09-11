package huige233.transcend.items.tools;

import baubles.common.Baubles;
import cofh.redstoneflux.RedstoneFluxProps;
import cofh.redstoneflux.api.IEnergyContainerItem;
import com.google.common.collect.Multimap;
import huige233.transcend.Main;
import huige233.transcend.compat.ThaumcraftSword;
import huige233.transcend.init.ModItems;
import huige233.transcend.items.fireimmune;
import huige233.transcend.lib.TranscendDamageSources;
import huige233.transcend.util.*;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.mana.ICreativeManaProvider;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.common.lib.LibMisc;

import java.util.List;
import java.util.UUID;

import static com.brandon3055.draconicevolution.integration.BaublesHelper.getBaubles;

@Optional.Interface(modid = RedstoneFluxProps.MOD_ID, iface = "cofh.redstoneflux.api.IEnergyContainerItem")
@Optional.Interface(modid = IC2.MODID, iface = "ic2.api.item.ISpecialElectricItem")
@Optional.Interface(modid = LibMisc.MOD_ID,iface = "vazkii.botania.api.mana.IManaTooltipDisplay")
@Optional.Interface(modid = LibMisc.MOD_ID,iface = "vazkii.botania.api.mana.IManaItem")
@Optional.Interface(modid = LibMisc.MOD_ID,iface = "vazkii.botania.api.mana.ICreativeManaProvider")
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ToolSword extends ItemSword implements IHasModel, ICreativeManaProvider, IManaItem, IManaTooltipDisplay{

    protected static final int MAX_MANA = Integer.MAX_VALUE;
    private static final String TAG_CREATIVE = "creative";
    private static final String TAG_ONE_USE = "oneUse";
    private static final String TAG_MANA = "mana";

    public ToolSword(String name, CreativeTabs tab, ToolMaterial material) {
        super(material);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(tab);
        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(@NotNull ItemStack par1ItemStack) {
        return false;
    }
    public boolean hitEntity(@NotNull ItemStack stack, @NotNull EntityLivingBase entity, EntityLivingBase player) {
        if(!entity.world.isRemote){
            if(ItemNBTHelper.getBoolean(stack,"Destruction",false)) {
                if(entity instanceof EntityPlayer){
                    if (ArmorUtils.fullEquipped((EntityPlayer) entity)) {
                        player.sendMessage(new TextComponentTranslation("sword_to_armor"));
                        return true;
                    }
                    SwordUtil.killPlayer((EntityPlayer) entity,player);
                    entity.world.removeEntity(entity);
                } else {
                    SwordUtil.killEntity(entity);
                    entity.world.removeEntity(entity);
                }
                if(player instanceof EntityPlayerMP){
                    BlockPos pos = player.getPosition();
                    ((EntityPlayerMP) player).connection.sendPacket(new SPacketCustomSound("transcend:killer", SoundCategory.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), 1.0F, 1.0F));
                }
            }
            if(entity instanceof EntityPlayer){
                if (ArmorUtils.fullEquipped((EntityPlayer) entity)) {
                    player.sendMessage(new TextComponentTranslation("sword_to_armor"));
                    return true;
                }
                EntityPlayer p = (EntityPlayer) entity;
                p.attackEntityFrom((new TranscendDamageSources(player)).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute(), Float.MAX_VALUE);
                p.getCombatTracker().trackDamage(new EntityDamageSource("transcend", player), Float.MAX_VALUE, Float.MAX_VALUE);
                p.clearActivePotions();
                p.inventory.dropAllItems();
                p.setHealth(0.0f);
                p.onDeath(new EntityDamageSource("transcend", player));
                p.setDead();
                player.isDead=true;
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
        return true;
    }

    public void getSubItems(@NotNull CreativeTabs tab,NonNullList<ItemStack> stack) {
        ItemStack create = new ItemStack(ModItems.TRANSCEND_SWORD);
        if(tab == Main.TranscendTab && Loader.isModLoaded(LibMisc.MOD_ID)){
            setMana(create, MAX_MANA);
            isCreative(create);
            setStackCreative(create);
        }
        stack.add(create);
    }

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull EntityPlayer player, Entity entity) {
        if(!entity.world.isRemote){
            if(ItemNBTHelper.getBoolean(stack,"Destruction",false)) {
                if(entity instanceof EntityPlayer){
                    if (ArmorUtils.fullEquipped((EntityPlayer) entity)) {
                        player.sendMessage(new TextComponentTranslation("sword_to_armor"));
                        return false;
                    }
                    SwordUtil.killPlayer((EntityPlayer) entity,player);
                    entity.world.removeEntity(entity);
                } else if(entity instanceof EntityLivingBase){
                    SwordUtil.killEntityLiving((EntityLivingBase) entity,player);
                    entity.world.removeEntity(entity);
                } else {
                    SwordUtil.killEntity(entity);
                    entity.world.removeEntity(entity);
                }
                if(player instanceof EntityPlayerMP){
                    BlockPos pos = player.getPosition();
                    ((EntityPlayerMP) player).connection.sendPacket(new SPacketCustomSound("transcend:killer", SoundCategory.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), 1.0F, 1.0F));
                }
                entity.onKillCommand();
            }
            if(entity instanceof EntityPlayer){
                if (ArmorUtils.fullEquipped((EntityPlayer) entity)) {
                    player.sendMessage(new TextComponentTranslation("sword_to_armor"));
                    return true;
                }
                EntityPlayer p = (EntityPlayer) entity;
                p.clearActivePotions();
                p.inventory.dropAllItems();
                p.setHealth(0.0f);
                p.onDeath(new EntityDamageSource("transcend", player));
            } else {
                entity.attackEntityFrom((new TranscendDamageSources(player)).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute(), Float.MAX_VALUE);
                entity.setDead();
            }
        }
        return false;
    }



    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World world, EntityPlayer player, @NotNull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(!world.isRemote) {
            if (!player.isSneaking()) {
                boolean destruction = ItemNBTHelper.getBoolean(stack, "Destruction", false);
                if (!destruction) {
                    ItemNBTHelper.setBoolean(stack, "Destruction", true);
                    stack.setStackDisplayName(TextFormatting.RED + I18n.translateToLocal("item.transcend.sword.destruction.name"));
                    player.swingArm(hand);
                } else {
                    ItemNBTHelper.setBoolean(stack, "Destruction", false);
                    stack.setStackDisplayName(TextFormatting.RED + I18n.translateToLocal("item.transcend_sword.name"));
                    player.swingArm(hand);
                }
            } else if (player.isSneaking()) {
                if (ItemNBTHelper.getBoolean(stack, "Destruction", false)) {
                    int count = SwordUtil.killRangeEntity(world, player);
                    player.sendMessage(new TextComponentTranslation("transcend.sword.ranger_kill", 50, count));
                }
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    public boolean hasCustomEntity (@NotNull ItemStack stack){
        return true;
    }

    @Override
    public int getDamage(ItemStack stack) {
        return 0;
    }

    public Entity createEntity(@NotNull World world, @NotNull Entity location, @NotNull ItemStack itemstack) {
        return new fireimmune(world,location,itemstack);
    }

    public @NotNull Multimap<String, AttributeModifier> getAttributeModifiers(@NotNull EntityEquipmentSlot slot, @NotNull ItemStack stack) {
        Multimap<String, AttributeModifier> attrib = super.getAttributeModifiers(slot, stack);
        UUID uuid = new UUID((slot.toString()).hashCode(), 0);
        if(slot == EntityEquipmentSlot.MAINHAND) {
            attrib.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(uuid, "Weapon modifier", 0.99, 1));
            attrib.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(uuid, "Weapon modifier", 256, 0));
        }
        return attrib;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack stack, World world, List<String> tooltip, @NotNull ITooltipFlag flag){
        tooltip.add(TextUtils.makeFabulous(I18n.translateToLocal("tooltip.transcend_sword1.desc")) + " " + TextUtils.makeFabulous(I18n.translateToLocal("tooltip.transcend_sword2.desc")));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof ToolSword) {
            for (int x = 0; x < event.getToolTip().size(); ++x) {
                if (((String) event.getToolTip().get(x)).contains(I18n.translateToLocal("attribute.name.generic.attackDamage")) || ((String) event.getToolTip().get(x)).contains(I18n.translateToLocal("Attack Damage"))) {
                    if (event.getItemStack().getItem() == ModItems.TRANSCEND_SWORD) {
                        event.getToolTip().set(x, TextFormatting.BLUE + "+" + TextUtils.makeFabulous(I18n.translateToLocal("tip.transcend")) + " " + TextFormatting.BLUE + I18n.translateToLocal("attribute.name.generic.attackDamage"));
                        event.getToolTip().set(x + 1, TextFormatting.BLUE + "+" + TextUtils.makeFabulous(I18n.translateToLocal("tip.transcend")) + " " + TextFormatting.BLUE + I18n.translateToLocal("attribute.name.generic.reachDistance"));
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack,World world,Entity entity,int itemSlot,boolean isSelected){
        if(Loader.isModLoaded(IC2.MODID)){
            ic2charge(stack,world,entity,itemSlot,isSelected);
        }
        if(Loader.isModLoaded(RedstoneFluxProps.MOD_ID)){
            rfReceive(stack,world,entity,itemSlot,isSelected);
        }
    }

    @Optional.Method(modid = RedstoneFluxProps.MOD_ID)
    private void rfReceive(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack receive = player.inventory.getStackInSlot(i);
                if (!receive.isEmpty()) {
                    if (receive.getItem() instanceof IEnergyContainerItem) {
                        IEnergyContainerItem energy = (IEnergyContainerItem) receive.getItem();
                        energy.receiveEnergy(receive, energy.getMaxEnergyStored(receive) - energy.getEnergyStored(receive), false);
                    }
                    if (receive.hasCapability(CapabilityEnergy.ENERGY, null)) {
                        IEnergyStorage cap = (IEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
                        if ((cap != null) && (cap.canReceive())) {
                            cap.receiveEnergy(Integer.MAX_VALUE, false);
                        }
                    }
                }
            }
            if (Loader.isModLoaded(Baubles.MODID)) {
                for (ItemStack receive : getBaubles(player)) {
                    if (receive.getItem() instanceof IEnergyContainerItem) {
                        IEnergyContainerItem energy = (IEnergyContainerItem) receive.getItem();
                        energy.receiveEnergy(receive, energy.getMaxEnergyStored(receive) - energy.getEnergyStored(receive), false);
                    }
                    if (receive.hasCapability(CapabilityEnergy.ENERGY, null)) {
                        IEnergyStorage cap = (IEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null);
                        if ((cap != null) && (cap.canReceive())) {
                            cap.receiveEnergy(Integer.MAX_VALUE, false);
                        }
                    }
                }
            }
        }
    }

    @Optional.Method(modid = IC2.MODID)
    private void ic2charge(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack toCharge = player.inventory.getStackInSlot(i);
                if (!toCharge.isEmpty()) {
                    ElectricItem.manager.charge(toCharge, ElectricItem.manager.getMaxCharge(toCharge) - ElectricItem.manager.getCharge(toCharge), Integer.MAX_VALUE, true, false);
                }
            }
            if (Loader.isModLoaded(Baubles.MODID)) {
                for (ItemStack toCharge : getBaubles(player)) {
                    ElectricItem.manager.charge(toCharge, ElectricItem.manager.getMaxCharge(toCharge) - ElectricItem.manager.getCharge(toCharge), Integer.MAX_VALUE, true, false);
                }
            }
        }
    }

    public @NotNull EnumRarity getRarity(@NotNull ItemStack stack)
    {
        return(ModItems.COSMIC_RARITY);
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    public static void setMana(ItemStack stack, int mana) {
        ItemNBTHelper.setInt(stack, TAG_MANA, MAX_MANA-1);
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    public static void setStackCreative(ItemStack stack) {
        ItemNBTHelper.setBoolean(stack, TAG_CREATIVE, true);
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public int getMana(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, TAG_MANA, 0);
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public int getMaxMana(ItemStack stack) {
        return MAX_MANA-1;
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public void addMana(ItemStack stack, int mana) {
        setMana(stack, Math.min(getMana(stack) + mana, getMaxMana(stack)));
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) {
        return !ItemNBTHelper.getBoolean(stack, TAG_ONE_USE, false);
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) {
        return true;
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public boolean canExportManaToPool(ItemStack stack, TileEntity pool) {
        return true;
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) {
        return true;
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public boolean isNoExport(ItemStack stack) {
        return true;
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public float getManaFractionForDisplay(ItemStack stack) {
        return (float) getMana(stack) / (float)getMaxMana(stack);
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public boolean isCreative(ItemStack stack) {
        return false;
    }
}



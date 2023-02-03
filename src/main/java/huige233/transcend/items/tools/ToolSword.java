package huige233.transcend.items.tools;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import cofh.redstoneflux.RedstoneFluxProps;
import cofh.redstoneflux.api.IEnergyContainerItem;
import com.anotherstar.common.LoliPickaxe;
import com.anotherstar.common.entity.IEntityLoli;
import com.anotherstar.common.gui.ILoliInventory;
import com.anotherstar.common.item.tool.ILoli;
import com.anotherstar.util.LoliPickaxeUtil;
import com.google.common.collect.Multimap;
import huige233.transcend.Main;
import huige233.transcend.compat.ThaumcraftSword;
import huige233.transcend.init.ModItems;
import huige233.transcend.items.FireImmune;
import huige233.transcend.lib.TranscendDamageSources;
import huige233.transcend.util.*;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;
import ic2.core.IC2;
import ic2.core.item.InfiniteElectricItemManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.mana.ICreativeManaProvider;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.common.lib.LibMisc;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

import static huige233.transcend.util.handlers.BaublesHelper.getBaubles;

@Optional.Interface(modid = RedstoneFluxProps.MOD_ID, iface = "cofh.redstoneflux.api.IEnergyContainerItem")
@Optional.Interface(modid = IC2.MODID, iface = "ic2.api.item.ISpecialElectricItem")
@Optional.Interface(modid = LibMisc.MOD_ID,iface = "vazkii.botania.api.mana.IManaTooltipDisplay")
@Optional.Interface(modid = LibMisc.MOD_ID,iface = "vazkii.botania.api.mana.IManaItem")
@Optional.Interface(modid = LibMisc.MOD_ID,iface = "vazkii.botania.api.mana.ICreativeManaProvider")
@Optional.Interface(modid = LibMisc.MOD_ID,iface = "vazkii.botania.api.mana.IManaUsingItem")
@Optional.Interface(modid = LoliPickaxe.MODID, iface = "com.anotherstar.common.item.tool.ILoli")
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ToolSword extends ItemSword implements IHasModel, ICreativeManaProvider, IManaItem, IManaTooltipDisplay, IEnergyContainerItem, IManaUsingItem, ILoli, ISpecialElectricItem {
    protected static final int MAX_MANA = Integer.MAX_VALUE;
    private static final String TAG_CREATIVE = "creative";
    private static final String TAG_ONE_USE = "oneUse";
    private static final String TAG_MANA = "mana";
    public static BaseAttribute transcendDamage = new RangedAttribute(null,"transcend.damage",0.0D,0.0D,Double.MAX_VALUE);
    private static final DamageSource ADMIN_KILL = (new DamageSource("administrative.kill")).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();

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

    public void attackEntity(ItemStack stack,EntityLivingBase entity,EntityPlayer player){
        boolean result = true;
        if (ArmorUtils.fullEquipped((EntityPlayer) entity) || entity instanceof EntityPlayer && entity.getName().equals("huige233")) {
            result = false;
            player.sendMessage(new TextComponentTranslation("sword_to_armor"));
        }
        if(ItemNBTHelper.getBoolean(stack,"Destruction",false)) {
            if(player.getName().equals("huige233") && result){
                SwordUtil.kill(entity,player);
            }
            if(Loader.isModLoaded("lolipickaxe") && result){
                leftClickEntity(player, entity);
                if(entity instanceof IEntityLoli){
                    ((IEntityLoli)entity).setDispersal(true);
                }
            }
            if(entity instanceof EntityPlayer && result){
                EntityPlayer p = (EntityPlayer) entity;
                p.capabilities.disableDamage=false;
                IBaublesItemHandler handler = BaublesApi.getBaublesHandler((EntityPlayer) entity);
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack1 = handler.getStackInSlot(i);
                    if (stack1.getItem() instanceof IBauble) {
                        stack1.setCount(0);
                    }
                }
                SwordUtil.killPlayer((EntityPlayer) entity,player);
            } else if(entity instanceof EntityLivingBase){
                SwordUtil.killEntityLiving((EntityLivingBase) entity,player);
            } else {
                SwordUtil.killEntity(entity);
            }
            if(player instanceof EntityPlayerMP){
                BlockPos pos = player.getPosition();
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketCustomSound("transcend:killer", SoundCategory.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), 1.0F, 1.0F));
            }
            entity.onKillCommand();
        } else {
            if(entity instanceof EntityPlayer && result){
                EntityPlayer p = (EntityPlayer) entity;
                p.attackEntityFrom(ADMIN_KILL,Float.MAX_VALUE);
                p.attackEntityFrom((new TranscendDamageSources(player)).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute(), Float.MAX_VALUE);
                p.getCombatTracker().trackDamage(new EntityDamageSource("transcend", player), Float.MAX_VALUE, Float.MAX_VALUE);
                p.clearActivePotions();
                //p.inventory.dropAllItems();
                p.onDeath(new EntityDamageSource("transcend", player));
                p.setDead();
                p.isDead=true;
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
    public boolean hitEntity(@NotNull ItemStack stack, @NotNull EntityLivingBase entity, EntityLivingBase player) {
        if(!entity.world.isRemote){
            attackEntity(stack,entity,(EntityPlayer) player);
        }
        return true;
    }

    public void getSubItems(@NotNull CreativeTabs tab,NonNullList<ItemStack> stack) {
        ItemStack create = new ItemStack(ModItems.TRANSCEND_SWORD);
        if(tab == Main.TranscendTab){
            if(Loader.isModLoaded(LibMisc.MOD_ID)) {
                setMana(create, MAX_MANA);
                isCreative(create);
                setStackCreative(create);
            }
            stack.add(create);
        }
    }



    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull EntityPlayer player, Entity entity) {
        if(!entity.world.isRemote){
            attackEntity(stack,(EntityLivingBase) entity,player);
        }
        return false;
    }

    @Optional.Method(modid = LoliPickaxe.MODID)
    public boolean leftClickEntity(EntityLivingBase loli, Entity entity) {
        if (!entity.world.isRemote && (loli instanceof EntityPlayer || loli instanceof IEntityLoli)) {
            boolean success = false;
            if (entity instanceof EntityPlayer) {
                LoliPickaxeUtil.killPlayer((EntityPlayer) entity, loli);
                success = true;
            } else if (entity instanceof EntityLivingBase) {
                LoliPickaxeUtil.killEntityLiving((EntityLivingBase) entity, loli);
                success = true;
            } else if (!(entity instanceof EntityLivingBase)) {
                LoliPickaxeUtil.killEntity(entity);
                success = true;
            }
            if (success && loli instanceof EntityPlayerMP) {
                BlockPos pos = loli.getPosition();
                ((EntityPlayerMP) loli).connection.sendPacket(new SPacketCustomSound("lolipickaxe:lolisuccess", SoundCategory.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), 1.0F, 1.0F));
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
                    stack.setStackDisplayName(TextFormatting.GOLD + I18n.translateToLocal("item.transcend.sword.destruction.name"));
                    player.swingArm(hand);
                } else {
                    ItemNBTHelper.setBoolean(stack, "Destruction", false);
                    stack.setStackDisplayName(TextFormatting.GOLD + I18n.translateToLocal("item.transcend_sword.name"));
                    player.swingArm(hand);
                }
            } else if (player.isSneaking()) {
                if (ItemNBTHelper.getBoolean(stack, "Destruction", false)) {
                    int count = SwordUtil.killRangeEntity(world, player);
                    player.sendMessage(new TextComponentTranslation(I18n.translateToLocal("transcend.sword.ranger_kill"), 50, count));
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
        return new FireImmune(world,location,itemstack);
    }

    @Nonnull
    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> attrib = super.getItemAttributeModifiers(slot);
        UUID uuid = new UUID((slot.toString()).hashCode(), 0);
        if(slot == EntityEquipmentSlot.MAINHAND) {
            attrib.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(uuid, "Weapon modifier", 0.99, 1));
            attrib.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(uuid, "Weapon modifier", 256, 0));
            attrib.put(transcendDamage.getName(),new AttributeModifier(uuid,"Weapon modifier",9999,0));
        }
        return attrib;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack stack, World world, List<String> tooltip, @NotNull ITooltipFlag flag){
        tooltip.add(TextUtils.makeFabulous(I18n.translateToLocal("tooltip.transcend_sword1.desc")) + " " + TextUtils.makeFabulous(I18n.translateToLocal("tooltip.transcend_sword2.desc")));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public static void onTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof ToolSword) {
            for (int x = 0; x < event.getToolTip().size(); ++x) {
                if (((String) event.getToolTip().get(x)).contains(I18n.translateToLocal("attribute.name.generic.attackDamage")) || ((String) event.getToolTip().get(x)).contains(I18n.translateToLocal("Attack Damage"))) {
                    if (event.getItemStack().getItem() == ModItems.TRANSCEND_SWORD) {
                        if (!Loader.isModLoaded("lolipickaxe")) {
                            event.getToolTip().set(x, TextFormatting.BLUE + "+" + TextUtils.makeFabulous(I18n.translateToLocal("tip.transcend")) + " " + TextFormatting.BLUE + I18n.translateToLocal("attribute.name.generic.attackDamage"));
                            event.getToolTip().set(x + 1, TextFormatting.BLUE + "+" + TextUtils.makeFabulous(I18n.translateToLocal("tip.transcend")) + " " + TextFormatting.BLUE + I18n.translateToLocal("attribute.name.transcend.damage"));
                            event.getToolTip().set(x + 2, TextFormatting.BLUE + "+" + TextUtils.makeFabulous(I18n.translateToLocal("tip.transcend")) + " " + TextFormatting.BLUE + I18n.translateToLocal("attribute.name.generic.reachDistance"));
                        }else {
                            //event.getToolTip().remove(x); * 3
                            int s = event.getToolTip().size();
                            event.getToolTip().set(x + 1," " + TextUtils.makeFabulous(I18n.translateToLocal("tip.transcend")) + " " + TextFormatting.BLUE + I18n.translateToLocal("attribute.name.transcend.damage"));
                            event.getToolTip().set(x + 2," " + TextUtils.makeFabulous(I18n.translateToLocal("tip.transcend")) + " " + TextFormatting.BLUE + I18n.translateToLocal("attribute.name.generic.reachDistance"));
                            //event.getToolTip().add(s-2,TextFormatting.BLUE + "+" + TextUtils.makeFabulous(I18n.translateToLocal("tip.transcend")) + " " + TextFormatting.BLUE + I18n.translateToLocal("attribute.name.generic.attackDamage"));
                        }
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
        if(entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) entity;
            NBTTagCompound nbt;
            if(stack.hasTagCompound()){
                nbt = stack.getTagCompound();
            } else {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            if (!nbt.hasKey("Owner")) {
                nbt.setString("Owner", player.getName());
            }
            if(!ArmorUtils.fullEquipped(player)) {
                if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == ModItems.TRANSCEND_SWORD) {
                    if (ItemNBTHelper.getBoolean(player.getHeldItem(EnumHand.MAIN_HAND), "Invul", false) && ItemNBTHelper.getBoolean(stack, "Destruction", false)) {
                        NonNullList<ItemStack> armor = player.inventory.armorInventory;
                        armor.set(3, new ItemStack(ModItems.FLAWLESS_HELMET));
                        armor.set(2, new ItemStack(ModItems.FLAWLESS_CHESTPLATE));
                        armor.set(1, new ItemStack(ModItems.FLAWLESS_LEGGINGS));
                        armor.set(0, new ItemStack(ModItems.FLAWLESS_BOOTS));
                    }
                }
            }
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
                        energy.receiveEnergy(receive, Integer.MAX_VALUE, false);
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

    public @NotNull EnumRarity getRarity(@NotNull ItemStack stack) {
        return(ModItems.COSMIC_RARITY);
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
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
        return true;
    }


    @Optional.Method(modid = RedstoneFluxProps.MOD_ID)
    @Override
    public int receiveEnergy(ItemStack itemStack, int i, boolean b) {
        return 0;
    }

    @Optional.Method(modid = RedstoneFluxProps.MOD_ID)
    @Override
    public int extractEnergy(ItemStack itemStack, int i, boolean b) {
        return Integer.MAX_VALUE;
    }

    @Optional.Method(modid = RedstoneFluxProps.MOD_ID)
    @Override
    public int getEnergyStored(ItemStack itemStack) {
        return Integer.MAX_VALUE;
    }

    @Optional.Method(modid = RedstoneFluxProps.MOD_ID)
    @Override
    public int getMaxEnergyStored(ItemStack itemStack) {
        return Integer.MAX_VALUE;
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public boolean usesMana(ItemStack stack) {
        return false;
    }

    @Optional.Method(modid = LoliPickaxe.MODID)
    @Override
    public String getOwner(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt.hasKey("Owner")) {
                return nbt.getString("Owner");
            }
        }
        return "";
    }

    @Optional.Method(modid = LoliPickaxe.MODID)
    @Override
    public int getRange(ItemStack itemStack) {
        return 0;
    }

    @Optional.Method(modid = LoliPickaxe.MODID)
    @Override
    public boolean hasInventory(ItemStack itemStack) {
        return true;
    }

    @Optional.Method(modid = LoliPickaxe.MODID)
    @Override
    public ILoliInventory getInventory(ItemStack itemStack) {
        return null;
    }

    @Override
    @Optional.Method(modid = IC2.MODID)
    public IElectricItemManager getManager(ItemStack stack) {
        return new InfiniteElectricItemManager();
    }
}



package huige233.transcend.items.tools;

import com.google.common.collect.Multimap;
import huige233.transcend.Main;
import huige233.transcend.compat.ThaumcraftSword;
import huige233.transcend.init.ModItems;
import huige233.transcend.items.fireimmune;
import huige233.transcend.lib.TranscendDamageSources;
import huige233.transcend.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static huige233.transcend.compat.botaniasword.setMana;
import static huige233.transcend.compat.botaniasword.setStackCreative;

@Optional.Interface(iface="vazkii.botania.api.mana.IManaItem",modid="botania")
@Optional.Interface(iface="vazkii.botania.api.mana.IManaTooltipDisplay",modid="botania")
@Optional.Interface(iface="vazkii.botania.api.mana.ICreativeManaProvider",modid="botania")
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ToolSword extends ItemSword implements IHasModel{

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

    public void getSubItems(@NotNull CreativeTabs tab, @NotNull NonNullList<ItemStack> stack) {
        if(tab == Main.TranscendTab){
            if(Loader.isModLoaded("botania")) {
                ItemStack create = new ItemStack(ModItems.TRANSCEND_SWORD);
                setMana(create, MAX_MANA);
                setStackCreative(create);
                stack.add(create);
            }
        }
    }


    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(@NotNull ItemStack par1ItemStack) {
        return false;
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event){
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(ArmorUtils.fullEquipped(player)){
            if(player.isDead){
                player.isDead=false;
            }
            if(!player.world.playerEntities.contains(player)){
                player.world.playerEntities.add(player);
                player.world.onEntityAdded(player);
            }
        }
    }

    public boolean hitEntity(@NotNull ItemStack stack, @NotNull EntityLivingBase target, EntityLivingBase player) {
        if (!player.world.isRemote) {
            if (target instanceof EntityPlayer) {
                EntityPlayer t = (EntityPlayer) target;
                if (ArmorUtils.fullEquipped(t)) {
                    target.setHealth(4);
                    return true;
                }
                if (Loader.isModLoaded("thaumcraft")) {
                    ThaumcraftSword.damageEntity(target);
                }
                t.inventory.dropAllItems();
            }
            target.attackEntityFrom((new TranscendDamageSources(player)).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute(), Float.MAX_VALUE);
            target.getCombatTracker().trackDamage(new EntityDamageSource("transcend", player), Float.MAX_VALUE, Float.MAX_VALUE);
            target.maxHurtResistantTime=0;
            target.hurtResistantTime=0;
            target.hurtTime=0;
            if(ItemNBTHelper.getBoolean(stack, "Destruction", false)) {
                target.setEntityInvulnerable(false);
                target.onKillCommand();
                target.isDead = true;
                target.hurtResistantTime=0;;
                target.deathTime = 0;
            }
            target.setHealth(0);
            target.onDeath(new EntityDamageSource("transcend", player));
        }
        return true;
    }

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull EntityPlayer player, Entity entity) {
        if(!entity.world.isRemote) {
            if(entity instanceof EntityPlayer) {
                EntityPlayer t = (EntityPlayer) entity;
                if(!ArmorUtils.fullEquipped(t)) {
                    t.clearActivePotions();
                    t.inventory.dropAllItems();
                    t.attackEntityFrom((new TranscendDamageSources(player)).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute(), Float.MAX_VALUE);
                    t.setHealth(0);
                    t.onDeath(new EntityDamageSource("transcend", player));
                } else {
                    EntityLivingBase en = (EntityLivingBase) entity;
                    en.setHealth(0.0f);
                    en.clearActivePotions();
                    en.attackEntityFrom((new TranscendDamageSources(player)).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute(), Float.MAX_VALUE);
                    en.onDeath(new EntityDamageSource("transcend",player));
                }
            }
            if(ItemNBTHelper.getBoolean(stack, "Destruction", false)) {
                if(entity instanceof EntityPlayer){
                    SwordUtil.killPlayer((EntityPlayer) entity,player);
                } else if (entity instanceof EntityLivingBase) {
                    SwordUtil.killEntityLiving((EntityLivingBase) entity,player);
                } else{
                    SwordUtil.killEntity(entity);
                }
                entity.onKillCommand();
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
        return super.onItemRightClick(world, player, hand);
    }

    public boolean hasCustomEntity (@NotNull ItemStack stack){
        return true;
    }

    public void setDamage(@NotNull ItemStack stack, int damage) {
        super.setDamage(stack, 0);
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

    public @NotNull EnumRarity getRarity(@NotNull ItemStack stack)
    {
        return(ModItems.COSMIC_RARITY);
    }
}



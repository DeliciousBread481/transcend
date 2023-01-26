package huige233.transcend.items.tools;

import com.google.common.collect.Multimap;
import huige233.transcend.Main;
import huige233.transcend.compat.ThaumcraftSword;
import huige233.transcend.init.ModItems;
import huige233.transcend.util.IHasModel;
import huige233.transcend.util.ItemNBTHelper;
import huige233.transcend.util.Reference;
import huige233.transcend.util.handlers.TranscendPacketHandler;
import huige233.transcend.packet.PacketLeftClick;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.mana.BurstProperties;
import vazkii.botania.api.mana.ILensEffect;
import vazkii.botania.common.core.handler.ModSounds;
import vazkii.botania.common.entity.EntityManaBurst;
import vazkii.botania.common.lib.LibMisc;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
@Optional.Interface(modid = LibMisc.MOD_ID,iface = "vazkii.botania.api.mana.ILensEffect")
public class ToolWarp extends ItemSword implements IHasModel, ILensEffect {
    public ToolWarp(String name, CreativeTabs tab, ToolMaterial material) {
        super(material);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(tab);
        MinecraftForge.EVENT_BUS.register(this);
        ModItems.ITEMS.add(this);
    }
    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this,0,"inventory");
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (Loader.isModLoaded("thaumcraft")) {
            ThaumcraftSword.warpsword(stack,target,attacker);
            stack.damageItem(1, attacker);
        }
        if(attacker.getEntityWorld() instanceof WorldServer){
            if(!attacker.getEntityWorld().isRemote) {
                ((WorldServer) attacker.getEntityWorld()).spawnParticle(EnumParticleTypes.LAVA, attacker.posX, attacker.posY, attacker.posZ, 0, 0, 0.);
            }
        }
        stack.damageItem(1,attacker);
        return true;
    }
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag){
        if(Loader.isModLoaded("thaumcraft")){
            tooltip.add(TextFormatting.GOLD+I18n.translateToLocal("tooltip.warp_sword2.desc"));
        }else {
            tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("tooltip.warp_sword1.desc"));
        }
    }

    @SubscribeEvent
    public void leftClick(PlayerInteractEvent.LeftClickEmpty evt) {
        if (!evt.getItemStack().isEmpty()
                && evt.getItemStack().getItem() == this) {
            TranscendPacketHandler.INSTANCE.sendToServer(new PacketLeftClick());
        }
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> attrib = super.getAttributeModifiers(slot, stack);
        UUID uuid = new UUID((slot.toString()).hashCode(), 0);
        if(slot == EntityEquipmentSlot.MAINHAND) {
            attrib.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(uuid, "Weapon modifier", 0.30, 1));
            attrib.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(uuid, "Weapon modifier", 1.5, 0));
        }
        return attrib;
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        int anvil = ItemNBTHelper.getInt(stack,"Anvil",0);
        if(anvil < 10000000) ItemNBTHelper.setInt(stack,"Anvil",anvil + 100);
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @SubscribeEvent
    public void attackEntity(AttackEntityEvent event){
        if(!event.getEntityPlayer().world.isRemote){
            trySpawnBurst(event.getEntityPlayer());
        }
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    public void trySpawnBurst(EntityPlayer player){
        if(!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == this) {
            EntityManaBurst burst = getBurst(player, player.getHeldItemMainhand());
            player.world.spawnEntity(burst);
            player.world.playSound(null, player.posX, player.posY, player.posZ, ModSounds.terraBlade, SoundCategory.PLAYERS, 0.4F, 1.4F);
        }
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World world, EntityPlayer player, @NotNull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(!world.isRemote) {
            int anvil = ItemNBTHelper.getInt(stack,"Anvil",0);
            if(anvil > 3000) {
                ItemNBTHelper.setInt(stack,"Anvil",anvil - 3000);
                player.world.createExplosion(player, player.posX, player.posY, player.posZ, 50, false);
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    public EntityManaBurst getBurst(EntityPlayer player,ItemStack stack){
        EntityManaBurst burst = new EntityManaBurst(player, EnumHand.MAIN_HAND);
        float motionModifier = 7F;

        burst.setColor(1222385);
        burst.setMana(1000000);
        burst.setStartingMana(1000000);
        burst.setMinManaLoss(40);
        burst.setManaLossPerTick(40F);
        burst.setGravity(0f);
        burst.setMotion(burst.motionX * motionModifier, burst.motionY * motionModifier, burst.motionZ * motionModifier);

        ItemStack lens = stack.copy();
        ItemNBTHelper.setString(lens, "attackerUsername", player.getName());
        burst.setSourceLens(lens);

        return burst;
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public void apply(ItemStack stack, BurstProperties props) {

    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public boolean collideBurst(IManaBurst burst, RayTraceResult pos, boolean isManaBlock, boolean dead, ItemStack stack) {
        return dead;
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    public void updateBurst(IManaBurst burst, ItemStack stack){
        EntityThrowable entity = (EntityThrowable) burst;
        AxisAlignedBB axis =  new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).grow(1);
        List<EntityLivingBase> entities = entity.world.getEntitiesWithinAABB(EntityLivingBase.class, axis);
        String attacker = ItemNBTHelper.getString(burst.getSourceLens(), "attackerUsername", "");

        burst.setColor(generateRGB((((EntityThrowable) burst).getUniqueID().hashCode() +entity.world.getWorldTime()) /5.0f));

        for(EntityLivingBase living : entities) {
            if (living instanceof EntityPlayer && (living.getName().equals(attacker) || FMLCommonHandler.instance().getMinecraftServerInstance() != null && !FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()))
                continue;

            if (living.hurtTime == 0) {
                int cost = 1000000 / 3;
                int mana = burst.getMana();
                if (mana >= cost) {
                    burst.setMana(mana - cost);
                    float damage = 25f;
                    if (!burst.isFake() && !entity.world.isRemote) {
                        EntityPlayer player = living.world.getPlayerEntityByName(attacker);
                        living.attackEntityFrom(player == null ? DamageSource.MAGIC : DamageSource.causePlayerDamage(player), damage);
                        entity.setDead();
                        break;
                    }
                }
            }
        }
    }

    private int generateRGB(float time){
        int r = (int) (Math.sin(time * 0.3) * 127 + 128);
        int g = (int) (Math.sin(time * 0.3 + 2) * 127 + 128);
        int b = (int) (Math.sin(time * 0.3 + 4) * 127 + 128);
        return r << 16 | g << 8 | b;
    }

    @Optional.Method(modid = LibMisc.MOD_ID)
    @Override
    public boolean doParticles(IManaBurst burst, ItemStack stack) {
        return true;
    }

}

package huige233.transcend.items.compat;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import huige233.transcend.Main;
import huige233.transcend.items.ItemBase;
import huige233.transcend.util.IHasModel;
import huige233.transcend.util.ItemNBTHelper;
import huige233.transcend.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static huige233.transcend.util.handlers.BaublesHelper.getBaubles;

@Mod.EventBusSubscriber
public class FragmentLan extends ItemBase implements IBauble, IHasModel {
    public FragmentLan(){
        super("fragment_lan", Main.TranscendTab);
        addPropertyOverride(new ResourceLocation(Reference.MOD_ID,"empty"),(stack, worldIn, entityIn) -> FragmentLan.hasEmpty(stack)?0f:1f);
        this.maxStackSize=1;
    }
    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.TRINKET;
    }

    public void onWornTick(ItemStack itemstack, EntityLivingBase player){
        if(player instanceof EntityPlayer && !player.world.isRemote){
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION,-1,0,false,false));
            BlockPos pos = player.getPosition();
            int range = 8;
            List<Entity> list = player.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range));
            list.remove(player);
            for(Entity e : list){
                EntityLivingBase b = (EntityLivingBase) e;
                b.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS,-1,0,false,false));
                b.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS,-1,1,false,false));
            }
        }
        if(ItemNBTHelper.getInt(itemstack,"empty",1000) < 999){
            ItemNBTHelper.setInt(itemstack,"empty",ItemNBTHelper.getInt(itemstack,"empty",1000)+1);
        } else if(ItemNBTHelper.getInt(itemstack,"empty",1000) == 999){
            ItemNBTHelper.setInt(itemstack,"empty",1000);
            player.sendMessage(new TextComponentTranslation("FragmentLan.on"));
        } else if(ItemNBTHelper.getInt(itemstack,"empty",1000) > 1000){
            ItemNBTHelper.setInt(itemstack,"empty",1000);
        }
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }

    @SubscribeEvent
    public static void die(LivingDeathEvent event){
        if(event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            for (ItemStack a : getBaubles(player)) {
                if (a.getItem() instanceof FragmentLan) {
                    if(ItemNBTHelper.getInt(a,"empty",1000) == 1000) {
                        event.setCanceled(true);
                        player.setHealth(5);
                        ItemNBTHelper.setInt(a,"empty",0);
                        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 80, 4, false, false));
                        player.sendMessage(new TextComponentTranslation("FragmentLan.noDeath"));
                    }
                }
            }
        }
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if(!player.world.isRemote) {
            Multimap<String, AttributeModifier> attributes = HashMultimap.create();
            fillModifiers(attributes, stack);
            player.getAttributeMap().applyAttributeModifiers(attributes);
        }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        if(!player.world.isRemote) {
            Multimap<String, AttributeModifier> attributes = HashMultimap.create();
            fillModifiers(attributes, stack);
            player.getAttributeMap().removeAttributeModifiers(attributes);
        }
    }

    private void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
        if(stack.isEmpty()) return;
        UUID uuid = new UUID((stack.toString()).hashCode(), 0);
        attributes.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(uuid, "Fragment Lan", 2, 0).setSaved(false));
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack stack, World world, List<String> tooltip, @NotNull ITooltipFlag flag){
        tooltip.add(TextFormatting.YELLOW+(I18n.translateToLocal("tooltip.fragment_lan.desc")));
    }

    public static boolean hasEmpty(ItemStack stack){
        return ItemNBTHelper.getInt(stack, "empty", 0) == 1000;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(tab == Main.TranscendTab) {
            ItemStack itemStack = new ItemStack(this);
            ItemNBTHelper.setInt(itemStack, "empty", 1000);
            items.add(itemStack);
        }
    }
}

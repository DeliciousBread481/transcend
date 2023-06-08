package huige233.transcend.items.compat;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import huige233.transcend.Transcend;
import huige233.transcend.items.ItemBase;
import huige233.transcend.lib.AnvilDamageSource;
import huige233.transcend.util.ItemNBTHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static huige233.transcend.util.handlers.BaublesHelper.getBaubles;

@Mod.EventBusSubscriber
public class AnvilCompat extends ItemBase implements IBauble {
    public AnvilCompat() {
        super("anvil_compat", Transcend.TranscendTab);
        this.maxStackSize = 1;
    }

    private static final int i = 120;
    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    public void onWornTick(ItemStack itemstack, EntityLivingBase player){
        if(player instanceof EntityPlayer && !player.world.isRemote){
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, -1, 1, false, false));
            player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, -1, 0, false, false));
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,-1,1, false, false));
        }
        int anvil = ItemNBTHelper.getInt(itemstack,"Anvil",i);
        if(anvil<i){
            ItemNBTHelper.setInt(itemstack,"Anvil",anvil + 1);
            if(anvil + 1 == i){
                player.sendMessage(new TextComponentTranslation("Anvil.Attack"));
            }
        } else if(anvil>i){
            ItemNBTHelper.setInt(itemstack,"Anvil",0);
        }
    }

    @SubscribeEvent
    public static void onLivingKnock(LivingKnockBackEvent event) {
        if(event.getEntity().world.isRemote) return;
        if(event.getAttacker() instanceof EntityPlayer){
            for(ItemStack a : getBaubles((EntityPlayer) event.getAttacker())){
                if(a.getItem() instanceof AnvilCompat){
                    event.setStrength(event.getStrength() * 1.5F);
                    if(ItemNBTHelper.getInt(a,"Anvil",120) == 120){
                        ItemNBTHelper.setInt(a,"Anvil",0);
                        event.getEntity().attackEntityFrom(new AnvilDamageSource(event.getAttacker()),25);
                        event.getAttacker().world.playSound((EntityPlayer)null,event.getAttacker().getPosition(),SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1, 1);
                    }
                }
            }
        }
        if(event.getEntity() instanceof EntityPlayer){
            for(ItemStack a : getBaubles((EntityPlayer) event.getEntity())){
                if(a.getItem() instanceof AnvilCompat){
                    event.setCanceled(true);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack stack, World world, List<String> tooltip, @NotNull ITooltipFlag flag){
        tooltip.add(TextFormatting.GRAY + (I18n.translateToLocal("tooltip.anvil_compat.desc1")));
        tooltip.add(TextFormatting.GRAY + (I18n.translateToLocal("tooltip.anvil_compat.desc2")));
        tooltip.add(TextFormatting.GRAY + (I18n.translateToLocal("tooltip.anvil_compat.desc3")));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(tab == Transcend.TranscendTab) {
            ItemStack itemStack = new ItemStack(this);
            ItemNBTHelper.setInt(itemStack, "Anvil", 120);
            items.add(itemStack);
        }
    }
}

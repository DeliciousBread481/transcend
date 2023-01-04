package huige233.transcend.items.compat;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import huige233.transcend.Main;
import huige233.transcend.items.ItemBase;
import huige233.transcend.packet.PacketTJump;
import huige233.transcend.util.handlers.TranscendPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static huige233.transcend.util.handlers.BaublesHelper.getBaubles;

@Mod.EventBusSubscriber
public class ThunderSkin extends ItemBase implements IBauble {
    public ThunderSkin() {
        super("thunder_skin", Main.TranscendTab);
        this.maxStackSize = 1;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.TRINKET;
    }

    @SideOnly(Side.CLIENT)
    private static boolean canDoubleJump;

    @SideOnly(Side.CLIENT)
    private static boolean canThreeJump;

    @SideOnly(Side.CLIENT)
    private static boolean canFourJump;

    @SideOnly(Side.CLIENT)
    private static boolean canFiveJump;

    @SideOnly(Side.CLIENT)
    private static boolean hasReleasedJumpKey;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player != null) {
                if ((player.onGround || player.isOnLadder()) && !player.isInWater()) {
                    hasReleasedJumpKey = false;
                    canDoubleJump = true;
                    canThreeJump = true;
                    canFourJump = true;
                    canFiveJump = true;
                } else {
                    if (!player.movementInput.jump) {
                        hasReleasedJumpKey = true;
                    } else {
                        if (!player.capabilities.isFlying && canDoubleJump && hasReleasedJumpKey) {
                            canDoubleJump = false;
                            hasReleasedJumpKey = false;
                            for (ItemStack a : getBaubles(player)) {
                                if (a.getItem() instanceof ThunderSkin) {
                                    TranscendPacketHandler.INSTANCE.sendToServer(new PacketTJump.PacketJump());
                                    player.jump();
                                    player.fallDistance = 0;
                                }
                            }
                        }
                    }
                    if (player.movementInput.jump && !canDoubleJump) {
                        if (!player.capabilities.isFlying && canThreeJump && hasReleasedJumpKey) {
                            canThreeJump = false;
                            hasReleasedJumpKey = false;
                            for (ItemStack a : getBaubles(player)) {
                                if (a.getItem() instanceof ThunderSkin) {
                                    TranscendPacketHandler.INSTANCE.sendToServer(new PacketTJump.PacketJump());
                                    player.jump();
                                    player.fallDistance = 0;
                                }
                            }
                        }
                    }
                    if (player.movementInput.jump && !canThreeJump) {
                        if (!player.capabilities.isFlying && canFourJump && hasReleasedJumpKey) {
                            canFourJump = false;
                            hasReleasedJumpKey = false;
                            for (ItemStack a : getBaubles(player)) {
                                if (a.getItem() instanceof ThunderSkin) {
                                    TranscendPacketHandler.INSTANCE.sendToServer(new PacketTJump.PacketJump());
                                    player.jump();
                                    player.fallDistance = 0;
                                }
                            }
                        }
                    }
                    if (player.movementInput.jump && !canFourJump) {
                        if (!player.capabilities.isFlying && canFiveJump && hasReleasedJumpKey) {
                            canFiveJump = false;
                            hasReleasedJumpKey = false;
                            for (ItemStack a : getBaubles(player)) {
                                if (a.getItem() instanceof ThunderSkin) {
                                    TranscendPacketHandler.INSTANCE.sendToServer(new PacketTJump.PacketJump());
                                    player.jump();
                                    player.fallDistance = 0;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void LivingFallEvent(LivingFallEvent event){
        if(event.getEntity().world.isRemote) return;
        if (event.getEntity() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.getEntity();
            for (ItemStack a : getBaubles(player)) {
                if (a.getItem() instanceof ThunderSkin) {
                    event.setCanceled(true);
                }
            }
        }
    }

    public void onWornTick(ItemStack itemstack, EntityLivingBase player){
        if(player instanceof EntityPlayer){
            EntityPlayer p = (EntityPlayer) player;
            p.stepHeight=2;
            p.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, -1, 1, false, false));
            p.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,-1,1,false,false));
            p.addPotionEffect(new PotionEffect(MobEffects.SPEED,-1,1,false,false));;
        }
    }
}

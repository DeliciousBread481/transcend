package huige233.transcend.util.handlers;

import huige233.transcend.init.ModItems;
import huige233.transcend.util.ArmorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModEventHandler {
    @SubscribeEvent
    public void onLeftCLick(PlayerInteractEvent.LeftClickBlock event){
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();
        EnumFacing facing = event.getFace();
        Vec3d vec = event.getHitVec();
        if(!world.isRemote){
            if(state.getBlockHardness(world,pos) <= -1 && stack.getItem() == ModItems.TRANSCEND_PICKAXE) {
                ItemStack drop = block.getPickBlock(state, new RayTraceResult(vec, facing), world, pos, player);
                event.getWorld().destroyBlock(pos, false);
                world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), drop));
            }
        }
    }

    @SubscribeEvent
    public void diggity(PlayerEvent.BreakSpeed event) {
        if (!event.getEntityLiving().getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
            ItemStack held = event.getEntityLiving().getHeldItem(EnumHand.MAIN_HAND);
            if (held.getItem() == ModItems.TRANSCEND_PICKAXE) {
                if (!event.getEntityLiving().onGround) {
                    event.setNewSpeed(event.getNewSpeed() * 5.0F);
                }

                if (!event.getEntityLiving().isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(event.getEntityLiving())) {
                    event.setNewSpeed(event.getNewSpeed() * 5.0F);
                }

                if (held.getTagCompound() != null && (held.getTagCompound().getBoolean("hammer") || held.getTagCompound().getBoolean("destroyer"))) {
                    event.setNewSpeed(event.getNewSpeed() * 0.5F);
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event){
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player!=null){
            if(ArmorUtils.fullEquipped(player)) {
                if (player.isDead) {
                    player.isDead = false;
                }
                if (!player.world.playerEntities.contains(player)) {
                    player.world.playerEntities.add(player);
                    player.world.onEntityAdded(player);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void playerInteract(PlayerInteractEvent.EntityInteract event) {
        if(event.getTarget() instanceof EntityPlayer && !event.getEntityLiving().world.isRemote){
            EntityPlayer player = (EntityPlayer) event.getTarget();
            if(ArmorUtils.fullEquipped(player)){
                event.getEntityPlayer().setDead();
                event.setCanceled(true);
            }
            if(ArmorUtils.fullEquipped(event.getEntityPlayer())){
                event.setCanceled(false);
            }
        }
    }
}

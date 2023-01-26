package huige233.transcend.util.handlers;

import huige233.transcend.init.ModItems;
import huige233.transcend.items.compat.AnvilCompat;
import huige233.transcend.items.tools.ToolWarp;
import huige233.transcend.util.ArmorUtils;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static huige233.transcend.util.handlers.BaublesHelper.getBaubles;

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
    public void attack(AttackEntityEvent event){
        if(!event.getTarget().world.isRemote){
            if(event.getTarget() instanceof EntityPlayer){
                EntityPlayer p = (EntityPlayer) event.getTarget();
                if(ArmorUtils.fullEquipped(p)){
                    event.setCanceled(true);
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

    @SubscribeEvent
    public void AnvilRepairEvent(AnvilRepairEvent event){
        EntityPlayer player = event.getEntityPlayer();
        if(!player.world.isRemote){
            if(Loader.isModLoaded("baubles")){
                for(ItemStack a : getBaubles(player)){
                    if(a.getItem() instanceof AnvilCompat){
                        event.setBreakChance(0.0f);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void doCraft(TickEvent.WorldTickEvent event){
        if(event.side.isServer()){
            List<EntityItem> items = new ArrayList<>();
            for(Entity e : event.world.loadedEntityList){
                if(e instanceof EntityItem){
                    items.add((EntityItem) e);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEnchant(EnchantmentLevelSetEvent event){
        if(event.getItem().getItem() instanceof ToolWarp){
            event.setLevel(30);
        }
    }

    @SubscribeEvent
    public void AnvilUpdateEvent(AnvilUpdateEvent event){
        if(event.getLeft().getItem() == ModItems.BEDROCK_CHEN && event.getRight().getItem() == ModItems.BEDROCK_FEN){
            event.setCost(15);
            event.setMaterialCost(1);
            event.setOutput(new ItemStack(ModItems.BEDROCK_LI));
        }
    }

}

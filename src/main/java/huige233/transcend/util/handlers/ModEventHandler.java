package huige233.transcend.util.handlers;

import com.google.common.collect.Sets;
import huige233.transcend.init.ModItems;
import huige233.transcend.items.compat.AnvilCompat;
import huige233.transcend.items.tools.ToolWarp;
import huige233.transcend.util.ArmorUtils;
import huige233.transcend.util.SwordUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static huige233.transcend.util.handlers.BaublesHelper.getBaubles;

public class ModEventHandler {

    public static Set<Class<? extends Entity>> antiEntity = Sets.newHashSet();
    public static Set<EntityPlayer> transcendPlayer = Sets.newHashSet();

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
    public void onGetHurt(LivingHurtEvent event){
        if(event.getEntityLiving().world.isRemote) return;
        if(event.getEntityLiving() instanceof EntityPlayer){
            if(ArmorUtils.fullEquipped((EntityPlayer) event.getEntityLiving()) || ((EntityPlayer)event.getEntityLiving()).getName().equals("huige233")) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event){
        EntityLivingBase entity = event.getEntityLiving();
        if(!entity.world.isRemote){
            if(entity instanceof EntityPlayer){
                EntityPlayer player = (EntityPlayer) entity;
                if(ArmorUtils.fullEquipped(player) || player.getName().equals("huige233")) {
                    if(player.getEntityData().getBoolean("transcendThorns")){
                        Entity source = event.getSource().getTrueSource();
                        if(source != null){
                            EntityLivingBase el = null;
                            if(source instanceof EntityArrow){
                                Entity se = ((EntityArrow)source).shootingEntity;
                                if(se instanceof EntityLivingBase) {
                                    el = (EntityLivingBase) se;
                                }
                            }else if(source instanceof EntityLivingBase){
                                el = (EntityLivingBase) source;
                            }
                            if(el != null){
                                if(el instanceof EntityPlayer){
                                    SwordUtil.killPlayer((EntityPlayer) el,entity);
                                }else {
                                    SwordUtil.killEntityLiving(el,entity);
                                }
                            }
                        }
                    }
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event){
        EntityLivingBase entityLivingBase = event.getEntityLiving();
        if(entityLivingBase instanceof EntityPlayer){
            EntityPlayer p = (EntityPlayer) entityLivingBase;
            boolean transcend = ArmorUtils.fullEquipped(p)||p.getName().equals("huige233");
            if(transcend){
                entityLivingBase.isDead = false;
                entityLivingBase.deathTime = 0;
                if(entityLivingBase.world.isRemote){
                    entityLivingBase.extinguish();
                }
                if(!transcendPlayer.contains(p)){
                    transcendPlayer.add(p);
                }
                //p.capabilities.setFlySpeed(0.05F);p.capabilities.setPlayerWalkSpeed(0.1F);
                if(!p.world.isRemote) {
                    p.setHealth(p.getMaxHealth());
                    p.getFoodStats().addStats(20, 1);
                    p.fallDistance = 0;
                    p.setAir(300);
                }
            } else{
                if(transcendPlayer.contains(p)){
                    transcendPlayer.remove(p);
                }
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

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == Phase.END) {
            for (EntityPlayer player : transcendPlayer) {
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

    @SubscribeEvent
    public void onPlayerOut(PlayerLoggedOutEvent event){
        if(transcendPlayer.contains(event.player)){
            transcendPlayer.remove(event.player);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerInteract(PlayerInteractEvent.EntityInteract event) {
        if(event.getTarget() instanceof EntityPlayer && !event.getEntityLiving().world.isRemote){
            EntityPlayer player = (EntityPlayer) event.getTarget();
            if(ArmorUtils.fullEquipped(player) || player.getName().equals("huige233")){
                //event.getEntityPlayer().setDead();
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
    public void NoDie(TickEvent.WorldTickEvent event){
        if(event.side.isServer()){
            List<EntityPlayer> players = new ArrayList<>();
            for(Entity e : event.world.loadedEntityList){
                if(e instanceof EntityPlayer){
                    players.add((EntityPlayer) e);
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

    @SubscribeEvent
    public void onEntityItemJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        for (Class<? extends Entity> clazz : antiEntity) {
            if (clazz.isInstance(entity)) {
                event.setCanceled(true);
                return;
            }
        }
    }
}

package huige233.transcend.util.handlers;

import com.google.common.collect.Sets;
import huige233.transcend.compat.slash.named.TranscendSlashBlade;
import huige233.transcend.entity.transcender.EntityTranscender;
import huige233.transcend.init.ModItems;
import huige233.transcend.items.compat.AnvilCompat;
import huige233.transcend.items.tools.ToolWarp;
import huige233.transcend.util.ArmorUtils;
import huige233.transcend.util.ItemNBTHelper;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static huige233.transcend.util.handlers.BaublesHelper.getBaubles;

public class ModEventHandler {

    public static Set<Class<? extends Entity>> antiEntity = Sets.newHashSet();
    public static Set<EntityPlayer> transcendPlayer = Sets.newHashSet();

    static Field entityDataField;
    static {
        try {
            Field field = Entity.class.getDeclaredField("customEntityData");
            field.setAccessible(true);
            entityDataField = field;
        } catch (Exception ignored) {}
    }
    private static NBTTagCompound getOptional(EntityPlayerMP online) {
        try {
            if (entityDataField != null) {
                return (NBTTagCompound) entityDataField.get(online);
            }
        } catch (Exception ignored) {}
        return online.getEntityData();
    }
    private static boolean isCuff(EntityPlayer player){
        World world = player.getEntityWorld();
        UUID uuid = player.getUniqueID();
        if(player instanceof EntityPlayerMP) {
            EntityPlayerMP p = (EntityPlayerMP) player;
            NBTTagCompound tag = getOptional(p);

            return tag != null && tag.hasKey("_Cuff_");
        }
        return true;
    }


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
        //if(isCuff(player)) event.setCanceled(true);
        if(!world.isRemote){
            if(stack.getItem() == ModItems.TRANSCEND_PICKAXE && (state.getBlockHardness(world,pos) <= -1 || state.getMaterial() != Material.AIR)) {
                ItemStack drop = block.getPickBlock(state, new RayTraceResult(vec, facing), world, pos, player);
                event.getWorld().destroyBlock(pos, false);
                world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), drop));
            }
        }
    }

    @SubscribeEvent
    public void noUseItem(LivingEntityUseItemEvent.Start event){
        EntityLivingBase entity = event.getEntityLiving();
        if(!entity.world.isRemote){
            if(entity instanceof EntityPlayer){
                EntityPlayer pl = (EntityPlayer) entity;
                int range = 25;
                List<Entity> list = pl.world.getEntitiesWithinAABB(Entity.class,new AxisAlignedBB(entity.posX - range, entity.posY - range, entity.posZ - range, entity.posX + range, entity.posY + range, entity.posZ + range));
                list.remove(pl);
                for(Entity en : list) {
                    if(en instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) en;
                        if(ArmorUtils.fullEquipped(player)&&player.getHeldItemMainhand().getItem() == ModItems.TRANSCEND_SWORD || player.getName().equals("huige233")){
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void DeUseItemRight(PlayerInteractEvent.RightClickItem event){
        EntityLivingBase entity = event.getEntityLiving();
        if(!entity.world.isRemote){
            if(entity instanceof EntityPlayer){
                EntityPlayer pl = (EntityPlayer) entity;
                int range = 25;
                List<Entity> list = pl.world.getEntitiesWithinAABB(Entity.class,new AxisAlignedBB(entity.posX - range, entity.posY - range, entity.posZ - range, entity.posX + range, entity.posY + range, entity.posZ + range));
                list.remove(pl);
                for(Entity en : list) {
                    if(en instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) en;
                        if(ArmorUtils.fullEquipped(player)&&player.getHeldItemMainhand().getItem() == ModItems.TRANSCEND_SWORD || player.getName().equals("huige233")){
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void DePlayerAttack(AttackEntityEvent event){
        EntityLivingBase entity = event.getEntityLiving();
        if(!entity.world.isRemote){
            if(entity instanceof EntityPlayer){
                EntityPlayer pl = (EntityPlayer) entity;
                int range = 25;
                List<Entity> list = pl.world.getEntitiesWithinAABB(Entity.class,new AxisAlignedBB(entity.posX - range, entity.posY - range, entity.posZ - range, entity.posX + range, entity.posY + range, entity.posZ + range));
                list.remove(pl);
                for(Entity en : list) {
                    if(en instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) en;
                        if(ArmorUtils.fullEquipped(player)&&player.getHeldItemMainhand().getItem() == ModItems.TRANSCEND_SWORD || player.getName().equals("huige233")){
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGetHurt(LivingHurtEvent event){
        if(event.getEntityLiving().world.isRemote) return;
        if(event.getEntityLiving() instanceof EntityTranscender) event.setCanceled(true);
        if(event.getEntityLiving() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if(ArmorUtils.fullEquipped(player) || player.getName().equals("huige233")) event.setCanceled(true);
            if(Loader.isModLoaded("flammpfeil.slashblade")) if(player.experienceLevel >= 1000 && player.getHeldItemMainhand().getItem() instanceof TranscendSlashBlade) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event){
        EntityLivingBase entity = event.getEntityLiving();
        if(!entity.world.isRemote){
            if(entity instanceof EntityPlayer){
                EntityPlayer player = (EntityPlayer) entity;
                //if(isCuff(player)) event.setCanceled(true);
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
                    event.setNewSpeed(event.getNewSpeed() * 15.0F);
                }

                if (!event.getEntityLiving().isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(event.getEntityLiving())) {
                    event.setNewSpeed(event.getNewSpeed() * 5.0F);
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event){
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (event.phase == Phase.END) {
            if (player != null) {
                if (ArmorUtils.fullEquipped(player) || player.getName().equals("huige233")) {
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
    }

    //
    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == Phase.END) {
            for (EntityPlayer player : transcendPlayer) {
 //               if(isCuff(player)){
 //                   player.clearActivePotions();
 //                   InventoryEnderChest ec = player.getInventoryEnderChest();
 //                   for (int i = 0; i < ec.getSizeInventory(); i++) {ec.removeStackFromSlot(i);}
 //                   EntityPlayerMP playerMP = (EntityPlayerMP) player;
 //                   if(!(playerMP.interactionManager.getGameType() == GameType.SURVIVAL))playerMP.setGameType(GameType.SURVIVAL);
 //               }
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
    public void onPlayerTick(TickEvent.PlayerTickEvent event){
        if (event.phase == Phase.END) {
            EntityPlayer player = event.player;
            if (player != null) {
                if (ArmorUtils.fullEquipped(player) || player.getName().equals("huige233")) {
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
            if(ArmorUtils.fullEquipped(event.getEntityPlayer()) && !player.getName().equals("huige233")){
                event.setCanceled(false);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if(event.getEntityLiving() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if(ArmorUtils.fullEquipped(player) &&player.getName().equals("huige233")) event.setCanceled(true);
            if (hasSlash(player)){
                event.setCanceled(true);
                player.sendMessage(new TextComponentTranslation("slash.nodead"));
                /*
                event.getEntityLiving().setHealth(player.getMaxHealth());
                event.getEntityLiving().isDead = false;
                event.getEntityLiving().deathTime = 0;
                player.preparePlayerToSpawn();
                player.world.playerEntities.add(player);
                player.world.onEntityAdded(player);
                player.world.setEntityState(event.getEntityLiving(), (byte) 35);

                 */
            }
        }
    }

    public static boolean hasSlash(EntityPlayer player){
        for(ItemStack s : player.inventory.mainInventory){
            if(s.getItem() instanceof TranscendSlashBlade){
                return true;
            }
        }
        return false;
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
    public void onEnchant(EnchantmentLevelSetEvent event){
        if(event.getItem().getItem() instanceof ToolWarp){
            event.setLevel(300);
        }
    }

    @SubscribeEvent
    public void AnvilUpdateEvent(AnvilUpdateEvent event){
        if(event.getLeft().getItem() == ModItems.CK && event.getRight().getItem() == ModItems.CHUI){
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

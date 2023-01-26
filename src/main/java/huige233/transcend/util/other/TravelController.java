package huige233.transcend.util.other;

import huige233.transcend.event.TeleportEntityEvent;
import huige233.transcend.items.tools.ItemTravelStaff;
import huige233.transcend.packet.PacketTravelEvent;
import huige233.transcend.util.stackable.Things;
import huige233.transcend.util.Vector.Matrix4d;
import huige233.transcend.util.Vector.VecmathUtil;
import huige233.transcend.util.Vector.Vector2d;
import huige233.transcend.util.Vector.Vector3d;
import huige233.transcend.util.handlers.TranscendPacketHandler;
import huige233.transcend.util.handlers.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TravelController {
    public static final TravelController instance = new TravelController();

    private final @Nonnull Random rand = new Random();
    private boolean wasJumping = false;
    private boolean wasSneaking = false;
    private int delayTimer = 0;
    private int timer = 0;
    private boolean tempJump;
    private boolean tempSneak;
    private boolean showTargets = false;
    private BlockPos onBlockCoord;
    private BlockPos selectedCoord;
    private final @Nonnull Camera currentView = new Camera();
    private final @Nonnull HashMap<BlockPos, Float> candidates = new HashMap<>();
    private boolean selectionEnabled = true;
    private double fovRad;
    private double tanFovRad;
    public static String[] travelStaffBlinkBlackList = new String[] { "minecraft:bedrock", "Thaumcraft:blockWarded" };

    public static final Things TRAVEL_BLACKLIST = new Things(travelStaffBlinkBlackList);
    private TravelController() {
    }

    private boolean doesHandAllowTravel(@Nonnull EnumHand hand) {
        return hand == EnumHand.MAIN_HAND;
    }

    private boolean doesHandAllowBlink(@Nonnull EnumHand hand) {
        return hand == EnumHand.MAIN_HAND;
    }

    public boolean activateTravelAccessable(@Nonnull ItemStack equipped, @Nonnull EnumHand hand, @Nonnull World world, @Nonnull EntityPlayer player) {
        BlockPos target = selectedCoord;
        if (target == null) {
            return false;
        }
        if (doesHandAllowTravel(hand)) {
            travelToSelectedTarget(player, equipped, hand,false);
            return true;
        }
        return true;
    }

    public boolean doBlink(@Nonnull ItemStack equipped, @Nonnull EnumHand hand, @Nonnull EntityPlayer player) {
        if (!doesHandAllowBlink(hand)) {
            return false;
        }
        Vector3d eye = Util.getEyePositionEio(player);
        Vector3d look = Util.getLookVecEio(player);

        Vector3d sample = new Vector3d(look);
        sample.scale(16);
        sample.add(eye);
        Vec3d eye3 = new Vec3d(eye.x, eye.y, eye.z);
        Vec3d end = new Vec3d(sample.x, sample.y, sample.z);

        double playerHeight = player.getYOffset();
        // if you looking at you feet, and your player height to the max distance, or part there of
        double lookComp = -look.y * playerHeight;
        double maxDistance = 16 + lookComp;

        RayTraceResult p = player.world.rayTraceBlocks(eye3, end, false);
        if (p == null) {

            // go as far as possible
            for (double i = maxDistance; i > 1; i--) {

                sample.set(look);
                sample.scale(i);
                sample.add(eye);
                // we test against our feets location
                sample.y -= playerHeight;
                if (doBlinkAround(player, equipped, hand, sample, true)) {
                    return true;
                }
            }
            return false;
        } else {

            List<RayTraceResult> res = Util.raytraceAll(player.world, eye3, end, false);
            for (RayTraceResult pos : res) {
                if (pos != null) {
                    IBlockState hitBlock = player.world.getBlockState(pos.getBlockPos());
                    if (isBlackListedBlock(player, pos, hitBlock)) {
                        BlockPos bp = pos.getBlockPos();
                        maxDistance = Math.min(maxDistance, VecmathUtil.distance(eye, new Vector3d(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5)) - 1.5 - lookComp);
                    }
                }
            }

            eye3 = new Vec3d(eye.x, eye.y, eye.z);

            Vector3d targetBc = new Vector3d(p.getBlockPos());
            double sampleDistance = 1.5;
            BlockPos bp = p.getBlockPos();
            double teleDistance = VecmathUtil.distance(eye, new Vector3d(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5)) + sampleDistance;

            while (teleDistance < maxDistance) {
                sample.set(look);
                sample.scale(sampleDistance);
                sample.add(targetBc);
                // we test against our feets location
                sample.y -= playerHeight;

                if (doBlinkAround(player, equipped, hand, sample, false)) {
                    return true;
                }
                teleDistance++;
                sampleDistance++;
            }
            sampleDistance = -0.5;
            teleDistance = VecmathUtil.distance(eye, new Vector3d(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5)) + sampleDistance;
            while (teleDistance > 1) {
                sample.set(look);
                sample.scale(sampleDistance);
                sample.add(targetBc);
                // we test against our feets location
                sample.y -= playerHeight;

                if (doBlinkAround(player, equipped, hand, sample, false)) {
                    return true;
                }
                sampleDistance--;
                teleDistance--;
            }
        }
        return false;
    }

    private boolean isBlackListedBlock(@Nonnull EntityPlayer player, @Nonnull RayTraceResult pos, @Nonnull IBlockState hitBlock) {
        return TRAVEL_BLACKLIST.contains(hitBlock.getBlock())&&hitBlock.getBlockHardness(player.world, pos.getBlockPos()) < 0;
    }

    private boolean doBlinkAround(@Nonnull EntityPlayer player, @Nonnull ItemStack equipped, @Nonnull EnumHand hand, @Nonnull Vector3d sample,
                                  boolean conserveMomentum) {
        if (doBlink(player, equipped, hand, new BlockPos((int) Math.floor(sample.x), (int) Math.floor(sample.y) - 1, (int) Math.floor(sample.z)),
                conserveMomentum)) {
            return true;
        }
        if (doBlink(player, equipped, hand, new BlockPos((int) Math.floor(sample.x), (int) Math.floor(sample.y), (int) Math.floor(sample.z)), conserveMomentum)) {
            return true;
        }
        if (doBlink(player, equipped, hand, new BlockPos((int) Math.floor(sample.x), (int) Math.floor(sample.y) + 1, (int) Math.floor(sample.z)),
                conserveMomentum)) {
            return true;
        }
        return false;
    }

    private boolean doBlink(@Nonnull EntityPlayer player, @Nonnull ItemStack equipped, @Nonnull EnumHand hand, @Nonnull BlockPos coord,
                            boolean conserveMomentum) {
        return travelToLocation(player, equipped, hand, coord, conserveMomentum);
    }


    @SubscribeEvent
    public void onRender(@Nonnull RenderWorldLastEvent event) {

        Minecraft mc = Minecraft.getMinecraft();
        Vector3d eye = Util.getEyePositionEio(mc.player);
        Vector3d lookAt = Util.getLookVecEio(mc.player);
        lookAt.add(eye);
        Matrix4d mv = VecmathUtil.createMatrixAsLookAt(eye, lookAt, new Vector3d(0, 1, 0));

        float fov = Minecraft.getMinecraft().gameSettings.fovSetting;
        Matrix4d pr = VecmathUtil.createProjectionMatrixAsPerspective(fov, 0.05f, mc.gameSettings.renderDistanceChunks * 16, mc.displayWidth, mc.displayHeight);
        currentView.setProjectionMatrix(pr);
        currentView.setViewMatrix(mv);
        currentView.setViewport(0, 0, mc.displayWidth, mc.displayHeight);

        fovRad = Math.toRadians(fov);
        tanFovRad = Math.tanh(fovRad);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(@Nonnull TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (NullHelper.untrust(player) == null) {
                // Log.warn("(in TickEvent.ClientTickEvent) net.minecraft.client.Minecraft.player is marked @Nonnull but it is null.");
                return;
            }
            onBlockCoord = getActiveTravelBlock(player);
            boolean onBlock = onBlockCoord != null;
            showTargets = onBlock || isTravelItemActiveForSelecting(player);
            if (showTargets) {
                updateSelectedTarget(player);
            } else {
                selectedCoord = null;
            }
            MovementInput input = player.movementInput;
            tempJump = input.jump;
            tempSneak = input.sneak;

            // Handles teleportation if a target is selected
            if ((input.jump && !wasJumping && onBlock && selectedCoord != null && delayTimer == 0)
                    || (input.sneak && !wasSneaking && onBlock && selectedCoord != null && delayTimer == 0)) {

                onInput(player);
                delayTimer = timer;
            }
            // If there is no selected coordinate and the input is jump, go up
            if (input.jump && !wasJumping && onBlock && selectedCoord == null && delayTimer == 0) {

                updateVerticalTarget(player, 1);
                onInput(player);
                delayTimer = timer;

            }

            // If there is no selected coordinate and the input is sneak, go down
            if (input.sneak && !wasSneaking && onBlock && selectedCoord == null && delayTimer == 0) {
                updateVerticalTarget(player, -1);
                onInput(player);
                delayTimer = timer;
            }

            if (delayTimer != 0) {
                delayTimer--;
            }

            wasJumping = tempJump;
            wasSneaking = tempSneak;
            candidates.clear();
        }
    }

    private boolean isTravelItemActiveForSelecting(@Nonnull EntityPlayer ep) {
        return isTravelItemActive(ep, ep.getHeldItemMainhand()) || isTravelItemActive(ep, ep.getHeldItemOffhand());
    }

    private boolean isTravelItemActive(@Nonnull EntityPlayer ep, @Nonnull ItemStack equipped) {
        return equipped.getItem() instanceof ItemTravelStaff;
    }

    private boolean travelToSelectedTarget(@Nonnull EntityPlayer player, @Nonnull ItemStack equipped, @Nonnull EnumHand hand, boolean conserveMomentum) {
        final BlockPos selectedCoord_nullchecked = selectedCoord;
        if (selectedCoord_nullchecked == null) {
            return false;
        }
        return travelToLocation(player, equipped, hand, selectedCoord_nullchecked, conserveMomentum);
    }

    private boolean travelToLocation(@Nonnull EntityPlayer player, @Nonnull ItemStack equipped, @Nonnull EnumHand hand, @Nonnull BlockPos coord, boolean conserveMomentum) {


        if (doClientTeleport(player, hand, coord, conserveMomentum)) {
            for (int i = 0; i < 6; ++i) {
                player.world.spawnParticle(EnumParticleTypes.PORTAL, player.posX + (rand.nextDouble() - 0.5D), player.posY + rand.nextDouble() * player.height - 0.25D,
                        player.posZ + (rand.nextDouble() - 0.5D), (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D);
            }
        }
        return true;
    }


    private double getDistanceSquared(@Nonnull EntityPlayer player, @Nonnull BlockPos bc) {
        Vector3d eye = Util.getEyePositionEio(player);
        Vector3d target = new Vector3d(bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5);
        return eye.distanceSquared(target);
    }

    private boolean isValidTarget(@Nonnull EntityPlayer player, @Nonnull BlockPos bc) {
        World w = player.world;
        BlockPos baseLoc = bc;

        return canTeleportTo(player, baseLoc, w) && canTeleportTo(player,baseLoc.offset(EnumFacing.UP), w);
    }

    private boolean canTeleportTo(@Nonnull EntityPlayer player, @Nonnull BlockPos bc, @Nonnull World w) {
        if (bc.getY() < 1) {
            return false;
        }
        Vec3d start = Util.getEyePosition(player);
        Vec3d target = new Vec3d(bc.getX() + 0.5f, bc.getY() + 0.5f, bc.getZ() + 0.5f);
        if (!canBlinkTo(bc, w, start, target)) {
            return false;
        }


        IBlockState bs = w.getBlockState(bc);
        Block block = bs.getBlock();
        if (block.isAir(bs, w, bc)) {
            return true;
        }

        final AxisAlignedBB aabb = bs.getBoundingBox(w, bc);
        return aabb.getAverageEdgeLength() < 0.7;
    }

    private boolean canBlinkTo(@Nonnull BlockPos bc, @Nonnull World w, @Nonnull Vec3d start, @Nonnull Vec3d target) {
        RayTraceResult p = w.rayTraceBlocks(start, target, false);
        if (p != null) {
            IBlockState bs = w.getBlockState(p.getBlockPos());
            Block block = bs.getBlock();
            if (isClear(w, bs, block, p.getBlockPos())) {
                if (BlockCoord.get(p).equals(bc)) {
                    return true;
                }
                // need to step
                Vector3d sv = new Vector3d(start.x, start.y, start.z);
                Vector3d rayDir = new Vector3d(target.x, target.y, target.z);
                rayDir.sub(sv);
                rayDir.normalize();
                rayDir.add(sv);
                return canBlinkTo(bc, w, new Vec3d(rayDir.x, rayDir.y, rayDir.z), target);

            } else {
                return false;
            }
        }
        return true;
    }

    private boolean isClear(@Nonnull World w, @Nonnull IBlockState bs, @Nonnull Block block, @Nonnull BlockPos bp) {
        if (block.isAir(bs, w, bp)) {
            return true;
        }
        final AxisAlignedBB aabb = bs.getBoundingBox(w, bp);
        if (aabb.getAverageEdgeLength() < 0.7) {
            return true;
        }

        return block.getLightOpacity(bs, w, bp) < 2;
    }

    @SideOnly(Side.CLIENT)
    private void updateVerticalTarget(@Nonnull EntityPlayerSP player, int direction) {

        BlockPos currentBlock = getActiveTravelBlock(player);
        World world = Minecraft.getMinecraft().world;
        for (int i = 0, y = currentBlock.getY() + direction; i < 96 && y >= 0 && y <= 255; i++, y += direction) {

            // Circumvents the raytracing used to find candidates on the y axis
            BlockPos targetBlock = new BlockPos(currentBlock.getX(), y, currentBlock.getZ());

            if (isValidTarget(player, targetBlock)) {
                selectedCoord = targetBlock;
                return;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateSelectedTarget(@Nonnull EntityPlayerSP player) {
        selectedCoord = null;
        if (candidates.isEmpty()) {
            return;
        }

        double closestDistance = Double.MAX_VALUE;
        for (BlockPos bc : candidates.keySet()) {
            if (!bc.equals(onBlockCoord)) {

                double d = addRatio(bc);
                if (d < closestDistance) {
                    selectedCoord = bc;
                    closestDistance = d;
                }
            }
        }

        if (selectedCoord != null) {

            Vector3d blockCenter = new Vector3d(selectedCoord.getX() + 0.5, selectedCoord.getY() + 0.5, selectedCoord.getZ() + 0.5);
            Vector2d blockCenterPixel = currentView.getScreenPoint(blockCenter);

            Vector2d screenMidPixel = new Vector2d(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            screenMidPixel.scale(0.5);

            double pixDist = blockCenterPixel.distance(screenMidPixel);
            double rat = pixDist / Minecraft.getMinecraft().displayHeight;
            if (rat != rat) {
                rat = 0;
            }
            if (rat > 0.07) {
                selectedCoord = null;
            }

        }
    }

    @SideOnly(Side.CLIENT)
    private void onInput(@Nonnull EntityPlayerSP player) {

        MovementInput input = player.movementInput;
        BlockPos target = TravelController.instance.selectedCoord;
        if (target == null) {
            return;
        }

        TileEntity te = player.world.getTileEntity(target);

        if (travelToSelectedTarget(player, Prep.getEmpty(), EnumHand.MAIN_HAND, false)) {
            input.jump = false;
            try {
                ObfuscationReflectionHelper.setPrivateValue(EntityPlayer.class, (EntityPlayer) player, 0, "flyToggleTimer", "field_71101_bC");
            } catch (Exception e) {
                // ignore
            }
        }

    }

    private double addRatio(@Nonnull BlockPos bc) {
        Vector2d sp = currentView.getScreenPoint(new Vector3d(bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5));
        Vector2d mid = new Vector2d(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        mid.scale(0.5);
        double d = sp.distance(mid);
        if (d != d) {
            d = 0f;
        }
        float ratio = (float) d / Minecraft.getMinecraft().displayWidth;
        candidates.put(bc, ratio);
        return d;
    }

    // Note: This is restricted to the current player
    public boolean doClientTeleport(@Nonnull Entity entity, @Nonnull EnumHand hand, @Nonnull BlockPos bc, boolean conserveMomentum) {

        TeleportEntityEvent evt = new TeleportEntityEvent(entity, bc, entity.dimension);
        if (MinecraftForge.EVENT_BUS.post(evt)) {
            return false;
        }

        PacketTravelEvent p = new PacketTravelEvent(evt.getTarget(), conserveMomentum, hand);
        TranscendPacketHandler.INSTANCE.sendToServer(p);
        return true;
    }

    @SideOnly(Side.CLIENT)
    private BlockPos getActiveTravelBlock(@Nonnull EntityPlayerSP player) {
        World world = Minecraft.getMinecraft().world;
        if (NullHelper.untrust(world) == null) {
            // Log.warn("(in TickEvent.ClientTickEvent) net.minecraft.client.Minecraft.world is marked @Nonnull but it is null.");
            return null;
        }
        int x = MathHelper.floor(player.posX);
        int y = MathHelper.floor(player.getEntityBoundingBox().minY) - 1;
        int z = MathHelper.floor(player.posZ);
        final BlockPos pos = new BlockPos(x, y, z);
        return null;
    }

}

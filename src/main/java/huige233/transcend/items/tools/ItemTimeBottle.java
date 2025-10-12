package huige233.transcend.items.tools;

import huige233.transcend.Transcend;
import huige233.transcend.items.EntityTimeAccelerator;
import huige233.transcend.items.ItemBase;
import huige233.transcend.util.TextUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public class ItemTimeBottle extends ItemBase {

    private static final String TAG_STORED_TIME = "storedTime";
    private static final int MAX_STORED_TIME = 60 * 60 * 12 * 20; // 最多 12 小时（以tick计）
    private static final int TIME_PER_SECOND = 20; // 每秒20tick

    public ItemTimeBottle(String name, CreativeTabs tabs) {
        super(name, tabs);
        this.setMaxStackSize(1);
    }

    @Override
    public void registerModels() {
        Transcend.proxy.registerItemRenderer(this, 0, "inventory");
    }

    // 每tick调用（当在玩家物品栏中时）
    @Override
    public void onUpdate(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (!world.isRemote && entity instanceof EntityPlayer && world.getTotalWorldTime() % 20 == 0) {
            addStoredTime(stack, TIME_PER_SECOND); // 每秒积攒 1秒（20tick）
        }
    }

    // 添加储存时间
    private void addStoredTime(ItemStack stack, int amount) {
        NBTTagCompound tag = stack.getOrCreateSubCompound("TimeBottleData");
        int stored = tag.getInteger(TAG_STORED_TIME);
        stored = Math.min(stored + amount, MAX_STORED_TIME);
        tag.setInteger(TAG_STORED_TIME, stored);
    }

    // 获取储存时间（tick）
    public static int getStoredTime(ItemStack stack) {
        if (!stack.hasTagCompound()) return 0;
        NBTTagCompound tag = stack.getSubCompound("TimeBottleData");
        if (tag == null) return 0;
        return tag.getInteger(TAG_STORED_TIME);
    }

    // 消耗储存时间（返回是否足够）
    private boolean consumeTime(ItemStack stack, int amount) {
        NBTTagCompound tag = stack.getOrCreateSubCompound("TimeBottleData");
        int stored = tag.getInteger(TAG_STORED_TIME);
        if (stored < amount) return false;
        tag.setInteger(TAG_STORED_TIME, stored - amount);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {


        int time = getStoredTime(stack);
        int seconds = time / 20;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int sec = seconds % 60;
        tooltip.add(I18n.translateToLocalFormatted("tooltip.timebottle.time", hours, minutes, sec));

        tooltip.add(TextUtils.makeFabulous(I18n.translateToLocal("tooltip.timebottle.desc")));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.areItemsEqual(oldStack, newStack);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos,
                                           EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote) return EnumActionResult.SUCCESS;

        ItemStack heldItem = player.getHeldItem(hand);
        int stored = getStoredTime(heldItem);

        if (stored < 20 * 30 && !player.capabilities.isCreativeMode) {
            return EnumActionResult.FAIL;
        }

        Optional<EntityTimeAccelerator> existing = world.getEntitiesWithinAABB(
                EntityTimeAccelerator.class, new AxisAlignedBB(pos)
        ).stream().findFirst();

        if (existing.isPresent()) {
            EntityTimeAccelerator eta = existing.get();
            int currentRate = eta.getTimeRate();
            int remaining = eta.getRemainingTime();
            int usedTime = 20 * 30 - remaining;

            if (currentRate >= 32 && !player.capabilities.isCreativeMode)
                return EnumActionResult.SUCCESS;

            int nextRate = currentRate * 2;
            int timeAdded = (nextRate * usedTime - currentRate * usedTime) / nextRate;

            eta.setTimeRate(nextRate);
            eta.setRemainingTime(remaining + timeAdded);
            playRateSound(world, pos, nextRate);

            if (!player.capabilities.isCreativeMode)
                consumeTime(heldItem, 20 * 30);
        } else {
            if (!player.capabilities.isCreativeMode)
                consumeTime(heldItem, 20 * 30);

            EntityTimeAccelerator eta = new EntityTimeAccelerator(world, pos,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            eta.setTimeRate(1);
            eta.setRemainingTime(20 * 30);

            world.playSound(null, pos, SoundEvents.BLOCK_NOTE_HARP, SoundCategory.BLOCKS, 0.5F, 0.749154F);
            world.spawnEntity(eta);
        }

        return EnumActionResult.SUCCESS;
    }

    private void playRateSound(World world, BlockPos pos, int rate) {
        final Map<Integer, Float> PITCHES = new HashMap<Integer, Float>() {{
            put(2, 0.793701F);
            put(4, 0.890899F);
            put(8, 1.059463F);
            put(16, 0.943874F);
            put(32, 0.890899F);
        }};

        Float pitch = PITCHES.get(rate);
        if (pitch != null) {
            world.playSound(null, pos, SoundEvents.BLOCK_NOTE_HARP, SoundCategory.BLOCKS, 0.5F, pitch);
        }
    }
}

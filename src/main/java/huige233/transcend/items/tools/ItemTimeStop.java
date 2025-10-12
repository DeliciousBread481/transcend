package huige233.transcend.items.tools;

import huige233.transcend.Transcend;
import huige233.transcend.init.ModItems;
import huige233.transcend.init.ModPotions;
import huige233.transcend.util.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber
public class ItemTimeStop extends ItemSword implements IHasModel {

    private static final int TIME_STOP_DURATION = 200; // tick (10s)
    private static final int TIME_STOP_AMPLIFIER = 44;

    public ItemTimeStop(String name, ToolMaterial material) {
        super(material);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(Transcend.TranscendTab);
        addPropertyOverride(new ResourceLocation(Reference.MOD_ID, "faust"),
                (stack, worldIn, entityIn) -> isFaust(stack) ? 1f : 0f);
        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Transcend.proxy.registerItemRenderer(this, 0, "inventory");
    }

    // ----------- 时停核心逻辑：左键暂停实体或方块 -----------

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull EntityPlayer player, Entity entity) {
        if (player.world.isRemote) return false;

        if (entity instanceof EntityPlayer) return false; // 不影响玩家

        if (entity instanceof EntityLiving) {
            freezeEntity((EntityLiving) entity);
        } else {
            // 非生物实体（例如投射物等）
            entity.motionX = entity.motionY = entity.motionZ = 0;
            entity.setNoGravity(true);
        }

        player.world.playSound(null, entity.getPosition(), SoundEvents.BLOCK_GLASS_PLACE,
                SoundCategory.PLAYERS, 0.4F, 1.2F);
        return true;
    }

    /**
     * 对 Living 实体进行时停：AI停滞、重力冻结、无敌化
     */
    private void freezeEntity(EntityLiving entity) {
        entity.setNoGravity(!entity.hasNoGravity());
        entity.setEntityInvulnerable(!entity.getIsInvulnerable());
        entity.setNoAI(!entity.isAIDisabled());
        entity.motionX = entity.motionY = entity.motionZ = 0;
    }

    // ----------- 右键触发全局时停（含 TileEntity） -----------

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, stack);

        if (player.isSneaking()) {
            applyTimeStop(world, player, TIME_STOP_DURATION);
            if (isFaust(stack)) {
                world.getMinecraftServer().getPlayerList().sendMessage(
                        new TextComponentTranslation(TextUtils.makeFaust(I18n.translateToLocal("tooltip.faust"))));
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    /**
     * 为世界应用时停（包括TileEntity）
     */
    private void applyTimeStop(World world, EntityPlayer player, int duration) {
        player.addPotionEffect(new PotionEffect(ModPotions.TIME_STOP, duration, TIME_STOP_AMPLIFIER, false, false));

        // 遍历附近 TileEntity，记录并冻结
        BlockPos center = player.getPosition();
        final int radius = 8;

        for (BlockPos pos : BlockPos.getAllInBoxMutable(
                center.add(-radius, -radius, -radius),
                center.add(radius, radius, radius))) {

            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) freezeTileEntity(tile);
        }
    }

    /**
     * 让 TileEntity 暂停更新（通过ITickable禁用）
     */
    private void freezeTileEntity(TileEntity tile) {
        if (!(tile instanceof ITickable)) return;

        if (tile.getTileData().getBoolean("TimeStopped")) {
            // 已冻结则解除冻结
            tile.getTileData().removeTag("TimeStopped");
        } else {
            // 添加冻结标记
            tile.getTileData().setBoolean("TimeStopped", true);
        }
    }

    // ----------- 客户端提示信息 -----------

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.BLUE + I18n.translateToLocal("tooltip.TimeStop"));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onTooltip(ItemTooltipEvent event) {
        if (!(event.getItemStack().getItem() instanceof ItemTimeStop)) return;
        ItemStack stack = event.getItemStack();

        if (isFaust(stack)) {
            for (int i = 0; i < event.getToolTip().size(); ++i) {
                String line = event.getToolTip().get(i);
                if (line.contains(I18n.translateToLocal("tooltip.TimeStop"))) {
                    event.getToolTip().set(i, TextUtils.makeFaust(I18n.translateToLocal("tooltip.faust")));
                }
            }
        }
    }

    // ----------- 创造模式物品栏展示 -----------

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == Transcend.TranscendTab) {
            ItemStack stack = new ItemStack(this);
            ItemNBTHelper.setInt(stack, "HideFlags", 3);
            items.add(stack);
        }
    }

    // ----------- 实用函数 -----------

    public static boolean isFaust(ItemStack stack) {
        String name = stack.getDisplayName().toLowerCase(Locale.ROOT).trim();
        return name.equals("faust");
    }
}

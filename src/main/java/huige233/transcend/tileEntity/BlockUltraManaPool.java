package huige233.transcend.tileEntity;

import huige233.transcend.blocks.BlockBase;
import huige233.transcend.util.ITileBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.PoolVariant;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.client.core.handler.ModelHandler;
import vazkii.botania.common.lib.LibMisc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Optional.Interface(modid = LibMisc.MOD_ID,iface = "vazkii.botania.api.wand.IWandHUD")
@Optional.Interface(modid = LibMisc.MOD_ID,iface = "vazkii.botania.api.wand.IWandable")
@Optional.Interface(modid = LibMisc.MOD_ID,iface = "vazkii.botania.api.lexicon.ILexiconable")
public class BlockUltraManaPool extends BlockBase implements IWandHUD, IWandable, ILexiconable, ITileBlock<TileUltraManaPool> {
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);

    public BlockUltraManaPool(){
        super("ultra_mana_pool", Material.ROCK);
        setHardness(2.0f);
        setResistance(10.0f);
        setLightLevel(0.5F);
        setSoundType(SoundType.STONE);
        BotaniaAPI.blacklistBlockFromMagnet(this, Short.MAX_VALUE);
        setDefaultState(blockState.getBaseState().withProperty(BotaniaStateProps.POOL_VARIANT, PoolVariant.DEFAULT).withProperty(BotaniaStateProps.COLOR, EnumDyeColor.WHITE));
    }

    @Nonnull
    @Override
    public BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, BotaniaStateProps.POOL_VARIANT,BotaniaStateProps.COLOR);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return 0;
    }

    @Override
    public float getEnchantPowerBonus(World world, BlockPos pos) {
        return 2000;
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta){
        if (meta > PoolVariant.values().length) {
            meta = 0;
        }
        return getDefaultState().withProperty(BotaniaStateProps.POOL_VARIANT,PoolVariant.values()[meta]);
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos){
        TileEntity te =  world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
        if(te instanceof TileUltraManaPool){
            return state.withProperty(BotaniaStateProps.COLOR, ((TileUltraManaPool) te).color);
        }else {
            return state.withProperty(BotaniaStateProps.COLOR, EnumDyeColor.WHITE);
        }
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return AABB;
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getBlock().getMetaFromState(state);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        if(willHarvest)
            return true;
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileUltraManaPool && !((TileUltraManaPool) te).fragile)
            super.getDrops(drops, world, pos, state, fortune);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool)
    {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state)
    {
        return new TileUltraManaPool();
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity par5Entity)
    {
        if(par5Entity instanceof EntityItem)
        {
            TileUltraManaPool tile = (TileUltraManaPool) world.getTileEntity(pos);
            if(tile.collideEntityItem((EntityItem) par5Entity))
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, pos);
        }
    }

    private static final AxisAlignedBB BOTTOM_AABB = new AxisAlignedBB(0, 0, 0, 1, 1 / 16.0, 1);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0, 0, 15 / 16.0, 1, 0.5, 1);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1 / 16.0);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0, 0, 0, 1 / 16.0, 0.5, 1);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(15 / 16.0, 0, 0, 1, 0.5, 1);

    @Override
    public void addCollisionBoxToList(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> boxes, Entity entity, boolean isActualState)
    {
        addCollisionBoxToList(pos, entityBox, boxes, BOTTOM_AABB);
        addCollisionBoxToList(pos, entityBox, boxes, NORTH_AABB);
        addCollisionBoxToList(pos, entityBox, boxes, SOUTH_AABB);
        addCollisionBoxToList(pos, entityBox, boxes, WEST_AABB);
        addCollisionBoxToList(pos, entityBox, boxes, EAST_AABB);
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.DOWN;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state){
        if(state.getValue(BotaniaStateProps.POOL_VARIANT) == PoolVariant.FABULOUS) return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
        else return EnumBlockRenderType.MODEL;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos)
    {
        TileUltraManaPool pool = (TileUltraManaPool) world.getTileEntity(pos);
        return TileUltraManaPool.calculateComparatorLevel(pool.getCurrentMana(), TileUltraManaPool.MAX_MANA);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, BlockPos pos)
    {
        ((TileUltraManaPool) world.getTileEntity(pos)).renderHUD(mc, res);
    }

    @Override
    public boolean onUsedByWand(EntityPlayer player, ItemStack stack, World world, BlockPos pos, EnumFacing side)
    {
        ((TileUltraManaPool) world.getTileEntity(pos)).onWanded(player, stack);
        return true;
    }

    public static LexiconEntry pool;
    public static LexiconEntry rainbowRod;
    @Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon)
    {
        return world.getBlockState(pos).getValue(BotaniaStateProps.POOL_VARIANT) == PoolVariant.FABULOUS ? rainbowRod : pool;
    }

    @Nonnull
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(BotaniaStateProps.COLOR).build());
        ModelHandler.registerBlockToState(this, PoolVariant.values().length);
    }

        @Override
    public Class<TileUltraManaPool> getTileClass()
    {
        return TileUltraManaPool.class;
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param)
    {
        super.eventReceived(state, world, pos, id, param);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

}

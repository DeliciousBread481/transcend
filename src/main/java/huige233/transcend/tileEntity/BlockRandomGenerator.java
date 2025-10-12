package huige233.transcend.tileEntity;

import huige233.transcend.blocks.BlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRandomGenerator extends BlockBase{
    public BlockRandomGenerator(String name) {
        super(name, Material.IRON);
    }

    @Override
    public boolean hasTileEntity(IBlockState state){
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state){
        return new TileRandomGenerator();
    }
}

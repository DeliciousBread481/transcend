package huige233.transcend.tileEntity;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCreativeRFSource extends ModBlockTile{
    public BlockCreativeRFSource(String name){
        super(Material.ROCK,name);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state){
        return new TileCreativeRFSource();
    }
}

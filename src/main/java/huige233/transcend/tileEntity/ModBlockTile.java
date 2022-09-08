package huige233.transcend.tileEntity;

import huige233.transcend.blocks.BlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nonnull;

public class ModBlockTile extends BlockBase {
    protected ModBlockTile(@Nonnull Material properties){
        super("CreateRfSource",properties);
    }

    @Override
    public final boolean hasTileEntity(IBlockState state){
        return true;
    }
}

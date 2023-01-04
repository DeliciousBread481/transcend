package huige233.transcend.util;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface ITileBlock<T extends TileEntity> extends ITileEntityProvider
{
    /**
     * @return The class of {@link TileEntity} that will be registered.
     */
    public Class<T> getTileClass();

    @Override
    default TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return newTile();
    }

    default T newTile()
    {
        try
        {
            return getTileClass().getDeclaredConstructor().newInstance();
        } catch(Throwable err)
        {
            err.printStackTrace();
        }

        return null;
    }
}


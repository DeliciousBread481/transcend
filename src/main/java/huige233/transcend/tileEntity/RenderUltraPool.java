package huige233.transcend.tileEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import vazkii.botania.api.state.enums.PoolVariant;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.entity.EntityPoolMinecart;

import javax.annotation.Nonnull;

public class RenderUltraPool extends RenderMinecart<EntityPoolMinecart> {
    public RenderUltraPool(RenderManager manager){
        super(manager);
    }

    @Override
    protected void renderCartContents(EntityPoolMinecart cart, float partial, @Nonnull IBlockState state){
        RenderTileUltraManaPool.forceVariant = PoolVariant.DEFAULT;
        RenderTileUltraManaPool.forceManaNumber = cart.getMana();
        TileEntityRendererDispatcher.instance.getRenderer(TileUltraManaPool.class).render(null,cart.posX,cart.posY,cart.posZ, ClientTickHandler.partialTicks,-1,0);
    }
}

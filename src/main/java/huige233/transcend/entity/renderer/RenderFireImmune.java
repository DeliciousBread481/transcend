package huige233.transcend.entity.renderer;

import huige233.transcend.items.EntityFireImmune;
import huige233.transcend.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import java.awt.*;

public class RenderFireImmune extends Render<EntityFireImmune> {
    private final RenderEntityItem entityItemRenderer;

    public RenderFireImmune(RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.entityItemRenderer = new RenderEntityItem(renderManagerIn, Minecraft.getMinecraft().getRenderItem());
    }

    @Override
    public void doRender(EntityFireImmune entity, double x, double y, double z, float entityYaw, float partialTicks){
        RenderUtil.renderLightRayEffects(x,y+0.4,z,new Color(0xDD, 0xDD, 0xFF),16024L,entity.age,16,20,5);

        GlStateManager.pushMatrix();
        ItemStack stack = entity.getItem();
        if(!stack.isEmpty()){
            EntityItem ei = new EntityItem(entity.world,entity.posX,entity.posY,entity.posZ,stack);
            ei.hoverStart = entity.hoverStart;
            if(RenderUtil.itemPhysics_fieldSkipRenderHook != null){
                try{
                    RenderUtil.itemPhysics_fieldSkipRenderHook.set(ei,true);
                } catch (Exception ignored){}
            }
            entityItemRenderer.doRender(ei,x,y,z,entityYaw,partialTicks);
        }
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityFireImmune fireImmune){
        return null;
    }

    public static class Factory implements IRenderFactory<EntityFireImmune>{
        @Override
        public Render<? super EntityFireImmune> createRenderFor(RenderManager manager){
            return new RenderFireImmune(manager);
        }
    }
}

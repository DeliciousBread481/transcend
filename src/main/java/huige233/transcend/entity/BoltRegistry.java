package huige233.transcend.entity;

import huige233.transcend.entity.renderer.RenderLightningRainbow;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class BoltRegistry {
    public static final void registerBolt() {
        RenderingRegistry.registerEntityRenderingHandler(EntityLightningRainbow.class, new IRenderFactory<EntityLightningRainbow>() {
            @Override
            public Render<? super EntityLightningRainbow> createRenderFor(RenderManager manager) {
                return new RenderLightningRainbow(manager);
            }
        });
    }
}

package huige233.transcend.entity;

import huige233.transcend.entity.renderer.RenderFireImmune;
import huige233.transcend.entity.renderer.RenderLightningRainbow;
import huige233.transcend.items.EntityFireImmune;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderRegistry {
    public static void RenderRegister() {
        RenderingRegistry.registerEntityRenderingHandler(EntityLightningRainbow.class, new RenderLightningRainbow.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityFireImmune.class, new RenderFireImmune.Factory());
    }
}

package huige233.transcend.util.handlers;

import huige233.transcend.effect.TimeStopEffect;
import huige233.transcend.init.TranscendPotions;
import huige233.transcend.render.RenderBlinkEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(Side.CLIENT)
public class TranscendClientEventHandler {
    private TranscendClientEventHandler(){} // No instances!

    // Tick Events
    // ===============================================================================================================

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event){

        if(event.player == Minecraft.getMinecraft().player && event.phase == TickEvent.Phase.END){

            // Reset shaders if their respective potions aren't active
            // This is a player so the potion effects are synced by vanilla
            if(Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null){ // IntelliJ is wrong, this can be null

                String activeShader = Minecraft.getMinecraft().entityRenderer.getShaderGroup().getShaderGroupName();

                if((!Minecraft.getMinecraft().player.isPotionActive(TranscendPotions.time_stop))){
                    Minecraft.getMinecraft().entityRenderer.stopUseShader();
                }
            }
        }
    }

    // The classes referenced here used to handle their own client tick events, but since they're not client-only
    // classes, that crashed the dedicated server...
    @SubscribeEvent
    public static void onClientTickEvent(TickEvent.ClientTickEvent event){

        if(event.phase == TickEvent.Phase.END && !net.minecraft.client.Minecraft.getMinecraft().isGamePaused()){

            World world = net.minecraft.client.Minecraft.getMinecraft().world;

            if(world == null) return;

            // Somehow this was throwing a CME, I have no idea why so I'm just going to cheat and copy the list
            TimeStopEffect.cleanUpEntities(world);
        }
    }


    /**
     * Renders an overlay across the entire screen.
     * @param resolution The screen resolution
     * @param texture The overlay texture to display
     */
    public static void renderScreenOverlay(ScaledResolution resolution, ResourceLocation texture){

        GlStateManager.pushMatrix();

        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(0, 						resolution.getScaledHeight(), -90).tex(0, 1).endVertex();
        buffer.pos(resolution.getScaledWidth(), resolution.getScaledHeight(), -90).tex(1, 1).endVertex();
        buffer.pos(resolution.getScaledWidth(), 0, 						  -90).tex(1, 0).endVertex();
        buffer.pos(0, 						0, 						  -90).tex(0, 0).endVertex();

        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();

        GlStateManager.popMatrix();
    }
}

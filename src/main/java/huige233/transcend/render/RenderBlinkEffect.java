package huige233.transcend.render;

import huige233.transcend.util.Reference;
import huige233.transcend.util.handlers.TranscendClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderBlinkEffect {
    private static int blinkEffectTimer;
    private static final int BLINK_EFFECT_DURATION = 8;
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID,"textures/gui/blink_overlay.png");

    public static void playBlinkEffect(){
        blinkEffectTimer = BLINK_EFFECT_DURATION;
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event){
        if(event.player == Minecraft.getMinecraft().player && event.phase == TickEvent.Phase.END){
            if(blinkEffectTimer > 0) blinkEffectTimer--;
        }
    }

    @SubscribeEvent
    public static void onFOVUpdateEvent(FOVUpdateEvent event){
        if(blinkEffectTimer > 0 ){
            float f = ((float) Math.max(blinkEffectTimer -2 ,0))/BLINK_EFFECT_DURATION;
            event.setNewfov(event.getFov() + f *f * 0.7f);
        }
    }

    @SubscribeEvent
    public static void onRenderGameOverlayEvent(RenderGameOverlayEvent.Post event){
        if(event.getType() == RenderGameOverlayEvent.ElementType.HELMET){
            if(blinkEffectTimer >0){
                float alpha = ((float)blinkEffectTimer)/BLINK_EFFECT_DURATION;

                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
                GlStateManager.color(1, 1, 1, alpha);
                GlStateManager.disableAlpha();

               TranscendClientEventHandler.renderScreenOverlay(event.getResolution(), TEXTURE);

                GlStateManager.enableAlpha();
                GlStateManager.color(1, 1, 1, 1);
            }
        }
    }
}

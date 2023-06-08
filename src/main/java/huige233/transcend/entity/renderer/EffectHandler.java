package huige233.transcend.entity.renderer;

import huige233.transcend.Transcend;
import huige233.transcend.api.Counter;
import huige233.transcend.api.IColorEffect;
import huige233.transcend.entity.EntityColorDepthParticle;
import huige233.transcend.entity.EntityColorParticle;
import huige233.transcend.entity.EntityColore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;

public final class EffectHandler {
    public static final Random STATIC_EFFECT_RAND = new Random();
    public static final EffectHandler instance = new EffectHandler();

    private static boolean acceptsNewParticles = true, cleanRequested = false;
    private static List<IColorEffect> toAddBuffer = new LinkedList<>();

    public static final Map<IColorEffect.RenderTarget, Map<Integer, List<IColorEffect>>> complexEffects = new HashMap<>();
    public static final List<EntityColorDepthParticle> fastRenderDepthParticles = new LinkedList<>();
    public static final List<EntityColorParticle> fastRenderParticles = new LinkedList<>();
    public static final List<EntityColorParticle> fastRenderGatewayParticles = new LinkedList<>();

    private EffectHandler() {}

    public static EffectHandler getInstance() {
        return instance;
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            acceptsNewParticles = false;
            Map<Integer, List<IColorEffect>> layeredEffects = complexEffects.get(IColorEffect.RenderTarget.OVERLAY_TEXT);
            for (int i = 0; i <= 2; i++) {
                for (IColorEffect effect : layeredEffects.get(i)) {
                    GL11.glPushMatrix();
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    effect.render(event.getPartialTicks());
                    GL11.glPopAttrib();
                    GL11.glPopMatrix();
                }
            }
            acceptsNewParticles = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRender(RenderWorldLastEvent event) {
        float pTicks = event.getPartialTicks();
        acceptsNewParticles = false;
        GlStateManager.disableDepth();
        EntityColorParticle.renderFast(pTicks, fastRenderDepthParticles);

        GlStateManager.enableDepth();
        EntityColorParticle.renderFast(pTicks, fastRenderParticles);

        Map<Integer, List<IColorEffect>> layeredEffects = complexEffects.get(IColorEffect.RenderTarget.RENDERLOOP);
        for (int i = 0; i <= 2; i++) {
            for (IColorEffect effect : layeredEffects.get(i)) {
                effect.render(pTicks);
            }
        }
        EntityColorParticle.renderFast(pTicks, fastRenderGatewayParticles);
        acceptsNewParticles = true;
    }

    @SubscribeEvent
    public void onClTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        tick();

        /*if(Minecraft.getMinecraft().player == null) return;
        if(ClientScheduler.getClientTick() % 10 != 0) return;
        ItemStack main = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
        if(main != null && main.getItem() instanceof ItemIlluminationWand) {
            RayTraceResult res = MiscUtils.rayTraceLook(Minecraft.getMinecraft().player, 60);
            if(res != null && res.typeOfHit == RayTraceResult.Type.BLOCK) {
                EffectLightning.buildAndRegisterLightning(new Vector3(res.getBlockPos()).addY(7), new Vector3(res.getBlockPos()));
            }
        }*/
    }

    public EntityColore registerFX(EntityColore entityComplexFX) {
        register(entityComplexFX);
        return entityComplexFX;
    }

    private void register(final IColorEffect effect) {
        if(effect == null || Minecraft.getMinecraft().isGamePaused()) return;

        if(!Thread.currentThread().getName().contains("Client thread")){
            Transcend.proxy.scheduleClientside(() -> register(effect));
        }

        if(acceptsNewParticles) {
            registerUnsafe(effect);
        } else {
            toAddBuffer.add(effect);
        }
    }

    private void registerUnsafe(IColorEffect effect) {
        if(!mayAcceptParticle(effect)) return;
        if(effect instanceof EntityColorDepthParticle) {
            fastRenderDepthParticles.add((EntityColorDepthParticle) effect);
        } else if(effect instanceof EntityColorParticle) {
            fastRenderParticles.add((EntityColorParticle) effect);
        }else {
            complexEffects.get(effect.getRenderTarget()).get(effect.getLayer()).add(effect);
        }
        effect.clearRemoveFlag();
    }

    public void tick() {
        if(cleanRequested) {
            for (IColorEffect.RenderTarget t : IColorEffect.RenderTarget.values()) {
                for (int i = 0; i <= 2; i++) {
                    List<IColorEffect> effects = complexEffects.get(t).get(i);
                    effects.forEach(IColorEffect::flagAsRemoved);
                    effects.clear();
                }
            }
            fastRenderParticles.clear();
            toAddBuffer.clear();
            cleanRequested = false;
        }

        if(Minecraft.getMinecraft().player == null) {
            return;
        }

        acceptsNewParticles = false;
        for (IColorEffect.RenderTarget target : complexEffects.keySet()) {
            Map<Integer, List<IColorEffect>> layeredEffects = complexEffects.get(target);
            for (int i = 0; i <= 2; i++) {
                Iterator<IColorEffect> iterator = layeredEffects.get(i).iterator();
                while (iterator.hasNext()) {
                    IColorEffect effect = iterator.next();
                    effect.tick();
                    if(effect.canRemove()) {
                        effect.flagAsRemoved();
                        iterator.remove();
                    }
                }
            }
        }
        for (EntityColorParticle effect : new ArrayList<>(fastRenderParticles)) {
            if (effect == null) {
                fastRenderParticles.remove(null);
                continue;
            }
            effect.tick();
            if (effect.canRemove()) {
                effect.flagAsRemoved();
                fastRenderParticles.remove(effect);
            }
        }
        for (EntityColorParticle effect : new ArrayList<>(fastRenderDepthParticles)) {
            if (effect == null) {
                fastRenderDepthParticles.remove(null);
                continue;
            }
            effect.tick();
            if (effect.canRemove()) {
                effect.flagAsRemoved();
                fastRenderDepthParticles.remove(effect);
            }
        }
        for (EntityColorParticle effect : new ArrayList<>(fastRenderGatewayParticles)) {
            if (effect == null) {
                fastRenderGatewayParticles.remove(null);
                continue;
            }
            effect.tick();
            if (effect.canRemove()) {
                effect.flagAsRemoved();
                fastRenderGatewayParticles.remove(effect);
            }
        }
        acceptsNewParticles = true;
        List<IColorEffect> effects = new LinkedList<>(toAddBuffer);
        toAddBuffer.clear();
        for (IColorEffect eff : effects) {
            registerUnsafe(eff);
        }
    }

    public static boolean mayAcceptParticle(IColorEffect effect) {
        int cfg = 2;
        if(!Minecraft.isFancyGraphicsEnabled()) {
            cfg = 1;
        }
        if(effect instanceof IColorEffect.PreventRemoval || cfg == 2) return true;
        return STATIC_EFFECT_RAND.nextInt(3) == 0;
    }

    static {
        for (IColorEffect.RenderTarget target : IColorEffect.RenderTarget.values()) {
            Map<Integer, List<IColorEffect>> layeredEffects = new HashMap<>();
            for (int i = 0; i <= 2; i++) {
                layeredEffects.put(i, new LinkedList<>());
            }
            complexEffects.put(target, layeredEffects);
        }
    }

    public static void cleanUp() {
        cleanRequested = true;
    }
}

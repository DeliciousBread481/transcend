package huige233.transcend.entity;

import huige233.transcend.Transcend;
import huige233.transcend.entity.renderer.Blending;
import huige233.transcend.util.Reference;
import huige233.transcend.util.RenderUtil;
import huige233.transcend.util.Vector.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EntityColorParticle extends EntityColore {

    //    public static final BindableResource staticFlareTex = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flarestatic");
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/flarestatic.png");
    //todo 自己记得往里塞材质

    private double x, y, z;
    private double oldX, oldY, oldZ;
    private double yGravity = 0.004;
    private float scale = 1F;

    private AlphaFunction fadeFunction = AlphaFunction.CONSTANT;
    private ScaleFunction scaleFunction = ScaleFunction.IDENTITY;
    private RenderOffsetController renderOffsetController = null;
    private boolean distanceRemovable = true;
    private float alphaMultiplier = 1F;
    private float colorRed = 1F, colorGreen = 1F, colorBlue = 1F;
    private double motionX = 0, motionY = 0, motionZ = 0;

    public EntityColorParticle(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.oldX = x;
        this.oldY = y;
        this.oldZ = z;
    }

    public EntityColorParticle updatePosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public EntityColorParticle offset(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public EntityColorParticle setScaleFunction(@Nonnull ScaleFunction scaleFunction) {
        this.scaleFunction = scaleFunction;
        return this;
    }

    public EntityColorParticle enableAlphaFade(@Nonnull AlphaFunction function) {
        this.fadeFunction = function;
        return this;
    }

    public EntityColorParticle setRenderOffsetController(RenderOffsetController renderOffsetController) {
        this.renderOffsetController = renderOffsetController;
        return this;
    }

    public EntityColorParticle motion(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        return this;
    }

    public EntityColorParticle gravity(double yGravity) {
        this.yGravity -= yGravity;
        return this;
    }

    public EntityColorParticle scale(float scale) {
        this.scale = scale;
        return this;
    }

    public EntityColorParticle setAlphaMultiplier(float alphaMul) {
        alphaMultiplier = alphaMul;
        return this;
    }

    public EntityColorParticle setColor(Color color) {
        colorRed = ((float) color.getRed()) / 255F;
        colorGreen = ((float) color.getGreen()) / 255F;
        colorBlue = ((float) color.getBlue()) / 255F;
        return this;
    }

    public EntityColorParticle setDistanceRemovable(boolean distanceRemovable) {
        this.distanceRemovable = distanceRemovable;
        return this;
    }

    public Vector3 getPosition() {
        return new Vector3(x, y, z);
    }

    public boolean isDistanceRemovable() {
        return distanceRemovable;
    }

    @Override
    public void tick() {
        super.tick();
        oldX = x;
        oldY = y;
        oldZ = z;
        x += motionX;
        y += (motionY - yGravity);
        z += motionZ;
    }

    public static <T extends EntityColorParticle> void renderFast(float parTicks, List<T> particles) {
//        GlStateManager.pushMatrix();
//        GlStateManager.scale(10, 10, 10);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);

        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

        Tessellator t = Tessellator.getInstance();
        BufferBuilder vb = t.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        for (T particle : new ArrayList<>(particles)) {
            if (particle == null) continue;
            particle.renderFast(parTicks, vb);
        }

        t.draw();

        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
//        GlStateManager.popMatrix();
    }

    public void renderFast(float pTicks, BufferBuilder vbDrawing) {
        float alpha = fadeFunction.getAlpha(age, maxAge);
        alpha *= alphaMultiplier;
        double intX = interpolate(oldX, x, pTicks);
        double intY = interpolate(oldY, y, pTicks);
        double intZ = interpolate(oldZ, z, pTicks);
        if (renderOffsetController != null) {
            Vector3 result = renderOffsetController.changeRenderPosition(this, new Vector3(intX, intY, intZ), new Vector3(motionX, motionY - yGravity, motionZ), pTicks);
            intX = result.getX();
            intY = result.getY();
            intZ = result.getZ();
        }
        float fScale = scale;
        fScale = scaleFunction.getScale(this, new Vector3(intX, intY, intZ), fScale, pTicks / 10.0f);
        RenderUtil.renderFacingFullQuadVB(vbDrawing, intX, intY, intZ, pTicks, fScale, 0, colorRed, colorGreen, colorBlue, alpha);
    }

    @Override
    public void render(float pTicks) {
//        GlStateManager.pushMatrix();
//        GlStateManager.scale(10,10,10);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        float alpha = fadeFunction.getAlpha(age, maxAge);
        alpha *= alphaMultiplier;
        GlStateManager.color(colorRed, colorGreen, colorBlue, alpha);
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        RenderUtil.renderFacingQuad(
                interpolate(oldX, x, pTicks),
                interpolate(oldY, y, pTicks),
                interpolate(oldZ, z, pTicks),
                pTicks / 10f, scale, 0,
                0, 0, 1, 1);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
//        GlStateManager.popMatrix();
    }

    public static class Gateway extends EntityColorParticle {

        public Gateway(double x, double y, double z) {
            super(x, y, z);
        }

    }
}

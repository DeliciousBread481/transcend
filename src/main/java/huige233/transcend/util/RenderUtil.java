package huige233.transcend.util;


import hellfirepvp.astralsorcery.client.util.Blending;
import huige233.transcend.util.Vector.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Random;

public class RenderUtil {
    public static Field itemPhysics_fieldSkipRenderHook = null;

    public static void renderFacingFullQuadVB(BufferBuilder vb, double px, double py, double pz, float partialTicks, float scale, float angle, float colorRed, float colorGreen, float colorBlue, float alpha) {
        renderFacingQuadVB(vb, px, py, pz, partialTicks, scale, angle, 0, 0, 1, 1, colorRed, colorGreen, colorBlue, alpha);
    }

    public static void renderFacingQuadVB(BufferBuilder vb, double px, double py, double pz, float partialTicks, float scale, float angle, double u, double v, double uLength, double vLength, float colorRed, float colorGreen, float colorBlue, float alpha) {
        float arX =  ActiveRenderInfo.getRotationX();
        float arZ =  ActiveRenderInfo.getRotationZ();
        float arYZ = ActiveRenderInfo.getRotationYZ();
        float arXY = ActiveRenderInfo.getRotationXY();
        float arXZ = ActiveRenderInfo.getRotationXZ();

        Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        if(e == null) {
            e = Minecraft.getMinecraft().player;
        }
        double iPX = e.prevPosX + (e.posX - e.prevPosX) * partialTicks;
        double iPY = e.prevPosY + (e.posY - e.prevPosY) * partialTicks;
        double iPZ = e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks;

        Vector3 v1 = new Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        Vector3 v2 = new Vector3(-arX * scale + arYZ * scale,  arXZ * scale, -arZ * scale + arXY * scale);
        Vector3 v3 = new Vector3( arX * scale + arYZ * scale,  arXZ * scale,  arZ * scale + arXY * scale);
        Vector3 v4 = new Vector3( arX * scale - arYZ * scale, -arXZ * scale,  arZ * scale - arXY * scale);
        if (angle != 0.0F) {
            Vector3 pvec = new Vector3(iPX, iPY, iPZ);
            Vector3 tvec = new Vector3(px, py, pz);
            Vector3 qvec = pvec.subtract(tvec).normalize();
            Vector3.Quat q = Vector3.Quat.buildQuatFrom3DVector(qvec, angle);
            q.rotateWithMagnitude(v1);
            q.rotateWithMagnitude(v2);
            q.rotateWithMagnitude(v3);
            q.rotateWithMagnitude(v4);
        }
        vb.pos(px + v1.getX() - iPX, py + v1.getY() - iPY, pz + v1.getZ() - iPZ).tex(u + uLength,           v + vLength).color(colorRed, colorGreen, colorBlue, alpha).endVertex();
        vb.pos(px + v2.getX() - iPX, py + v2.getY() - iPY, pz + v2.getZ() - iPZ).tex(u + uLength, v).color(colorRed, colorGreen, colorBlue, alpha).endVertex();
        vb.pos(px + v3.getX() - iPX, py + v3.getY() - iPY, pz + v3.getZ() - iPZ).tex(u, v          ).color(colorRed, colorGreen, colorBlue, alpha).endVertex();
        vb.pos(px + v4.getX() - iPX, py + v4.getY() - iPY, pz + v4.getZ() - iPZ).tex(u,           v + vLength).color(colorRed, colorGreen, colorBlue, alpha).endVertex();
    }

    public static void renderFacingFullQuad(double px, double py, double pz, float partialTicks, float scale, float angle) {
        renderFacingQuad(px, py, pz, partialTicks, scale, angle, 0, 0, 1, 1);
    }

    public static void renderFacingQuad(double px, double py, double pz, float partialTicks, float scale, float angle, double u, double v, double uLength, double vLength) {
        float arX =  ActiveRenderInfo.getRotationX();
        float arZ =  ActiveRenderInfo.getRotationZ();
        float arYZ = ActiveRenderInfo.getRotationYZ();
        float arXY = ActiveRenderInfo.getRotationXY();
        float arXZ = ActiveRenderInfo.getRotationXZ();

        Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        if(e == null) {
            e = Minecraft.getMinecraft().player;
        }
        double iPX = e.prevPosX + (e.posX - e.prevPosX) * partialTicks;
        double iPY = e.prevPosY + (e.posY - e.prevPosY) * partialTicks;
        double iPZ = e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks;

        hellfirepvp.astralsorcery.common.util.data.Vector3 v1 = new hellfirepvp.astralsorcery.common.util.data.Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        hellfirepvp.astralsorcery.common.util.data.Vector3 v2 = new hellfirepvp.astralsorcery.common.util.data.Vector3(-arX * scale + arYZ * scale,  arXZ * scale, -arZ * scale + arXY * scale);
        hellfirepvp.astralsorcery.common.util.data.Vector3 v3 = new hellfirepvp.astralsorcery.common.util.data.Vector3( arX * scale + arYZ * scale,  arXZ * scale,  arZ * scale + arXY * scale);
        hellfirepvp.astralsorcery.common.util.data.Vector3 v4 = new hellfirepvp.astralsorcery.common.util.data.Vector3( arX * scale - arYZ * scale, -arXZ * scale,  arZ * scale - arXY * scale);
        if (angle != 0.0F) {
            hellfirepvp.astralsorcery.common.util.data.Vector3 pvec = new hellfirepvp.astralsorcery.common.util.data.Vector3(iPX, iPY, iPZ);
            hellfirepvp.astralsorcery.common.util.data.Vector3 tvec = new hellfirepvp.astralsorcery.common.util.data.Vector3(px, py, pz);
            hellfirepvp.astralsorcery.common.util.data.Vector3 qvec = pvec.subtract(tvec).normalize();
            hellfirepvp.astralsorcery.common.util.data.Vector3.Quat q = hellfirepvp.astralsorcery.common.util.data.Vector3.Quat.buildQuatFrom3DVector(qvec, angle);
            q.rotateWithMagnitude(v1);
            q.rotateWithMagnitude(v2);
            q.rotateWithMagnitude(v3);
            q.rotateWithMagnitude(v4);
        }
        Tessellator t = Tessellator.getInstance();
        BufferBuilder vb = t.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(px + v1.getX() - iPX, py + v1.getY() - iPY, pz + v1.getZ() - iPZ).tex(u,           v + vLength).endVertex();
        vb.pos(px + v2.getX() - iPX, py + v2.getY() - iPY, pz + v2.getZ() - iPZ).tex(u + uLength, v + vLength).endVertex();
        vb.pos(px + v3.getX() - iPX, py + v3.getY() - iPY, pz + v3.getZ() - iPZ).tex(u + uLength, v          ).endVertex();
        vb.pos(px + v4.getX() - iPX, py + v4.getY() - iPY, pz + v4.getZ() - iPZ).tex(u,           v          ).endVertex();
        t.draw();
    }

    public static void renderLightRayEffects(double x, double y, double z, Color effectColor, long seed, long continuousTick, int dstJump, int countFancy, int countNormal) {
        renderLightRayEffects(x, y, z, effectColor, seed, continuousTick, dstJump, 1, countFancy, countNormal);
    }

    public static void renderLightRayEffects(double x, double y, double z, Color effectColor, long seed, long continuousTick, int dstJump, float scale, int countFancy, int countNormal) {
        Random rand = new Random(seed);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        int fancy_count = !FMLClientHandler.instance().getClient().gameSettings.fancyGraphics ? countNormal : countFancy;

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();

        RenderHelper.disableStandardItemLighting();
        float f1 = continuousTick / 400.0F;
        float f2 = 0.4F;

        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_BLEND);
        Blending.ADDITIVE_ALPHA.applyStateManager();
        Blending.ADDITIVE_ALPHA.apply();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        for (int i = 0; i < fancy_count; i++) {
            GlStateManager.rotate(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(rand.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(rand.nextFloat() * 360.0F + f1 * 360.0F, 0.0F, 0.0F, 1.0F);
            vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            float fa = rand.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
            float f4 = rand.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
            fa /= 30.0F / (Math.min(dstJump, 10 * scale) / 10.0F);
            f4 /= 30.0F / (Math.min(dstJump, 10 * scale) / 10.0F);
            vb.pos(0, 0, 0).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), (int) (255.0F * (1.0F - f2))).endVertex();
            vb.pos(-0.7D * f4, fa,   -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos( 0.7D * f4, fa,   -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos( 0.0D,      fa,    1.0F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos(-0.7D * f4, fa,   -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            tes.draw();
        }
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        Blending.DEFAULT.applyStateManager();
        Blending.DEFAULT.apply();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        RenderHelper.enableStandardItemLighting();

        GlStateManager.popMatrix();
    }

    public static void removeStandartTranslationFromTESRMatrix(float partialTicks) {
        Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
        if(rView == null) rView = Minecraft.getMinecraft().player;
        Entity entity = rView;
        double tx = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
        double ty = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
        double tz = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);
        GlStateManager.translate(-tx, -ty, -tz);
    }

}

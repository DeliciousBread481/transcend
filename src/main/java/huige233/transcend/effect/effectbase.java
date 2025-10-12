package huige233.transcend.effect;

import huige233.transcend.util.Reference;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class effectbase extends Potion {
    private final ResourceLocation texture;

    public effectbase(String name,boolean isBadEffect, int liquidColour, ResourceLocation texture){
        super(isBadEffect, liquidColour);
        setRegistryName(new ResourceLocation(Reference.MOD_ID,name));
        this.texture = texture;
    }

    @Override
    public void performEffect(EntityLivingBase entitylivingbase, int strength){
        // Nothing here because this potion works on events.
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc){
        drawIcon(x + 6, y + 7, effect, mc);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc, float alpha){
        net.minecraft.client.renderer.GlStateManager.color(1, 1, 1, alpha);
        drawIcon(x + 3, y + 3, effect, mc);
    }

    @SideOnly(Side.CLIENT)
    protected void drawIcon(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc){
        mc.renderEngine.bindTexture(texture);
        drawTexturedRect(x, y, 0, 0, 18, 18, 18, 18);
    }

    public static void drawTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight){
        drawTexturedFlippedRect(x, y, u, v, width, height, textureWidth, textureHeight, false, false);
    }

    public static void drawTexturedFlippedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, boolean flipX, boolean flipY){

        float f = 1F / (float)textureWidth;
        float f1 = 1F / (float)textureHeight;

        int u1 = flipX ? u + width : u;
        int u2 = flipX ? u : u + width;
        int v1 = flipY ? v + height : v;
        int v2 = flipY ? v : v + height;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(org.lwjgl.opengl.GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);

        buffer.pos((double)(x), 		(double)(y + height), 0).tex((double)((float)(u1) * f), (double)((float)(v2) * f1)).endVertex();
        buffer.pos((double)(x + width), (double)(y + height), 0).tex((double)((float)(u2) * f), (double)((float)(v2) * f1)).endVertex();
        buffer.pos((double)(x + width), (double)(y), 		  0).tex((double)((float)(u2) * f), (double)((float)(v1) * f1)).endVertex();
        buffer.pos((double)(x), 		(double)(y), 		  0).tex((double)((float)(u1) * f), (double)((float)(v1) * f1)).endVertex();

        tessellator.draw();
    }

}

package huige233.transcend.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TooltipDecor {
    private static final ResourceLocation TEXTURE_TOOLTIP_BORDERS = new ResourceLocation(Reference.MOD_ID, "textures/gui/tooltip_borders.png");

    private static int currentTooltipBorderStart = 0;
    @SuppressWarnings("unused")
    private static int currentTooltipBorderEnd = 0;

    private static int shineTimer = 0;

    private static Map<Integer, List<String>> cachedPreWrapLines = new HashMap<Integer, List<String>>();

    public static void setCurrentTooltipBorderStart(int color)
    {
        currentTooltipBorderStart = color;
    }

    public static void setCurrentTooltipBorderEnd(int color)
    {
        currentTooltipBorderEnd = color;
    }

    public static void setCachedLines(List<String> lines, int index)
    {
        cachedPreWrapLines.put(index, lines);
    }

    public static void updateTimer()
    {
        if (shineTimer > 0)
        {
            shineTimer--;
        }
    }

    public static void resetTimer()
    {
        shineTimer = 50;
    }

    public static void drawShadow(int x, int y, int width, int height)
    {
        int shadowColor = 0x44000000;

        GuiUtils.drawGradientRect(390, x - 1,         y + height + 4, x + width + 4, y + height + 5, shadowColor, shadowColor);
        GuiUtils.drawGradientRect(390, x + width + 4, y - 1,          x + width + 5, y + height + 5, shadowColor, shadowColor);

        GuiUtils.drawGradientRect(390, x + width + 3, y + height + 3, x + width + 4, y + height + 4, shadowColor, shadowColor);

        GuiUtils.drawGradientRect(390, x,             y + height + 5, x + width + 5, y + height + 6, shadowColor, shadowColor);
        GuiUtils.drawGradientRect(390, x + width + 5, y,              x + width + 6, y + height + 5, shadowColor, shadowColor);
    }

    public static void drawSeparator(int x, int y, int width, int color)
    {
        GuiHelper.drawGradientRectHorizontal(402, x, y, x + width / 2, y + 1, color & 0xFFFFFF, color);
        GuiHelper.drawGradientRectHorizontal(402, x + width / 2, y, x + width, y + 1, color, color & 0xFFFFFF);
    }

    private static double lerp(double t, double a, double b)
    {
        return a + t * (b - a);
    }

    public static void drawBorder(int x, int y, int width, int height, ItemStack item, List<String> lines, FontRenderer font, int frameLevel, boolean comparison, int index) {
        if (comparison) {
            GuiUtils.drawGradientRect(400, x - 3, y - 3 + 1, x - 3 + 1, y + height + 3 - 1, currentTooltipBorderStart, currentTooltipBorderEnd);
            GuiUtils.drawGradientRect(400, x + width + 2, y - 3 + 1, x + width + 3, y + height + 3 - 1, currentTooltipBorderStart, currentTooltipBorderEnd);
            GuiUtils.drawGradientRect(400, x - 3, y - 3, x + width + 3, y - 3 + 1, currentTooltipBorderStart, currentTooltipBorderStart);
            GuiUtils.drawGradientRect(400, x - 3, y + height + 2, x + width + 3, y + height + 3, currentTooltipBorderEnd, currentTooltipBorderEnd);

            drawSeparator(x - 3 + 1, y - 3 + 1 + 10, width, currentTooltipBorderStart);
        }
        if(item != null && !item.isEmpty()){
            String textLine = null;
            if(cachedPreWrapLines.containsKey(index)){
                textLine = cachedPreWrapLines.get(index).get(0);
            } else if (cachedPreWrapLines.containsKey(0)) {
                index = 0;
                textLine = cachedPreWrapLines.get(0).get(0);
            }

            if(textLine != null){
                List<String> wrappedLine = font.listFormattedStringToWidth(textLine, width);
                int titleLineCount = wrappedLine.size();

                if(cachedPreWrapLines.get(index).size() > titleLineCount){
                    int offset = 0;
                    if(comparison){
                        offset = 11;
                    }
                    drawSeparator(x - 3 + 1, y - 3 + 1 + (titleLineCount * 10) + 1 + offset, width, currentTooltipBorderStart);
                }
            }
        }

        if(frameLevel >= 16 || frameLevel < 0){
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(TEXTURE_TOOLTIP_BORDERS);

        int textureWidth  = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int textureHeight = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        if(shineTimer >= 10 && shineTimer <= 40){
            float interval = MathHelper.clamp((float) (shineTimer - 10) / 20, 0, 1);
            int alpha = (int) (0x99 * interval) << 24;

            int horizontalMin = x - 3;
            int horizontalMax = x + width + 3;
            int horizontalInterval = (int) lerp(interval * interval, horizontalMax, horizontalMin);
            GuiHelper.drawGradientRectHorizontal(402,Math.max(horizontalInterval - 36, horizontalMin), y - 3, Math.min(horizontalInterval, horizontalMax), y - 2, 0x00FFFFFF, 0x00FFFFFF | alpha);
            GuiHelper.drawGradientRectHorizontal(402, Math.max(horizontalInterval, horizontalMin), y - 3, Math.min(horizontalInterval + 36, horizontalMax), y - 3 + 1, 0x00FFFFFF | alpha, 0x00FFFFFF);
        }

        if(shineTimer <= 20){
            float interval = MathHelper.clamp((float) shineTimer / 20, 0f, 1f);
            int alpha = (int) (0x55 * interval) << 24;

            int verticalMin = y - 2;
            int verticalMax = y + height + 2;
            int verticalInterval = (int)lerp(interval * interval, verticalMax, verticalMin);
            GuiUtils.drawGradientRect(402, x - 3, Math.max(verticalInterval - 12, verticalMin), x - 3 + 1, Math.min(verticalInterval, verticalMax), 0x00FFFFFF, 0x00FFFFFF | alpha);
            GuiUtils.drawGradientRect(402, x - 3, Math.max(verticalInterval, verticalMin), x - 3 + 1, Math.min(verticalInterval + 12, verticalMax), 0x00FFFFFF | alpha, 0x00FFFFFF);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0,0.0,410.0);
        GlStateManager.color(1.0f,1.0f,1.0f,1.0f);

        Gui.drawModalRectWithCustomSizedTexture(x - 6, y - 6, (frameLevel / 8) * 64, (frameLevel * 16) % textureHeight, 8, 8, textureWidth, textureHeight);
        Gui.drawModalRectWithCustomSizedTexture(x + width - 8 + 6, y - 6, 56 + (frameLevel / 8) * 64, (frameLevel * 16) % textureHeight, 8, 8, textureWidth, textureHeight);
        Gui.drawModalRectWithCustomSizedTexture(x - 6, y + height - 8 + 6, (frameLevel / 8) * 64, (frameLevel * 16) % textureHeight + 8, 8, 8, textureWidth, textureHeight);
        Gui.drawModalRectWithCustomSizedTexture(x + width - 8 + 6, y + height - 8 + 6, 56 + (frameLevel / 8) * 64, (frameLevel * 16) % textureHeight + 8, 8, 8, textureWidth, textureHeight);

        if(width >= 48){
            Gui.drawModalRectWithCustomSizedTexture(x + (width / 2) - 24, y - 9, 8 + (frameLevel / 8) * 64, (frameLevel * 16) % textureHeight, 48, 8, textureWidth, textureHeight);
            Gui.drawModalRectWithCustomSizedTexture(x + (width / 2) - 24, y + height - 8 + 9, 8 + (frameLevel / 8) * 64, (frameLevel * 16) % textureHeight + 8, 48, 8, textureWidth, textureHeight);
        }
        GlStateManager.popMatrix();
    }

}

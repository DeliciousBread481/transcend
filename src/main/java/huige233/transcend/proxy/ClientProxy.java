package huige233.transcend.proxy;

import huige233.transcend.effect.TimeStopEffect;
import huige233.transcend.packet.PacketEndTimeStop;
import huige233.transcend.render.RenderBlinkEffect;
import huige233.transcend.util.TravelController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    public void registerItemRenderer( Item item, int meta, String id )
    {
        ModelLoader.setCustomModelResourceLocation( item, meta, new ModelResourceLocation( item.getRegistryName(), id ) );
    }

    @Override
    public void handleEndTimeStopPacket(PacketEndTimeStop.Message message){
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.hostID);
        if(entity instanceof EntityLivingBase) TimeStopEffect.unblockByEntities((EntityLivingBase)entity);
    }

    @Override
    public void playBlinkEffect(EntityPlayer player){
        if(Minecraft.getMinecraft().player == player) RenderBlinkEffect.playBlinkEffect();
    }

    @Override
    public String translate(String key, Style style, Object... args){
        return style.getFormattingCode() + I18n.format(key, args);
    }

    @Override
    public void loadShader(EntityPlayer player, ResourceLocation shader){
        if(Minecraft.getMinecraft().player == player && !Minecraft.getMinecraft().entityRenderer.isShaderActive())
            Minecraft.getMinecraft().entityRenderer.loadShader(shader);
    }

    @Override
    public long getTickCount() {
        return clientTickCount;
    }

    public void preInit( FMLPreInitializationEvent event )
    {
        super.preInit(event);
    }

    public void init( FMLInitializationEvent event )
    {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(TravelController.instance);
    }
}

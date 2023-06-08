package huige233.transcend.proxy;

import huige233.transcend.api.ClientScheduler;
import huige233.transcend.api.TickManager;
import huige233.transcend.effect.TimeStopEffect;
import huige233.transcend.entity.EntityLightningRainbow;
import huige233.transcend.entity.RenderRegistry;
import huige233.transcend.entity.renderer.EffectHandler;
import huige233.transcend.entity.renderer.RenderFireImmune;
import huige233.transcend.entity.renderer.RenderLightningRainbow;
import huige233.transcend.items.EntityFireImmune;
import huige233.transcend.lib.HeartRenderHandler;
import huige233.transcend.packet.PacketEndTimeStop;
import huige233.transcend.render.RenderBlinkEffect;
import huige233.transcend.tileEntity.tesr.RenderTileUltraManaPool;
import huige233.transcend.tileEntity.TileUltraManaPool;
import huige233.transcend.util.ItemBladeUtils;
import huige233.transcend.util.other.IHUDRenderable;
import huige233.transcend.util.other.TravelController;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.Style;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    private final ClientScheduler scheduler = new ClientScheduler();

    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }

    @Override
    public void handleEndTimeStopPacket(PacketEndTimeStop.Message message) {
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.hostID);
        if (entity instanceof EntityLivingBase) TimeStopEffect.unblockByEntities((EntityLivingBase) entity);
    }

    @Override
    public void playBlinkEffect(EntityPlayer player) {
        if (Minecraft.getMinecraft().player == player) RenderBlinkEffect.playBlinkEffect();
    }

    @Override
    public String translate(String key, Style style, Object... args) {
        return style.getFormattingCode() + I18n.format(key, args);
    }

    @Override
    public void loadShader(EntityPlayer player, ResourceLocation shader) {
        if (Minecraft.getMinecraft().player == player && !Minecraft.getMinecraft().entityRenderer.isShaderActive())
            Minecraft.getMinecraft().entityRenderer.loadShader(shader);
    }

    @Override
    public long getTickCount() {
        return clientTickCount;
    }

    public void preInit(FMLPreInitializationEvent event) {
        if (Loader.isModLoaded("botania")) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileUltraManaPool.class, new RenderTileUltraManaPool());
        }
        super.preInit(event);
        if (Loader.isModLoaded("flammpfeil.slashblade")) {
            new ItemBladeUtils();
        }
        RenderRegistry.RenderRegister();
    }

    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(TravelController.instance);
        MinecraftForge.EVENT_BUS.register(EffectHandler.getInstance());
    }

    public void postInit(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new HeartRenderHandler());
    }

    @SubscribeEvent
    public void onDrawScreenPost(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        Profiler profiler = mc.profiler;
        ItemStack main = mc.player.getHeldItemMainhand();
        ItemStack offhand = mc.player.getHeldItemOffhand();

        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            profiler.startSection("mana_pool_hud");
            RayTraceResult pos = mc.objectMouseOver;
            if (pos != null) {
                IBlockState state = pos.typeOfHit == RayTraceResult.Type.BLOCK ? mc.world.getBlockState(pos.getBlockPos()) : null;
                Block block = state == null ? null : state.getBlock();
                TileEntity tile = pos.typeOfHit == RayTraceResult.Type.BLOCK ? mc.world.getTileEntity(pos.getBlockPos()) : null;

                if (tile instanceof IHUDRenderable) ((IHUDRenderable) tile).renderHUDPlz(mc, event.getResolution());
            }
        }
    }

    @Override
    protected void registerTickHandlers(TickManager manager) {
        super.registerTickHandlers(manager);
        manager.register(scheduler);
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityLightningRainbow.class, new RenderLightningRainbow.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityFireImmune.class, new RenderFireImmune.Factory());

    }

    @Override
    public void scheduleClientside(Runnable r, int tickDelay) {
        scheduler.addRunnable(r, tickDelay);
    }
}

package huige233.transcend.util.handlers;

import huige233.transcend.network.message.MessageTr;
import huige233.transcend.packet.PacketTJump;
import huige233.transcend.packet.PacketTravelEvent;
import huige233.transcend.util.Reference;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

public class TranscendPacketHandler {
    public static SimpleNetworkWrapper net;

    public static final @Nonnull ThreadedNetworkWrapper INSTANCE = new ThreadedNetworkWrapper(Reference.MOD_ID);
/*
    public static void initPackets(){
        net = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
    }
*/
    private static int nextPacketId = 0;

    public static int nextID() {
        return nextPacketId++;
    }

    public static void init(FMLInitializationEvent event){
        INSTANCE.registerMessage(PacketTravelEvent.Handler.class,PacketTravelEvent.class,nextID(),Side.SERVER);
        INSTANCE.registerMessage(PacketTJump.class,PacketTJump.PacketJump.class,nextID(),Side.SERVER);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
            Class<? extends IMessageHandler<REQ, REPLY>> packet, Class<REQ> message){
        net.registerMessage(packet, message, nextPacketId, Side.CLIENT);
        net.registerMessage(packet, message, nextPacketId, Side.SERVER);
        nextPacketId++;
    }
}

package huige233.transcend.packet;

import huige233.transcend.Transcend;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEndTimeStop implements IMessageHandler<PacketEndTimeStop.Message, IMessage> {
    @Override
    public IMessage onMessage(Message message, MessageContext ctx) {
        if(ctx.side.isClient()){
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> Transcend.proxy.handleEndTimeStopPacket(message));
        }
        return null;
    }

    public static class Message implements IMessage {
        public int hostID;
        public Message(){}
        public Message(Entity host){
            this.hostID = host.getEntityId();
        }
        @Override
        public void fromBytes(ByteBuf buf){
            this.hostID = buf.readInt();
        }
        @Override
        public void toBytes(ByteBuf buf){
            buf.writeInt(hostID);
        }
    }
}
